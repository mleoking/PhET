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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.BackgroundColorDialog;
import edu.colorado.phet.faraday.control.GridControlsDialog;

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
    public FaradayApplication( FaradayApplicationModel appModel ) throws IOException {
        super( appModel );
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
        JMenu optionsMenu = new JMenu( SimStrings.get( "Menubar.options" ) );
        optionsMenu.setMnemonic( 'O' );
        getPhetFrame().addMenu( optionsMenu );

        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( SimStrings.get( "Menubar.backgroundColor" ) );
        backgroundColorMenuItem.setMnemonic( 'B' );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBackgroundColorMenuItem();
            }
        } );
        optionsMenu.add( backgroundColorMenuItem );

        // Grid Controls dialog
        JMenuItem gridControlsMenuItem = new JMenuItem( SimStrings.get( "Menubar.gridControls" ) );
        gridControlsMenuItem.setMnemonic( 'G' );
        gridControlsMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleGridControlsMenuItem();
            }
        } );
        optionsMenu.add( gridControlsMenuItem );
    }
    
    /**
     * Handles the "Background Color" menu item.
     * Displays a Color dialog and changes the background of all apparatus panels.
     */
    private void handleBackgroundColorMenuItem() {
        BackgroundColorDialog dialog = new BackgroundColorDialog( this );
        dialog.show();
    }
    
    /**
     * Handles the "Grid Controls" menu item.
     * Opens a dialog that contains controls for the "compass grid".
     */
    public void handleGridControlsMenuItem() {
        GridControlsDialog dialog = new GridControlsDialog( this );
        dialog.show();
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
        {
            // Get the default locale from property javaws.locale.
            String applicationLocale = System.getProperty( "javaws.locale" );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                SimStrings.setLocale( new Locale( applicationLocale ) );
            }

            // Override default locale using "user.language=" command line argument.
            String argsKey = "user.language=";
            if( args.length > 0 && args[0].startsWith( argsKey ) ) {
                String locale = args[0].substring( argsKey.length(), args[0].length() );
                SimStrings.setLocale( new Locale( locale ) );
            }

            // Initialize simulation strings using resource bundle for the
            // locale.
            SimStrings.setStrings( FaradayConfig.LOCALIZATION_BUNDLE_BASENAME );
        }

        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "FaradayApplication.title" );
        String description = SimStrings.get( "FaradayApplication.description" );
        String version = SimStrings.get( "FaradayApplication.version" );
        int width = FaradayConfig.APP_FRAME_WIDTH;
        int height = FaradayConfig.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Create the application model.
        FaradayApplicationModel appModel = new FaradayApplicationModel( title, description, version, frameSetup );

        // Create the application.
        PhetApplication app = new FaradayApplication( appModel );
        
        // Start the application.
        app.startApplication();
    }
}