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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayApplication;
import edu.colorado.phet.faraday.control.dialog.BackgroundColorDialog;
import edu.colorado.phet.faraday.control.dialog.GridControlsDialog;


/**
 * OptionsMenu implements the Options menu that appears in the Faraday menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OptionsMenu extends JMenu {
    
    private FaradayApplication _application;
    
    /**
     * Sole constructor.
     * 
     * @param application
     */
    public OptionsMenu( FaradayApplication application ) {
        
        super( SimStrings.get( "Menubar.options" ) );
        
        _application = application;
        
        setMnemonic( SimStrings.get( "Menubar.options.mnemonic" ).charAt( 0 ) );

        // Background Color menu item
        JMenuItem backgroundColorMenuItem = new JMenuItem( SimStrings.get( "Menubar.backgroundColor" ) );
        backgroundColorMenuItem.setMnemonic( SimStrings.get( "Menubar.backgroundColor.mnemonic" ).charAt( 0 ) );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBackgroundColorMenuItem();
            }
        } );
        add( backgroundColorMenuItem );

        // Grid Controls dialog
        JMenuItem gridControlsMenuItem = new JMenuItem( SimStrings.get( "Menubar.gridControls" ) );
        gridControlsMenuItem.setMnemonic( SimStrings.get( "Menubar.gridControls.mnemonic" ).charAt( 0 ) );
        gridControlsMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleGridControlsMenuItem();
            }
        } );
        add( gridControlsMenuItem );
    }

    /**
     * Handles the "Background Color" menu item.
     * Displays a Color dialog and changes the background of all apparatus panels.
     */
    private void handleBackgroundColorMenuItem() {
        BackgroundColorDialog dialog = new BackgroundColorDialog( _application );
        dialog.show();
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
