package edu.colorado.phet.common.phetcommon.application;

import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.preferences.DefaultTrackingPreferences;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;

public class TrackingApplicationManager {
    private PhetApplicationConfig config;

    public static TrackingApplicationManager instance;

    public TrackingApplicationManager( PhetApplicationConfig config ) {
        this.config = config;
    }

    public static void initInstance( PhetApplicationConfig config ) {
        if ( instance != null ) {
            throw new RuntimeException( "TrackingApplicationManager already inited" );
        }
        instance = new TrackingApplicationManager( config );
    }

    public static void postMessage( TrackingMessage trackingMessage ) {
        instance.postMessageImpl( trackingMessage );
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
        boolean trackingAllowed = new DefaultTrackingPreferences().isEnabled();
//        System.out.println( "trackingAllowed = " + trackingAllowed );
        return trackingAllowed;
    }

    public boolean isTrackingCommandLineOptionSet() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

    public static void postMessage( TrackingMessage.MessageType messageType ) {
        postMessage( new TrackingMessage( instance.config, messageType ) );
    }
}
