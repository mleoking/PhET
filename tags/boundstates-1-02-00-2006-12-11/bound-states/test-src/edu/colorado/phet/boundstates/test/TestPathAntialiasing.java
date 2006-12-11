
package edu.colorado.phet.boundstates.test;

import java.awt.*;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * TestPathAntialiasing test antialiasing of a GeneralPath.
 * On Windows, a GeneralPath always seems to look lousy,
 * no matter how antialiasing is configured.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPathAntialiasing {
    
    private static final Dimension FRAME_SIZE = new Dimension( 400, 400 );
    
    /**
     * Calculates a sine value
     * 
     * @param x the value along the x-axis
     * @param halfY the value of the y-axis
     * @param maxX the width of the x-axis
     * @param cycles number of cycles to be drawn
     * @return double
     */
    private static double getNormalizedSine( double x, double halfY, double maxX, int cycles ) {
        double piDouble = 2 * Math.PI * cycles;
        double factor = piDouble / maxX;
        return (int) ( Math.sin( x * factor ) * halfY + halfY );
    }
    
    public static void main( String[] args ) {

        // Sine wave approximation
        final GeneralPath path = new GeneralPath();
        final double dx = 1;
        for ( double x = 0; x <= FRAME_SIZE.width; x += dx ) {
            final double y = getNormalizedSine( x, FRAME_SIZE.height/2, FRAME_SIZE.width, 5 /* cycles */ );
            if ( x == 0 ) {
                path.moveTo( (float)x, (float)y );
            }
            else {
                path.lineTo( (float)x, (float)y );
            }
        }
        
        JPanel panel = new JPanel() {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );
                // Draw the sine wave
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
                g2.setPaint( Color.RED );
                g2.setStroke( new BasicStroke( 1f ) );
                g2.draw( path );
            }
        };
        panel.setBackground( Color.BLACK );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.setSize( FRAME_SIZE );
        frame.setResizable( false );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
