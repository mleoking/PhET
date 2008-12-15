package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.tracking.ActionPerformedMessage;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * PhetExit encapsulates the various ways of exiting a sim.
 * <p>
 * The quitMacOSX method is invoked via reflection, so we use the marker
 * interface IProguardKeepClass to prevent this class from being shrunk.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetExit implements IProguardKeepClass {
    
    public static void exit() {
        TrackingManager.postSessionEndedMessage();
        TrackingManager.waitFor( 1000 );
        System.exit( 0 );
    }
    
    /**
     * Invoked when the Quit menu item is selected from the standard Mac OSX menubar.
     * This should not be called directly.  It is intended to be registered with a
     * com.apple.eawt.ApplicationListener.  See OSXAdapter and Unfuddle #920.
     * 
     * @return true
     */
    public static boolean quitMacOSX() {
        TrackingManager.postActionPerformedMessage( ActionPerformedMessage.MAC_OSX_QUIT_SELECTED );
        TrackingManager.postSessionEndedMessage();
        TrackingManager.waitFor( 1000 );
        return true;
    }
}
