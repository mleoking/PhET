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
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.common.piccolophet.TabbedPanePropertiesDialog;
import edu.colorado.phet.opticaltweezers.OTAbstractApplication;
import edu.colorado.phet.opticaltweezers.dialog.DeveloperControlsDialog;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 * This menu is not localized, since it will not be visible to the end user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu implements ActionListener {

    private OTAbstractApplication _app;
    
    private JCheckBoxMenuItem _developerControlsItem;
    private JDialog _developerControlsDialog;
    
    private JCheckBoxMenuItem tabPropertiesItem;
    private JDialog tabPropertiesDialog;

    public DeveloperMenu( OTAbstractApplication app ) {
        super( "Developer" );

        _app = app;

        _developerControlsItem = new JCheckBoxMenuItem( "Developer Controls..." );
        add( _developerControlsItem );
        _developerControlsItem.addActionListener( this );
        
        if ( app.getTabbedModulePane() instanceof PhetTabbedPane ) {
            tabPropertiesItem = new JCheckBoxMenuItem( "Tabbed Pane properties..." );
            add( tabPropertiesItem );
            tabPropertiesItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleTabProperties();
                }
            } );
        }
    }

    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _developerControlsItem ) {
            if ( _developerControlsItem.isSelected() ) {
                Frame owner = PhetApplication.getInstance().getPhetFrame();
                _developerControlsDialog = new DeveloperControlsDialog( owner, _app );
                _developerControlsDialog.setVisible( true );
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
    
    private void handleTabProperties() {
        if ( tabPropertiesItem.isSelected() ) {
            Frame owner = _app.getPhetFrame();
            tabPropertiesDialog = new TabbedPanePropertiesDialog( owner, (PhetTabbedPane)_app.getTabbedModulePane() );
            tabPropertiesDialog.setVisible( true );
            tabPropertiesDialog.addWindowListener( new WindowAdapter() {

                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    tabPropertiesItem.setSelected( false );
                    tabPropertiesDialog = null;
                }
            } );
        }
        else {
            tabPropertiesDialog.dispose();
        }
    }
}
