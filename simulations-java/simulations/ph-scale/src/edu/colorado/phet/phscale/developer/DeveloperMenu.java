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

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.phscale.PHScaleApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu implements ActionListener {

    private PHScaleApplication _app;
    private JCheckBoxMenuItem _particlesControlsMenuItem;
    private JDialog _particleControlsDialog;

    public DeveloperMenu( PHScaleApplication app ) {
        super( "Developer" );

        _app = app;

        _particlesControlsMenuItem = new JCheckBoxMenuItem( "Particle Controls..." );
        add( _particlesControlsMenuItem );
        _particlesControlsMenuItem.addActionListener( this );
    }

    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _particlesControlsMenuItem ) {
            if ( _particlesControlsMenuItem.isSelected() ) {
                Frame owner = PhetApplication.instance().getPhetFrame();
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
}
