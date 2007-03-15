/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.PropertiesLoader;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
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
     * @param title
     * @param description
     * @param version
     * @param frameSetup
     */
    public RSApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
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

                // Initialize localization.
                SimStrings.init( args, RSConstants.SIM_STRINGS_NAME );

                // Load simulation properties file
                Properties simulationProperties = PropertiesLoader.loadProperties( RSConstants.SIM_PROPERTIES_NAME );
                
                // Title & description
                String title = SimStrings.get( "RSApplication.title" );
                String description = SimStrings.get( "RSApplication.description" );
                
                // Version
                String version = PhetApplication.getVersionString( simulationProperties );

                // Frame setup
                int width = RSConstants.APP_FRAME_SIZE.width;
                int height = RSConstants.APP_FRAME_SIZE.height;
                FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

                // Create the application.
                RSApplication app = new RSApplication( args, title, description, version, frameSetup );
                app.setSimulationProperties( simulationProperties );
                
                // Start the application.
                app.startApplication();
            }
        } );
    }
}
