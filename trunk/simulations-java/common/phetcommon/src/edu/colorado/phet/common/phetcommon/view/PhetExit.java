package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;

public class PhetExit {
    
    public static void exit() {
        TrackingManager.postSessionEndedMessage();
        TrackingManager.waitFor( 1000 );
        System.exit( 0 );
    }
    
    /**
     * Invoked when the Quit menu item is selected from the standard Mac OSX menubar.
     * This should not be called directly.  It is intended to be registered with a
     * com.apple.eawt.ApplicationListener.  See OSXAdapter.
     * 
     * @return true
     */
    public static boolean quitMacOSX() {
        TrackingManager.postActionPerformedMessage( TrackingMessage.MAC_OSX_QUIT_SELECTED );
        TrackingManager.postSessionEndedMessage();
        TrackingManager.waitFor( 1000 );
        return true;
    }
}
