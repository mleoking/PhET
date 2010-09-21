/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.translationutility.TUStrings;

/**
 * MenuBar is the menu bar for the main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MenuBar extends JMenuBar {

    public MenuBar( final MainFrame frame ) {
        
        // File menu
        JMenu fileMenu = new JMenu( TUStrings.FILE_MENU );
        fileMenu.setMnemonic( TUStrings.FILE_MENU_MNEMONIC );
        add( fileMenu );
        
        // File>Exit menu item
        JMenuItem exitMenuItem = new JMenuItem( TUStrings.EXIT_MENU_ITEM, TUStrings.EXIT_MENU_ITEM_MNEMONIC );
        fileMenu.add( exitMenuItem );
        exitMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( frame.hasUnsavedChanges() ) {
                    String message = HTMLUtils.toHTMLString( TUStrings.UNSAVED_CHANGES_MESSAGE + "<br><br>" + TUStrings.CONFIRM_EXIT );
                    int response = JOptionPane.showConfirmDialog( frame, message, TUStrings.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION );
                    if ( response != JOptionPane.YES_OPTION ) {
                        return;
                    }
                }
                System.exit( 0 );
            }
        } );
    }
}
