/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingAtomModule;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;

/**
 * RutherfordScatteringApplication is the main class for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordScatteringApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public RutherfordScatteringApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {
        addModule( new RutherfordAtomModule() );
        addModule( new PlumPuddingAtomModule() );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {
    // do nothing for this sim
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     *
     * @param args command line arguments
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main( final String[] args ) {

        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                // Initialize look-and-feel
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.initLookAndFeel();

                PhetApplicationConfig config = new PhetApplicationConfig( args, RSConstants.FRAME_SETUP, RSResources.getResourceLoader() );

                // Create the application.
                RutherfordScatteringApplication app = new RutherfordScatteringApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
