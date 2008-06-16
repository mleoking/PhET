/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.colorado.phet.translationutility.TUResources;

/**
 * MenuBar is the menu bar for the main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MenuBar extends JMenuBar {

    public MenuBar() {
        
        // File menu
        JMenu fileMenu = new JMenu( TUResources.getString( "menu.file" ) );
        fileMenu.setMnemonic( TUResources.getChar( "menu.file.mnemonic", 'F' ) );
        add( fileMenu );
        
        // File>Exit menu item
        JMenuItem exitMenuItem = new JMenuItem( TUResources.getString( "menu.item.exit" ), TUResources.getChar( "menu.item.exit.mnemonic", 'x' ) );
        fileMenu.add( exitMenuItem );
        exitMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
    }
}
