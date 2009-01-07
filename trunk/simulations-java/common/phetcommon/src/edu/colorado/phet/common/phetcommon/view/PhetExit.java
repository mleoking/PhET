package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;

/**
 * PhetExit encapsulates the various ways of exiting a sim.
 *
 * @author Sam Reid
 */
public class PhetExit {
    
    public static void exit() {
        TrackingManager.postSessionEndedMessage();
        TrackingManager.waitFor( 1000 );
        System.exit( 0 );
    }
}
