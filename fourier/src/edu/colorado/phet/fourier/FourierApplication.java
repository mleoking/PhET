/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import java.io.IOException;

import javax.swing.JMenu;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * FourierApplication is the main application for the PhET "Fourier Analysis" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierApplication extends PhetApplication {


    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean ENABLE_DEVELOPER_MENU = true;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     * @throws IOException
     */
    public FourierApplication( FourierApplicationModel appModel, String[] args ) throws IOException {
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
           
        // Developer menu
        if ( ENABLE_DEVELOPER_MENU ) {

            JMenu developerMenu = new JMenu( "Developer" );
            developerMenu.setMnemonic( 'v' );
            getPhetFrame().addMenu( developerMenu );

            //XXX Add menu items for Developer menu
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
        SimStrings.init( args, FourierConfig.LOCALIZATION_BUNDLE_BASENAME );
        
        // Initialize Look-&-Feel
//        PhetLookAndFeel.setLookAndFeel();
//        PhetLookAndFeel laf = new PhetLookAndFeel();
//        laf.setBackgroundColor( new Color( 120, 165, 120 ) );
//        laf.apply();
        
        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "FourierApplication.title" );
        String description = SimStrings.get( "FourierApplication.description" );
        String version = SimStrings.get( "FourierApplication.version" );
        int width = FourierConfig.APP_FRAME_WIDTH;
        int height = FourierConfig.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Create the application model.
        FourierApplicationModel appModel = new FourierApplicationModel( title, description, version, frameSetup );

        // Create the application.
        PhetApplication app = new FourierApplication( appModel, args );
        
        // Start the application.
        app.startApplication();
    }
}
