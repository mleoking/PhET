/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.PropertiesLoader;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * BSCovalentBondsApplication is the simulation titled "Double Wells and Covalent Bonds".
 * It has only the "Two Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCovalentBondsApplication extends BSAbstractApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCovalentBondsApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractApplication implementation
    //----------------------------------------------------------------------------
    
    /*
     * Initializes modules.
     */
    protected void initModules() {
        addTwoWellsModule();
        getTwoWellsModule().setHasWiggleMe( true );
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
                SimStrings.getInstance().initYoda( args, BSConstants.SIM_STRINGS_NAME );

                // Load simulation properties file
                Properties simulationProperties = PropertiesLoader.loadProperties( BSConstants.SIM_PROPERTIES_NAME );

                // Title & description
                String title = SimStrings.get( "BSCovalentBondsApplication.title" );
                String description = SimStrings.get( "BSCovalentBondsApplication.description" );

                // Version
                String version = PhetApplication.getVersionString( simulationProperties );

                // Frame setup
                int width = BSConstants.APP_FRAME_WIDTH;
                int height = BSConstants.APP_FRAME_HEIGHT;
                FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

                // Create the application.
                BSAbstractApplication app = new BSCovalentBondsApplication( args, title, description, version, frameSetup );
                app.setSimulationProperties( simulationProperties );
                
                // Start the application.
                app.startApplication();
            }
        } );
    }
}
