/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.developer;

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
import edu.colorado.phet.neuron.NeuronApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    private NeuronApplication app;
    private JCheckBoxMenuItem developerControlsItem;
    private JCheckBoxMenuItem tabPropertiesItem;
    private JDialog developerControlsDialog;
    private JDialog tabPropertiesDialog;

    public DeveloperMenu( NeuronApplication app ) {
        super( "Developer" );

        this.app = app;

        developerControlsItem = new JCheckBoxMenuItem( "Developer Controls..." );
        add( developerControlsItem );
        developerControlsItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDeveloperControls();
            }
        });
        
        tabPropertiesItem = new JCheckBoxMenuItem( "Tabbed Pane properties..." );
        add( tabPropertiesItem );
        tabPropertiesItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleTabProperties();
            }
        });
    }

    private void handleDeveloperControls() {
        if ( developerControlsItem.isSelected() ) {
            Frame owner = PhetApplication.getInstance().getPhetFrame();
            developerControlsDialog = new DeveloperControlsDialog( owner, app );
            developerControlsDialog.setVisible( true );
            developerControlsDialog.addWindowListener( new WindowAdapter() {

                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    developerControlsItem.setSelected( false );
                    developerControlsDialog = null;
                }
            } );
        }
        else {
            developerControlsDialog.dispose();
        }
    }
    
    private void handleTabProperties() {
        if ( tabPropertiesItem.isSelected() ) {
            Frame owner = PhetApplication.getInstance().getPhetFrame();
            tabPropertiesDialog = new TabbedPanePropertiesDialog( owner, app.getTabbedPane() );
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
