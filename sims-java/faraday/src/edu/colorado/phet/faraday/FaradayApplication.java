/* Copyright 2004, University of Colorado */

/*
 * CVS Info - 
 * Filename : $Source$
 * Branch : $Name$ 
 * Modified by : $Author$ 
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import java.io.IOException;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.faraday.control.menu.DeveloperMenu;
import edu.colorado.phet.faraday.control.menu.OptionsMenu;
import edu.colorado.phet.faraday.module.*;

/**
 * FaradayApplication is the main application for the PhET "Faraday's Law" simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayApplication extends PhetApplication {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param args command line arguments
     */
    public FaradayApplication( String[] args ) {
        super( args, FaradayResources.getConfig(), FaradayConstants.FRAME_SETUP );
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
        if ( FaradayConstants.DEBUG_ENABLE_DEVELOPER_MENU ) {
            DeveloperMenu developerMenu = new DeveloperMenu();
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
    public static void main( String[] args ) throws IOException {

        // Create the application.
        PhetApplication app = new FaradayApplication( args );
        
        // Start the application.
        app.startApplication();
    }
}