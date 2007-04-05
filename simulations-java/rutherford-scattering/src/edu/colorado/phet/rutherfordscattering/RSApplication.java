/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.PhetProjectConfig;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingAtomModule;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;

/**
 * RSApplication is the main class for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param args command line arguments
     * @param config
     * @param frameSetup
     */
    public RSApplication( String[] args, PhetProjectConfig config, FrameSetup frameSetup )
    {
        super( args, config, frameSetup );
        initModules();
        initMenubar( args );
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

                // Create the application.
                RSApplication app = new RSApplication( args, RSConstants.CONFIG, RSConstants.FRAME_SETUP );
                
                // Start the application.
                app.startApplication();
            }
        } );
    }
}
