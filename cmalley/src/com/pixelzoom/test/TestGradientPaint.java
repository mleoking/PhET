
package com.pixelzoom.test;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * TestGradientPaint demonstrates problems that occur when filling a rectangle 
 * with an acyclic GradientPaint using rendering hint KEY_RENDERING=VALUE_RENDER_QUALITY. 
 * <p>
 * This program exhibits 2 serious problems when run with Apple JVM 1.4.2_09 under OS 10.3.9.
 * You can demonstrate the two problems by commenting out the appropriate values
 * for Y1 and Y2 in the source code below.
 * <p>
 * PROBLEM 1: 
 * If the GradientPaint's y2 coordinate is inside the bounds of the rectangle,
 * then the JVM crashes.
 * <p>
 * PROBLEM 2:
 * If the GradinentPaint's y1 coordinate is inside the bounds of the rectangle,
 * then points above y1 are not colored correctly.
 * <p>
 * Both of these problem goes away if the rendering hint is not set,
 * or if the GradientPaint's y1 and y1 coordinates are at the rectangle's bounds.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version March 10, 2006
 */
public class TestGradientPaint {
    
    private static final Dimension FRAME_SIZE = new Dimension( 200, 200 );
    private static final Dimension RECTANGLE_SIZE = new Dimension( FRAME_SIZE );
    private static final Shape SHAPE = new Rectangle( RECTANGLE_SIZE );
    
    // Using these values, everything is OK.
//    private static final float Y1 = 0;
//    private static final float Y2 = RECTANGLE_SIZE.height;

    // PROBLEM 1: Using these values, the JVM crashes.
    private static final float Y1 = 0;
    private static final float Y2 = RECTANGLE_SIZE.height - 50;
    
    // PROBLEM 2: Using these values, the points above y1 are not colored correctly.
//    private static final float Y1 = 50;
//    private static final float Y2 = RECTANGLE_SIZE.height;
    
    private static final Paint PAINT = new GradientPaint( 0, Y1, Color.BLUE, 0, Y2, Color.GREEN );
    
    public static void main( String[] args ) {

        JPanel panel = new JPanel() {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );
                Graphics2D g2 = (Graphics2D) graphics;
                // Comment out this line to make PROBLEMS 1 & 2 go away.
                g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
                g2.setPaint( PAINT );
                g2.fill( SHAPE );
            }
        };

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.setSize( FRAME_SIZE );
        frame.setResizable( false );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
