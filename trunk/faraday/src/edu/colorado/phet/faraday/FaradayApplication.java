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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.ColorDialog;
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
        
        // Menubar
        {
            // Options menu
            JMenu optionsMenu = new JMenu( SimStrings.get( "OptionsMenu.title" ) );
            optionsMenu.setMnemonic( 'O' );
            getPhetFrame().addMenu( optionsMenu );
            
            // Background Color menu item
            JMenuItem colorMenuItem = new JMenuItem( SimStrings.get( "BackgroundColor.menuItem" ) );
            colorMenuItem.setMnemonic( 'B' );
            colorMenuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleColorMenuItem();
                }
            } );
            optionsMenu.add( colorMenuItem );
            
            // Grid Controls dialog
            JMenuItem gridMenuItem = new JMenuItem( SimStrings.get( "GridControls.menuItem" ) );
            gridMenuItem.setMnemonic( 'G' );
            gridMenuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleGridControlsMenuItem();
                }
            } );
            optionsMenu.add( gridMenuItem );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the background color for all apparatus panels in all modules.
     * 
     * @param color the color
     */
    private void setAllBackgrounds( Color color ) {
        System.out.println( "background color = " + color );
        ModuleManager moduleManager = getModuleManager();
        int numberOfModules = moduleManager.numModules();
        for ( int i = 0; i < numberOfModules; i++ ) {
            moduleManager.moduleAt( i ).getApparatusPanel().setBackground( color );
            getPhetFrame().repaint();
        }
    }
   
    //----------------------------------------------------------------------------
    // Menu handlers
    //----------------------------------------------------------------------------
    
    /**
     * Handles the "Background Color" menu item.
     * Displays a Color dialog and changes the background of all apparatus panels.
     */
    private void handleColorMenuItem() {
        ColorDialog.Listener listener = new ColorDialog.Listener() {
            public void colorChanged( Color color ) {
                setAllBackgrounds( color );
            }
            public void ok( Color color ) {
                setAllBackgrounds( color );
            }
            public void cancelled( Color color ) {
                setAllBackgrounds( color );
            }  
        };
        String title = SimStrings.get( "ColorDialog.title" );
        Component parent = getPhetFrame();
        Color color = getModuleManager().getActiveModule().getApparatusPanel().getBackground();
        ColorDialog.showDialog( title, parent, color, listener );
    }
    
    /**
     * Handles the "Grid Controls" menu item.
     * Opens a dialog that contains controls for the "compass grid".
     */
    public void handleGridControlsMenuItem() {
        String title = SimStrings.get( "GridControlsDialog.title" );
        GridControlsDialog dialog = new GridControlsDialog( this, title );
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