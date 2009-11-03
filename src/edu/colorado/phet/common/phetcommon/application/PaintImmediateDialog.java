package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * This dialog attempts to paint itself immediately using a Timer.
 * Use this in situations where your dialog doesn't paint in a timely manner (or at all).
 * Workaround for Unfuddle #89.
 */
public class PaintImmediateDialog extends JDialog {
    
    public PaintImmediateDialog() {
    }

    public PaintImmediateDialog( Frame frame ) {
        super( frame );
    }

    public PaintImmediateDialog( Frame frame, String title ) {
        super( frame, title );
    }

    public PaintImmediateDialog( Dialog owner ) {
        super( owner );
    }

    public PaintImmediateDialog( Dialog owner, String title ) {
        super( owner, title );
    }

    public void setVisible( boolean b ) {
        final Container contentPane = getContentPane();
        Timer timer = new Timer( 60, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                paintImmediate( (JComponent) contentPane );
            }
        } );
        timer.start();
        super.setVisible( b );
    }

    private static void paintImmediate( JComponent component ) {
        component.paintImmediately( 0, 0, component.getWidth(), component.getHeight() );
    }

}
