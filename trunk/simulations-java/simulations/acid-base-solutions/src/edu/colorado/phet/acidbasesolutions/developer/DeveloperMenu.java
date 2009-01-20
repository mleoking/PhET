/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.common.piccolophet.TabbedPanePropertiesDialog;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    private final AcidBaseSolutionsApplication _app;
    private final JCheckBoxMenuItem _tabPropertiesItem;
    private final JCheckBoxMenuItem _particlesControlsMenuItem;
    
    private JDialog _tabPropertiesDialog;
    private JDialog _particleControlsDialog;

    public DeveloperMenu( AcidBaseSolutionsApplication app ) {
        super( "Developer" );

        _app = app;

        _tabPropertiesItem = new JCheckBoxMenuItem( "Tabbed Pane properties..." );
        add( _tabPropertiesItem );
        _tabPropertiesItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleTabProperties();
            }
        });
        
        _particlesControlsMenuItem = new JCheckBoxMenuItem( "Particle controls..." );
        add( _particlesControlsMenuItem );
        _particlesControlsMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleParticleControls();
            }
        });
    }

    private void handleTabProperties() {
        if ( _tabPropertiesItem.isSelected() ) {
            Frame owner = _app.getPhetFrame();
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
    
    private void handleParticleControls() {
        if ( _particlesControlsMenuItem.isSelected() ) {
            Frame owner = _app.getPhetFrame();
            _particleControlsDialog = new ParticleControlsDialog( owner, _app );
            _particleControlsDialog.setVisible( true );
            _particleControlsDialog.addWindowListener( new WindowAdapter() {
                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }
                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }
                private void cleanup() {
                    _particlesControlsMenuItem.setSelected( false );
                    _particleControlsDialog = null;
                }
            } );
        }
        else {
            _particleControlsDialog.dispose();
        }
    }
}
