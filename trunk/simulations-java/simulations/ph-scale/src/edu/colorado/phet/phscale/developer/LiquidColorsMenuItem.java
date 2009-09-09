package edu.colorado.phet.phscale.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import edu.colorado.phet.phscale.PHScaleApplication;


public class LiquidColorsMenuItem extends JCheckBoxMenuItem {
    
    private final PHScaleApplication app;
    private JDialog dialog;
    
    public LiquidColorsMenuItem( PHScaleApplication app ) {
        super( "Liquid colors..." );
        this.app = app;
        addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                handleLiquidColors();
            }
        }); 
    }

    private void handleLiquidColors() {
        if ( isSelected() ) {
            Frame owner = app.getPhetFrame();
            dialog = new LiquidColorsDialog( owner );
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
