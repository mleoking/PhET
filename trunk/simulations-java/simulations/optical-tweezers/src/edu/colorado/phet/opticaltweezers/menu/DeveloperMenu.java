/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.menu;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.opticaltweezers.dialog.DeveloperControlsDialog;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains developer-only features for tuning and debugging.
 * To make this menu visible, start the program with the -dev command line arg. 
 * This menu is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu implements ActionListener {
    
    private JCheckBoxMenuItem _developerControlsItem;
    private JDialog _developerControlsDialog;
    
    public DeveloperMenu() {
        super( "Developer" );
        
        _developerControlsItem = new JCheckBoxMenuItem( "Developer Controls..." );
        add( _developerControlsItem );
        _developerControlsItem.addActionListener( this );
    }
    
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _developerControlsItem ) {
            if ( _developerControlsItem.isSelected() ) {
                Frame owner = PhetApplication.instance().getPhetFrame();
                _developerControlsDialog = new DeveloperControlsDialog( owner );
                _developerControlsDialog.show();
                _developerControlsDialog.addWindowListener( new WindowAdapter() {
                    public void windowClosed( WindowEvent e ) {
                        cleanup();
                    }
                    public void windowClosing( WindowEvent e ) {
                        cleanup();
                    }
                    private void cleanup() {
                        _developerControlsItem.setSelected( false );
                        _developerControlsDialog = null; 
                    }
                } );
            }
            else {
                _developerControlsDialog.dispose();
            }
        }
    }
}
