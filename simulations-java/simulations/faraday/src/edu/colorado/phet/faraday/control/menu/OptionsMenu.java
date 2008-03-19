/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.faraday.FaradayApplication;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.dialog.BackgroundColorHandler;
import edu.colorado.phet.faraday.control.dialog.GridControlsDialog;


/**
 * OptionsMenu implements the Options menu that appears in the Faraday menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OptionsMenu extends JMenu {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FaradayApplication _application;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param application
     */
    public OptionsMenu( FaradayApplication application ) {
        
        super( FaradayStrings.MENU_OPTIONS );
        
        _application = application;
        
        setMnemonic( FaradayStrings.MNEMONIC_OPTIONS );

        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( FaradayStrings.MENU_ITEM_BACKGROUND_COLOR );
        backgroundColorMenuItem.setMnemonic( FaradayStrings.MNEMONIC_BACKGROUND_COLOR );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBackgroundColorMenuItem();
            }
        } );
        add( backgroundColorMenuItem );

        // Grid Controls dialog
        JMenuItem gridControlsMenuItem = new JMenuItem( FaradayStrings.MENU_ITEM_GRID_CONTROLS );
        gridControlsMenuItem.setMnemonic( FaradayStrings.MNEMONIC_GRID_CONTROLS );
        gridControlsMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleGridControlsMenuItem();
            }
        } );
        add( gridControlsMenuItem );
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Handles the "Background Color" menu item.
     * Displays a Color dialog and changes the background of all apparatus panels.
     */
    private void handleBackgroundColorMenuItem() {
        BackgroundColorHandler dialog = new BackgroundColorHandler( _application );
        dialog.showDialog();
    }
    
    /**
     * Handles the "Grid Controls" menu item.
     * Opens a dialog that contains controls for the "compass grid".
     */
    public void handleGridControlsMenuItem() {
        GridControlsDialog dialog = new GridControlsDialog( _application );
        dialog.setVisible( true );
    }
}
