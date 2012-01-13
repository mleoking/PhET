// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIdDialog;
import edu.colorado.phet.common.phetcommon.simsharing.logs.ConsoleLog;
import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog;
import edu.colorado.phet.common.phetcommon.simsharing.logs.StringLog;
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

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConfig.getConfig;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage.MessageType.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemActions.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SystemObjects.simsharingManager;
import static edu.colorado.phet.common.phetcommon.simsharing.util.WhatIsMyIPAddress.whatIsMyIPAddress;

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

    //This number should be increased when the data format changes so that a different parser must be used
    private static final int PARSER_VERSION = 2;

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
    private String machineCookie; // identifies the client machine
    private int messageCount; // number of delivered events, for cross-checking that no events were dropped

    private final ArrayList<Log> logs = new ArrayList<Log>();
    public final StringLog stringLog = new StringLog();

    // Singleton, private constructor
    private SimSharingManager( PhetApplicationConfig config ) {

        enabled = config.hasCommandLineArg( COMMAND_LINE_OPTION );
        simStartedTime = System.currentTimeMillis();
        if ( enabled ) {
            initIfEnabled( config );
        }
        logs.add( new ConsoleLog() );
        logs.add( stringLog );
        if ( getConfig( studyName ).isSendToLogFile() ) {
            logs.add( new SimSharingFileLogger( machineCookie, sessionId ) );
        }
        if ( getConfig( studyName ).isSendToServer() ) {
            logs.add( new MongoLog( machineCookie, sessionId ) );
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

        sendStartupMessage( config );

        //Look up ip address and report in a separate thread so it doesn't slow down the main thread too much
        new Thread() {
            @Override public void run() {
                sendSystemMessage( simsharingManager, ipAddressLookup, param( ipAddress, whatIsMyIPAddress() ) );
            }
        }.start();
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
            for ( Log log : logs ) {
                try {
                    log.addMessage( m );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            //Every 100 events, send an event that says how many events have been sent. This way we can check to see that no events were dropped.
            messageCount++;
            if ( messageCount % 100 == 0 && messageCount > 0 ) {
                sendSystemMessage( simsharingManager, sentEvent, param( ParameterKeys.messageCount, messageCount ) );
            }
        }
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

    public static void main( String[] args ) {
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            byte[] ipAddr = addr.getAddress();
            System.out.println( "addr.getHostAddress() = " + addr.getHostAddress() );

            // Get hostname
            String hostname = addr.getHostName();
            System.out.println( "SimSharingManager.main" );
        }
        catch ( UnknownHostException e ) {
        }
    }
}