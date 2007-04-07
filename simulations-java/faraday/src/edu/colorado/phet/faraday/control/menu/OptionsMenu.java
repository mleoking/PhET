/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.faraday.FaradayApplication;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.control.dialog.BackgroundColorHandler;
import edu.colorado.phet.faraday.control.dialog.GridControlsDialog;


/**
 * OptionsMenu implements the Options menu that appears in the Faraday menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
        
        super( FaradayResources.getString( "Menubar.options" ) );
        
        _application = application;
        
        setMnemonic( FaradayResources.getString( "Menubar.options.mnemonic" ).charAt( 0 ) );

        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( FaradayResources.getString( "Menubar.backgroundColor" ) );
        backgroundColorMenuItem.setMnemonic( FaradayResources.getString( "Menubar.backgroundColor.mnemonic" ).charAt( 0 ) );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBackgroundColorMenuItem();
            }
        } );
        add( backgroundColorMenuItem );

        // Grid Controls dialog
        JMenuItem gridControlsMenuItem = new JMenuItem( FaradayResources.getString( "Menubar.gridControls" ) );
        gridControlsMenuItem.setMnemonic( FaradayResources.getString( "Menubar.gridControls.mnemonic" ).charAt( 0 ) );
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
        dialog.show();
    }
}
