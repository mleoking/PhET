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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.dialog.BackgroundColorDialog;
import edu.colorado.phet.faraday.control.dialog.GridControlsDialog;

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
        JMenu optionsMenu = new JMenu( SimStrings.get( "Menubar.options" ) );
        optionsMenu.setMnemonic( SimStrings.get( "Menubar.options.mnemonic").charAt(0) );
        getPhetFrame().addMenu( optionsMenu );

        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( SimStrings.get( "Menubar.backgroundColor" ) );
        backgroundColorMenuItem.setMnemonic( SimStrings.get( "Menubar.backgroundColor.mnemonic").charAt(0) );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBackgroundColorMenuItem();
            }
        } );
        optionsMenu.add( backgroundColorMenuItem );

        // Grid Controls dialog
        JMenuItem gridControlsMenuItem = new JMenuItem( SimStrings.get( "Menubar.gridControls" ) );
        gridControlsMenuItem.setMnemonic( SimStrings.get( "Menubar.gridControls.mnemonic").charAt(0) );
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
        SimStrings.init( args, FaradayConfig.LOCALIZATION_BUNDLE_BASENAME );
        
        // Initialize Look-&-Feel
//        PhetLookAndFeel.setLookAndFeel();
//        PhetLookAndFeel laf = new PhetLookAndFeel();
//        laf.setBackgroundColor( new Color( 120, 165, 120 ) );
//        laf.apply();
        
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
        PhetApplication app = new FaradayApplication( appModel, args );
        
        // Start the application.
        app.startApplication();
    }
}