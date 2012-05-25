// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet;

import apple.dts.samplecode.osxadapter.OSXAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Main application class for simulations based on LWJGL for 3D display. Currently responsible for certain
 * startup behavior and handling Mac OSX close events properly.
 */
public class LWJGLPhetApplication extends PhetApplication {

    public LWJGLPhetApplication( PhetApplicationConfig config ) {
        super( config );
        registerForMacOSXEvents();
    }

    // Generic registration with the Mac OS X application menu
    // Checks the platform, then attempts to register with the Apple EAWT
    // See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
    public void registerForMacOSXEvents() {
        if ( PhetUtilities.isMacintosh() ) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods.
                // NOTE: This uses reflection and requires security privileges, so use only in signed applications.
                OSXAdapter.setQuitHandler( this, LWJGLPhetApplication.class.getDeclaredMethod( "macQuit", (Class[]) null ) );
            }
            catch( Exception e ) {
                System.err.println( "Error while loading the OSXAdapter:" );
                e.printStackTrace();
            }
        }
    }

    // General quit handler; fed to the OSXAdapter as the method to call when a system quit event occurs
    // A quit event is triggered by Cmd-Q, selecting Quit from the application or Dock menu, or logging out.
    // This will be called via reflection by OSXAdapter, so IDEs may identify this as an unused method.
    public boolean macQuit() {
        exit();
        return true; // yes, we really want to quit
    }
}
