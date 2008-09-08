package edu.colorado.phet.common.piccolophet.test.testmacbug;

import java.awt.*;

import javax.swing.*;

/**
 * Gradient Paint causes this program to crash on Mac OS 10.4.11 with Java 1.5.0_13.
 * The same program runs fine on Mac OS 10.5.4 with Java 1.5.0_13, so the problem
 * must be in native code on Mac OS 10.4.
 */
public class TestGradientPaint {
    public static void main( String[] args ) {
        final GradientPaint gradientPaint = new GradientPaint( 0, 0, Color.blue, 0, 10, Color.blue );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final Rectangle rectangle = new Rectangle( 20, 20 );
        frame.setContentPane( new JPanel() {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );    //To change body of overridden methods use File | Settings | File Templates.
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
                g2.setPaint( gradientPaint );
                g2.fill( rectangle );
            }
        } );
        frame.setSize( 200, 200 );
        frame.setVisible( true );
    }

}
