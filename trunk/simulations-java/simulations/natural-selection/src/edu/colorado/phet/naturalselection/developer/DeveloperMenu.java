/* Copyright 2007, University of Colorado */

package edu.colorado.phet.naturalselection.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedPanePropertiesDialog;
import edu.colorado.phet.naturalselection.SimTemplateApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    private SimTemplateApplication _app;
    private JCheckBoxMenuItem _developerControlsItem;
    private JCheckBoxMenuItem _tabPropertiesItem;
    private JDialog _developerControlsDialog;
    private JDialog _tabPropertiesDialog;

    public DeveloperMenu( SimTemplateApplication app ) {
        super( "Developer" );

        _app = app;

        _developerControlsItem = new JCheckBoxMenuItem( "Developer Controls..." );
        add( _developerControlsItem );
        _developerControlsItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDeveloperControls();
            }
        });
        
        _tabPropertiesItem = new JCheckBoxMenuItem( "Tabbed Pane properties..." );
        add( _tabPropertiesItem );
        _tabPropertiesItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleTabProperties();
            }
        });
    }

    private void handleDeveloperControls() {
        if ( _developerControlsItem.isSelected() ) {
            Frame owner = PhetApplication.instance().getPhetFrame();
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
    
    private void handleTabProperties() {
        if ( _tabPropertiesItem.isSelected() ) {
            Frame owner = PhetApplication.instance().getPhetFrame();
            _tabPropertiesDialog = new TabbedPanePropertiesDialog( owner, _app.getTabbedPane() );
            _tabPropertiesDialog.setVisible( true );
            _tabPropertiesDialog.addWindowListener( new WindowAdapter() {

                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    _tabPropertiesItem.setSelected( false );
                    _tabPropertiesDialog = null;
                }
            } );
        }
        else {
            _tabPropertiesDialog.dispose();
        }
    }
}
