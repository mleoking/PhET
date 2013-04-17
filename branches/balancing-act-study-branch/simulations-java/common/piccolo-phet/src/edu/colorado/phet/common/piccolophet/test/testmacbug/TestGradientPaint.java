// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test.testmacbug;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Gradient Paint causes this program to crash on Mac OS 10.4.11 with Java 1.5.0_13.
 * The same program runs fine on Mac OS 10.5.4 with Java 1.5.0_13, so the problem
 * must be in native code on Mac OS 10.4.
 * <p/>
 * The workaround is to render with VALUE_RENDER_SPEED instead of VALUE_RENDER_QUALITY.
 */
public class TestGradientPaint {

    private static final Object VALUE_RENDER = RenderingHints.VALUE_RENDER_QUALITY; // causes a crash
//    private static final Object VALUE_RENDER = RenderingHints.VALUE_RENDER_SPEED; // works fine

    public static void main( String[] args ) {
        final GradientPaint gradientPaint = new GradientPaint( 0, 0, Color.blue, 0, 10, Color.blue );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final Rectangle rectangle = new Rectangle( 20, 20 );
        frame.setContentPane( new JPanel() {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setRenderingHint( RenderingHints.KEY_RENDERING, VALUE_RENDER );
                g2.setPaint( gradientPaint );
                g2.fill( rectangle );
            }
        } );
        frame.setSize( 200, 200 );
        frame.setVisible( true );
    }

}
