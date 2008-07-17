/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.phscale.PHScaleApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    private PHScaleApplication _app;
    private JCheckBoxMenuItem _particlesControlsMenuItem;
    private JCheckBoxMenuItem _liquidColorsMenuItem;
    private JDialog _particleControlsDialog;
    private JDialog _liquidColorsDialog;

    public DeveloperMenu( PHScaleApplication app ) {
        super( "Developer" );

        _app = app;

        _particlesControlsMenuItem = new JCheckBoxMenuItem( "Particle Controls..." );
        _particlesControlsMenuItem.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                handleParticleControls();
            }
        }); 
        add( _particlesControlsMenuItem );
        
        _liquidColorsMenuItem = new JCheckBoxMenuItem( "Liquid Colors..." );
        _liquidColorsMenuItem.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                handleLiquidColors();
            }
        }); 
        add( _liquidColorsMenuItem );
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
    
    private void handleLiquidColors() {
        if ( _liquidColorsMenuItem.isSelected() ) {
            Frame owner = _app.getPhetFrame();
            _liquidColorsDialog = new LiquidColorsDialog( owner );
            _liquidColorsDialog.setVisible( true );
            _liquidColorsDialog.addWindowListener( new WindowAdapter() {
                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }
                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }
                private void cleanup() {
                    _liquidColorsMenuItem.setSelected( false );
                    _liquidColorsDialog = null;
                }
            } );
        }
        else {
            _liquidColorsDialog.dispose();
        }
    }
}
