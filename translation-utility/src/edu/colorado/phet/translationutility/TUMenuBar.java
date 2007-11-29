/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.FindDialog.FindListener;


public class TUMenuBar extends JMenuBar {

    private Frame _dialogOwner;
    private FindListener _findListener;
    private FindDialog _findDialog;
    private String _previousFindText;
    
    public TUMenuBar( Frame dialogOwner, FindListener findListener ) {
        
        _dialogOwner = dialogOwner;
        _findListener = findListener;
        
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
        
        // Edit menu
        JMenu editMenu = new JMenu( TUResources.getString( "menu.edit" ) );
        editMenu.setMnemonic( TUResources.getChar( "menu.edit.mnemonic", 'E' ) );
        add( editMenu );
        
        // Edit>Find menu item
        final JMenuItem findMenuItem = new JMenuItem( TUResources.getString( "menu.item.find" ), TUResources.getChar( "menu.item.find.mnemonic", 'F' ) );
        editMenu.add( findMenuItem );
        findMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _findDialog = new FindDialog( _dialogOwner, _previousFindText );
                _findDialog.addFindListener( _findListener );
                _findDialog.addWindowListener( new WindowAdapter() {
                    // called when the close button in the dialog's window dressing is clicked
                    public void windowClosing( WindowEvent e ) {
                        _findDialog.dispose();
                    }
                    // called by JDialog.dispose
                    public void windowClosed( WindowEvent e ) {
                        _previousFindText = _findDialog.getText();
                        _findDialog = null;
                        findMenuItem.setEnabled( true );
                    }
                } );
                SwingUtils.centerDialogInParent( _findDialog );
                _findDialog.show();
                findMenuItem.setEnabled( false );
            }
        } );
    }
}
