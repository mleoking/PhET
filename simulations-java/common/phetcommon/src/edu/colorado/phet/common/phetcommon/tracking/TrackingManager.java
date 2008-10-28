package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.DefaultTrackingPreferences;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

public class TrackingManager {
    private PhetApplicationConfig config;

    public static TrackingManager instance;

    public TrackingManager( PhetApplicationConfig config ) {
        this.config = config;
    }

    public static void initInstance( PhetApplicationConfig config ) {
        if ( instance != null ) {
            throw new RuntimeException( "TrackingManager already initialized" );
        }
        instance = new TrackingManager( config );
    }

    public static void postMessage( TrackingMessage trackingMessage ) {
        //check for tracking enabled before message construction
        // because may construction may cause java.security.AccessControlException under web start.
        if ( instance.isTrackingEnabled() ) {
            instance.postMessageImpl( trackingMessage );
        }
    }

    private void postMessageImpl( final TrackingMessage trackingInformation ) {
        if ( isTrackingEnabled() ) {
            Thread t = new Thread( new Runnable() {
                public void run() {
                    try {
                        new Tracker().postMessage( trackingInformation );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
            t.start();
        }
        else {

        }
    }

    private boolean isTrackingEnabled() {
        return isTrackingCommandLineOptionSet() && isTrackingAllowed();
    }

    private boolean isTrackingAllowed() {
        //todo: perhaps we should use PhetPreferences.isTrackingEnabled(String,String)
        return new DefaultTrackingPreferences().isEnabled();
    }

    public boolean isTrackingCommandLineOptionSet() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

    public static void postActionPerformedMessage( TrackingMessage.MessageType messageType ) {
        postMessage( new ActionPerformedMessage( createSessionID(), messageType.getName() ) );
    }

    private static SessionID createSessionID() {
        return new SessionID( instance.config );
    }

    public static void postStateChangedMessage( String name, boolean newValue ) {
        postStateChangedMessage( name, Boolean.valueOf( newValue ) );
    }

    public static void postStateChangedMessage( String name, Object newValue ) {
        postMessage( new StateChangedMessage( createSessionID(), name, null, newValue.toString() ) );
    }
}
