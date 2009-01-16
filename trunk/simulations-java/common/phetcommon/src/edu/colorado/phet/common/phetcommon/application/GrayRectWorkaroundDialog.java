package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * Workaround for Unfuddle #89.
 * Use this in situations where your dialog doesn't paint in a timely manner (or at all).
 */
public class GrayRectWorkaroundDialog extends JDialog {
    
    public GrayRectWorkaroundDialog() {
    }

    public GrayRectWorkaroundDialog( Frame frame ) {
        super( frame );
    }

    public GrayRectWorkaroundDialog( Dialog owner ) {
        super( owner );
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
