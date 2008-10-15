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

    public void applicationStarted( PhetApplication app ) {
        if ( isTrackingEnabled() && isTrackingAllowed() ) {
            new Tracker( config ).startTracking();
        }
    }

    private boolean isTrackingAllowed() {
        //todo: perhaps we should use PhetPreferences.isTrackingEnabled(String,String)
        boolean trackingAllowed = new DefaultTrackingPreferences().isEnabled();
//        System.out.println( "trackingAllowed = " + trackingAllowed );
        return trackingAllowed;
    }

    public boolean isTrackingEnabled() {
        return Arrays.asList( config.getCommandLineArgs() ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

}
