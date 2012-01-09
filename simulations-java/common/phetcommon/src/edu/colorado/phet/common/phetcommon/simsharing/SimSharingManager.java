// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.client.IActor;
import edu.colorado.phet.common.phetcommon.simsharing.client.StringActor;
import edu.colorado.phet.common.phetcommon.simsharing.client.ThreadedActor;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIdDialog;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelMessage;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemMessage;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserMessage;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage.MessageType.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemActions.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemObjects.simsharingManager;

/**
 * Central access point for sim-sharing initialization and event sending.
 * If sim-sharing is enabled, events are sent to the Console, a log, and the server (if a connection is made.)
 * If sim-sharing is disabled, all event-sending methods are no-ops.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingManager {

    // logging
    private static final Logger LOGGER = Logger.getLogger( SimSharingManager.class.getCanonicalName() );

    //This number should be increased when the data format changes so that a different parser must be used
    private static final int PARSER_VERSION = 2;

    //Flag for debugging, if this is set to false, then it won't send messages to the server, but will still print them to the console
    private static final boolean ALLOW_SERVER_CONNECTION = true;

    // Delimiter between fields. We use Tab instead of comma since it is much less common in string representation of objects.
    // Must be public for usage in the processing tools (in Scala)
    public static final String DELIMITER = "\t";

    // Command line option to enable sim-sharing
    private static final String COMMAND_LINE_OPTION = "-study";

    // Singleton
    private static SimSharingManager INSTANCE = null;

    public static final SimSharingManager getInstance() {
        assert ( INSTANCE != null ); // in case we forget to call init first
        return INSTANCE;
    }

    // Initialization, creates the singleton and sends startup events if sim-sharing is enabled.
    public static void init( final PhetApplicationConfig config ) {
        INSTANCE = new SimSharingManager( config );
    }

    // These members are always initialized.
    private final boolean enabled; //Flag indicating whether sim-sharing is enabled.
    private final long simStartedTime; //The time that the singleton was instantiated.

    // These members are initialized only if sim-sharing is enabled.
    private String studyName; // name for the study, as provided on the command line
    private String studentId; // student id, as provided by the student
    private String sessionId; // identifies the session
    private String machineCookie; // identifies the host machine
    private IActor actor; // actor for sending messages to the server
    private int messageCount; // number of delivered events, for cross-checking that no events were dropped
    private static Collection<String> queue = Collections.synchronizedCollection( new ArrayList<String>() ); // queue of events that occurred before init finished
    public Property<String> log = new Property<String>( "" ); // log events locally, as a fallback plan //TODO StringBuffer would be more efficient

    // Singleton, private constructor
    private SimSharingManager( PhetApplicationConfig config ) {
        enabled = config.hasCommandLineArg( COMMAND_LINE_OPTION );
        simStartedTime = System.currentTimeMillis();
        if ( enabled ) {
            initIfEnabled( config );
        }
    }

    // Portion of initialization that's performed only if sim-sharing is enabled.
    private void initIfEnabled( final PhetApplicationConfig config ) {
        assert ( enabled );

        studyName = config.getOptionArg( COMMAND_LINE_OPTION );
        studentId = getStudentId();
        sessionId = generateStrongId();

        // Get the machine cookie from the properties file, create one if it doesn't exist.
        SimSharingPropertiesFile propertiesFile = new SimSharingPropertiesFile();
        machineCookie = propertiesFile.getMachineCookie();
        if ( machineCookie == null ) {
            machineCookie = generateStrongId();
            propertiesFile.setMachineCookie( machineCookie );
        }

        new Thread( new Runnable() {
            public void run() {
                actor = createActor(); // Connect to the server
                sendStartupMessage( config );
                if ( actor != null ) {
                    sendConnectedMessage();
                    deliverQueue(); //Process any events that were collected while we were trying to connect to the server
                }
                else {
                    LOGGER.warning( "Unable to connect. Is the sim-sharing server running?" );
                }
            }
        } ).start();
    }


    // Gets the number of messages that have been sent.
    public int getMessageCount() {
        return messageCount;
    }

    // Events will be sent only if sim-sharing is enabled.
    public boolean isEnabled() {
        return enabled;
    }

    /*
     * Gets the name of the study.
     * This is the optional arg supplied after the "-study" program arg (eg, "-study utah").
     * Returns null if no study is specified.
     */
    public String getStudyName() {
        return studyName;
    }

    // Convenience method for sending an event performed by the system (not necessarily directly by the user).
//    public static String sendModelEvent( String object, String action, Parameter... parameters ) {
//        return getInstance().sendEvent( new ModelMessage( object, action, parameters ) );
//    }

    public static String sendSystemMessage( SystemObject object, SystemAction action, Parameter... parameters ) {
        return getInstance().sendMessage( new SystemMessage( system, object, action, parameters ) );
    }

    // Convenience method for sending an event from something the user did
    public static String sendUserMessage( UserComponent object, UserAction action, Parameter... parameters ) {
        return getInstance().sendMessage( new UserMessage( user, object, action, parameters ) );
    }

    public static String sendModelMessage( ModelObject object, ModelAction action, Parameter... parameters ) {
        return getInstance().sendMessage( new ModelMessage( model, object, action, parameters ) );
    }

    // Convenience method for sending a standardized event, when the user tries to interactive with something that's not interactive.
    public static String sendNonInteractiveUserMessage( UserComponent object, UserAction action ) {
        return SimSharingManager.sendUserMessage( object, action, new Parameter( interactive, false ) );
    }

    // Sends an event. If sim-sharing is disabled, this is a no-op.
    // Private because clients should use the send*Message methods to indicate the message type
    private String sendMessage( SimSharingMessage message ) {
        if ( enabled ) {

            // create the event string
            String timestamp = Long.toString( System.currentTimeMillis() - simStartedTime );
            String messageString = timestamp + DELIMITER + message.toString( DELIMITER );

            // send the event string
            sendToConsole( messageString );
            sendToLog( messageString );
            sendToServer( messageString );

            //Every 100 events, send an event that says how many events have been sent. This way we can check to see that no events were dropped.
            messageCount++;
            if ( messageCount % 100 == 0 && messageCount > 0 ) {
                sendSystemMessage( simsharingManager, sentEvent, param( ParameterKeys.messageCount, messageCount ) );
            }

            return messageString;
        }
        else {
            return null;
        }
    }

    // Sends an event to the console.
    private void sendToConsole( String message ) {
        assert ( enabled );
        System.out.println( message );
    }

    // Sends a message to the sim-sharing log.
    private void sendToLog( String message ) {
        assert ( enabled );
        if ( log.get().length() != 0 ) {
            log.set( log.get() + "\n" );
        }
        log.set( log.get() + message );
    }

    // Sends a message to the server, and prefixes the message with a couple of additional fields.
    private void sendToServer( String message ) {
        assert ( enabled );
        if ( actor != null ) {
            try {
                actor.tell( machineCookie + "\t" + sessionId + "\t" + message );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            catch ( Throwable t ) {
                t.printStackTrace();
            }
        }
        else {
            // Actor is initialized in a separate thread, queue any messages that occur before it is initialized.
            queue.add( message );
        }
    }

    // Gets the id entered by the student. Semantics of this id vary from study to study. If the study requires no id, then returns null.
    private String getStudentId() {
        assert ( enabled );
        SimSharingConfig simSharingConfig = SimSharingConfig.getConfig( studyName );
        String id = null;
        if ( simSharingConfig.requestId ) {
            SimSharingIdDialog dialog = new SimSharingIdDialog( null, simSharingConfig.idPrompt, simSharingConfig.idRequired );
            SwingUtils.centerWindowOnScreen( dialog );
            dialog.setVisible( true ); // dialog is modal, so this blocks until an id is entered.
            id = dialog.getId();
        }
        return id;
    }

    // Creates the connection to the server. If the connection fails, returns null.
    private IActor createActor() {
        assert ( enabled );
        IActor actor = null;
        if ( ALLOW_SERVER_CONNECTION ) {
            try {
                actor = new ThreadedActor( new StringActor() );
            }
            catch ( ClassNotFoundException e ) {
                e.printStackTrace();
            }
            catch ( EOFException e ) {
                LOGGER.warning( "Reached the end of the DataInputStream before reading 2 bytes. Is the sim-sharing server running?" );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            catch ( Throwable t ) {
                t.printStackTrace();
            }
        }
        return actor;
    }

    // Sends a message when sim-sharing has been started up.
    private void sendStartupMessage( PhetApplicationConfig config ) {
        assert ( enabled );
        sendSystemMessage( simsharingManager, started,
                           param( time, simStartedTime ),
                           param( name, config.getName() ),
                           param( version, config.getVersion().formatForAboutDialog() ),
                           param( project, config.getProjectName() ),
                           param( flavor, config.getFlavor() ),
                           param( locale, config.getLocale().toString() ),
                           param( distributionTag, config.getDistributionTag() ),
                           param( javaVersion, System.getProperty( "java.version" ) ),
                           param( osName, System.getProperty( "os.name" ) ),
                           param( osVersion, System.getProperty( "os.version" ) ),
                           param( parserVersion, PARSER_VERSION ),
                           param( study, studyName ),
                           param( id, studentId ),
                           param( ParameterKeys.machineCookie, machineCookie ) );
    }

    // Sends an event when we've connected to the sim-sharing server.
    private void sendConnectedMessage() {
        assert ( enabled && actor != null );
        sendSystemMessage( simsharingManager, connectedToServer );
    }

    //TODO fix synchronization issues #3188
    // Deliver events in the queue to the server.
    private synchronized void deliverQueue() {
        assert ( enabled );
        for ( String event : queue ) {
            sendToServer( event );
        }
        queue.clear();
    }

    //Generate a strong unique id, see http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
    private static String generateStrongId() {
        return new BigInteger( 130, new SecureRandom() ).toString( 32 );
    }

    //Uses a heuristic that converts a class name like AbstractSlider to abstractSlider for use as an object in a sim sharing event.
    public static String toInstanceName( Class c ) {
        String name = c.getName();
        final int dotIndex = name.indexOf( '.' );
        String trimmed = dotIndex >= 0 ? name.substring( dotIndex + 1 ) : name;
        return Character.toLowerCase( trimmed.charAt( 0 ) ) + trimmed.substring( 1 );
    }
}