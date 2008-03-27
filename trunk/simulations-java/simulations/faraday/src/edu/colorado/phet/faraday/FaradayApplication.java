/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday;

import java.io.IOException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.faraday.control.menu.DeveloperMenu;
import edu.colorado.phet.faraday.control.menu.OptionsMenu;
import edu.colorado.phet.faraday.module.*;

/**
 * FaradayApplication is the main application for the PhET "Faraday's Law" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final boolean _developControlsEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public FaradayApplication( PhetApplicationConfig config ) {
        super( config );
        _developControlsEnabled = CommandLineUtils.contains( config.getCommandLineArgs(), "-dev" );
        initModules();
        initMenubar();
    }

    private void initModules() {
        BarMagnetModule barMagnetModule = new BarMagnetModule();
        addModule( barMagnetModule );
        PickupCoilModule pickupCoilModule = new PickupCoilModule();
        addModule( pickupCoilModule );
        ElectromagnetModule electromagnetModule = new ElectromagnetModule();
        addModule( electromagnetModule );
        TransformerModule transformerModule = new TransformerModule();
        addModule( transformerModule );
        GeneratorModule generatorModule = new GeneratorModule();
        addModule( generatorModule );
    }

    /**
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );

        // Developer menu
        if ( _developControlsEnabled ) {
            DeveloperMenu developerMenu = new DeveloperMenu( getPhetFrame() );
            getPhetFrame().addMenu( developerMenu );
        }
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for the PhET Color Vision application.
     *
     * @param args command line arguments
     */
    public static void main( final String[] args ) throws IOException {

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
                PhetApplicationConfig config = new PhetApplicationConfig( args, FaradayConstants.FRAME_SETUP, FaradayResources.getResourceLoader() );

                // Create the application.
                NonPiccoloPhetApplication app = new FaradayApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}