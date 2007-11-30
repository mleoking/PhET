/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class TUMenuBar extends JMenuBar {

    public TUMenuBar() {
        
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
