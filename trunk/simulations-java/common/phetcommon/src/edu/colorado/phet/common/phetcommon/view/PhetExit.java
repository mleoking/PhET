package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;

public class PhetExit {
    public static void exit() {
        TrackingManager.postSessionEndedMessage();
        TrackingManager.waitFor( 1000 );
        System.exit( 0 );
    }
}
