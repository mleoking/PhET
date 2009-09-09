package edu.colorado.phet.phscale.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import edu.colorado.phet.phscale.PHScaleApplication;

/**
 * Menu item for accessing developer controls related to particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParticleControlsMenuItem extends JCheckBoxMenuItem {

    private final PHScaleApplication app;
    private JDialog dialog;
    
    public ParticleControlsMenuItem( PHScaleApplication app ) {
        super( "Particle controls..." );
        this.app = app;
        addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                handleParticleControls();
            }
        }); 
    }
    
    private void handleParticleControls() {
        if ( isSelected() ) {
            Frame owner = app.getPhetFrame();
            dialog = new ParticleControlsDialog( owner, app );
            dialog.setVisible( true );
            dialog.addWindowListener( new WindowAdapter() {
                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }
                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }
                private void cleanup() {
                    setSelected( false );
                    dialog = null;
                }
            } );
        }
        else {
            dialog.dispose();
        }
    }
}
