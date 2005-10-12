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

import java.awt.Color;
import java.io.IOException;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.menu.DeveloperMenu;
import edu.colorado.phet.faraday.control.menu.OptionsMenu;

/**
 * FaradayApplication is the main application for the PhET
 * "Faraday's Law" simulation.
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
     * @param appModel the application model
     * @throws IOException
     */
    public FaradayApplication( FaradayApplicationModel appModel, String[] args ) throws IOException {
        super( appModel, args );
        assert( appModel != null );
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------
    
    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
        
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
        
        // Developer menu
        if ( FaradayConfig.DEBUG_ENABLE_DEVELOPER_MENU ) {
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
        
        // Initialize localization.
        SimStrings.init( args, FaradayConfig.LOCALIZATION_BUNDLE_BASENAME );
        
        // Initialize Look-&-Feel
//        PhetLookAndFeel.setLookAndFeel();
//        PhetLookAndFeel laf = new PhetLookAndFeel();
//        laf.apply();
        
        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "FaradayApplication.title" );
        String description = SimStrings.get( "FaradayApplication.description" );
        String version = FaradayConfig.APP_VERSION;
        int width = FaradayConfig.APP_FRAME_WIDTH;
        int height = FaradayConfig.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Create the application model.
        FaradayApplicationModel appModel = new FaradayApplicationModel( title, description, version, frameSetup );

        // Create the application.
        PhetApplication app = new FaradayApplication( appModel, args );
        
        // Start the application.
        app.startApplication();
    }
}