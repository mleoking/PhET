// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import apple.dts.samplecode.osxadapter.OSXAdapter;

import java.awt.Component;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.ModulePanel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;

/**
 * The JME version of the PhET application. Used to override the module-panel handling, so that we
 * never hide the JME3 version.
 * <p/>
 * NOT to be confused with the PhetJMEApplication, which is the JME-application variant for PhET. Yeah.
 * <p/>
 * TODO: make it so that we only override the behavior when switching from the JME3 canvas to itself?
 */
public class JMEPhetApplication extends PiccoloPhetApplication {
    public JMEPhetApplication( PhetApplicationConfig config ) {
        this( config, new TabbedModulePanePiccolo() {
            private ModulePanel panel = null;

            // TODO: get rid of this hack!

            // don't add anything after the initial module panel
            @Override public void add( Component comp, Object constraints ) {
                if ( panel == null || !( comp instanceof ModulePanel ) ) {
                    super.add( comp, constraints );
                    if ( comp instanceof ModulePanel ) {
                        panel = (ModulePanel) comp;
                    }
                }
            }

            @Override public void remove( Component comp ) {
                // don't allow removal of our primary module panel
            }
        } );
        registerForMacOSXEvents();
    }

    public JMEPhetApplication( PhetApplicationConfig config, TabbedModulePanePiccolo tabbedModulePane ) {
        super( config, tabbedModulePane );
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
                OSXAdapter.setQuitHandler( this, JMEPhetApplication.class.getDeclaredMethod( "macQuit", (Class[]) null ) );
            }
            catch ( Exception e ) {
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
