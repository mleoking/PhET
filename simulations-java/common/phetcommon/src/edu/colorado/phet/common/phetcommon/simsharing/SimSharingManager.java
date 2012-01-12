// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIdDialog;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelMessage;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemMessage;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserMessage;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConfig.getConfig;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage.MessageType.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemActions.sentEvent;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemActions.started;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemObjects.simsharingManager;

/**
 * Central access point for sim-sharing initialization and event sending.
 * If sim-sharing is enabled, events are sent to the Console, a log, and the server (if a connection is made.)
 * If sim-sharing is disabled, all event-sending methods are no-ops.
 * <p/>
 * For instructions on how to instrument a sim, please read simulations-java\common\phetcommon\simsharing-readme.txt
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
    private Mongo mongo;

    //http://www.cs.umd.edu/class/spring2006/cmsc433/lectures/util-concurrent.pdf
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private SimSharingFileLogger simSharingFileLogger;

    //Strings for MongoDB
    public static final String TIME = "time";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String COMPONENT = "component";
    public static final String ACTION = "action";
    public static final String PARAMETERS = "parameters";

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
    private String machineCookie; // identifies the client machine
    private int messageCount; // number of delivered events, for cross-checking that no events were dropped
    public final Property<String> log = new Property<String>( "" ); // log events locally, as a fallback plan //TODO StringBuffer would be more efficient

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

        //Disables mongo logging (and maybe all other logging as well!)
        //TODO: how to disable just the mongo log from DBPort info messages?  They are sent repeatedly when server offline.
        try {
            LogManager.getLogManager().reset();
        }
        catch ( Exception e ) {
            System.out.println( "error on log reset: " + e.getMessage() );
        }

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

        try {
            mongo = new Mongo();
        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }
        simSharingFileLogger = new SimSharingFileLogger( machineCookie, sessionId );

        sendStartupMessage( config );
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

    //Convenience overload to provide no parameters
    public static void sendSystemMessage( ISystemObject object, ISystemAction action ) {
        sendSystemMessage( object, action, new ParameterSet() );
    }

    public static void sendSystemMessage( ISystemObject object, ISystemAction action, ParameterSet parameters ) {
        getInstance().sendMessage( new SystemMessage( system, object, action, parameters ) );
    }

    public void sendSystemMessageNS( ISystemObject object, ISystemAction action, ParameterSet parameters ) {
        sendMessage( new SystemMessage( system, object, action, parameters ) );
    }

    //Convenience overload to provide no parameters
    public static void sendUserMessage( IUserComponent object, IUserAction action ) {
        sendUserMessage( object, action, new ParameterSet() );
    }

    // Convenience method for sending an event from something the user did
    public static void sendUserMessage( IUserComponent object, IUserAction action, ParameterSet parameters ) {
        getInstance().sendMessage( new UserMessage( user, object, action, parameters ) );
    }

    public static void sendModelMessage( ModelObject object, IModelAction action ) {
        getInstance().sendMessage( new ModelMessage( model, object, action, new ParameterSet() ) );
    }

    public static void sendModelMessage( ModelObject object, IModelAction action, ParameterSet parameters ) {
        getInstance().sendMessage( new ModelMessage( model, object, action, parameters ) );
    }

    // Sends an event. If sim-sharing is disabled, this is a no-op.
    // Private because clients should use the send*Message methods to indicate the message type
    private void sendMessage( SimSharingMessage message ) {
        if ( enabled ) {
            AugmentedMessage m = new AugmentedMessage( message );
            sendToConsole( m );
            sendToLog( m );
            if ( getConfig( studyName ).isSendToLogFile() ) {
                try {
                    simSharingFileLogger.sendToLogFile( m );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
            sendToServer( m );

            //Every 100 events, send an event that says how many events have been sent. This way we can check to see that no events were dropped.
            messageCount++;
            if ( messageCount % 100 == 0 && messageCount > 0 ) {
                sendSystemMessage( simsharingManager, sentEvent, param( ParameterKeys.messageCount, messageCount ) );
            }
        }
    }

    // Sends an event to the console.
    private void sendToConsole( AugmentedMessage message ) {
        assert ( enabled );
        System.out.println( message );
    }

    // Sends a message to the sim-sharing log.
    private void sendToLog( AugmentedMessage message ) {
        assert ( enabled );
        if ( log.get().length() != 0 ) {
            log.set( log.get() + "\n" );
        }
        log.set( log.get() + message );
    }

    // Sends a message to the server, and prefixes the message with a couple of additional fields.
    private void sendToServer( final AugmentedMessage message ) {
        assert ( enabled );

        executor.execute( new Runnable() {
            public void run() {

                //one database per machine
                DB database = mongo.getDB( machineCookie );

                //One collection per session, lets us easily iterate and add messages per session.
                DBCollection coll = database.getCollection( sessionId );

                BasicDBObject doc = new BasicDBObject() {{
                    put( TIME, message.time + "" );
                    put( MESSAGE_TYPE, message.message.messageType.toString() );
                    put( COMPONENT, message.message.component.toString() );
                    put( ACTION, message.message.action.toString() );
                    put( PARAMETERS, new BasicDBObject() {{
                        for ( Parameter parameter : message.message.parameters ) {
//                            if ( parameter.name == null ) {
//                                System.out.println( "parameter = " + parameter );
//                            }
//                            else if ( parameter.value == null ) {
//                                System.out.println( "parameter.value = " + parameter.value );
//                            }
                            put( parameter.name.toString(), parameter.value == null ? "null" : parameter.value.toString() );
                        }
                    }} );
                }};

                try {
                    WriteResult result = coll.insert( doc );
                }

                //like new MongoException.Network( "can't say something" , ioe )
                catch ( RuntimeException e ) {
//                    System.out.println( "My error: " + e.getMessage() );
//
//                    Enumeration<String> s = LogManager.getLogManager().getLoggerNames();
//                    while ( s.hasMoreElements() ) {
//                        String s1 = s.nextElement();
//                        System.out.println( "s1 = " + s1 );
//                    }
                }
            }
        } );
    }

    // Gets the id entered by the student. Semantics of this id vary from study to study. If the study requires no id, then returns null.
    private String getStudentId() {
        assert ( enabled );
        SimSharingConfig simSharingConfig = getConfig( studyName );
        String id = null;
        if ( simSharingConfig.requestId ) {
            SimSharingIdDialog dialog = new SimSharingIdDialog( null, simSharingConfig.idPrompt, simSharingConfig.idRequired );
            SwingUtils.centerWindowOnScreen( dialog );
            dialog.setVisible( true ); // dialog is modal, so this blocks until an id is entered.
            id = dialog.getId();
        }
        return id;
    }

    // Sends a message when sim-sharing has been started up.
    private void sendStartupMessage( PhetApplicationConfig config ) {
        assert ( enabled );
        sendSystemMessageNS( simsharingManager, started, param( time, simStartedTime ).
                param( name, config.getName() ).
                param( version, config.getVersion().formatForAboutDialog() ).
                param( project, config.getProjectName() ).
                param( flavor, config.getFlavor() ).
                param( locale, config.getLocale().toString() ).
                param( distributionTag, config.getDistributionTag() ).
                param( javaVersion, System.getProperty( "java.version" ) ).
                param( osName, System.getProperty( "os.name" ) ).
                param( osVersion, System.getProperty( "os.version" ) ).
                param( parserVersion, PARSER_VERSION ).
                param( study, studyName ).
                param( id, studentId ).
                param( ParameterKeys.machineCookie, machineCookie ).
                param( ParameterKeys.sessionId, sessionId ) );
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