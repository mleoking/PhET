package edu.colorado.phet.common.phetcommon.application;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.preferences.DefaultTrackingPreferences;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;

public class TrackingApplicationManager {
    private PhetApplicationConfig config;

    public TrackingApplicationManager( PhetApplicationConfig config ) {
        this.config = config;
    }

    public void applicationStarted() {
        if ( isTrackingEnabled() ) {
            new Tracker().postMessage( config.getTrackingInformation() );
        }
    }

    private boolean isTrackingEnabled() {
        return isTrackingCommandLineEnabled() && isTrackingAllowed();
    }

    private boolean isTrackingAllowed() {
        //todo: perhaps we should use PhetPreferences.isTrackingEnabled(String,String)
        boolean trackingAllowed = new DefaultTrackingPreferences().isEnabled();
//        System.out.println( "trackingAllowed = " + trackingAllowed );
        return trackingAllowed;
    }

    public boolean isTrackingCommandLineEnabled() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

}
