/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * TestImageReflection tests the reflection of images about the axes.
 * The canvas is divided into quadrants, and a custom node is used 
 * to draw one of the 4 quadrants. The custom node is converted to an
 * Image, and the other 3 quadrants are drawn using that image.
 * By reflecting the other 3 quadrants about the appropriate axis
 * or axes, we create an image that is symmetrical around the origin.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestImageReflection extends JFrame {

    /* 
     * NOTE! 
     * If dimensions are odd numbers, we see seams
     * and alignment problems between the quadrants.
     */
    private static final Dimension BOX_SIZE = new Dimension( 475, 475 );
    
    public static void main( String[] args ) {
        JFrame frame = new TestImageReflection();
        frame.show();
    }
    
    public TestImageReflection() {
        super( "Test Image Reflection" );
        
        PCanvas canvas = new PCanvas();
        canvas.setSize( BOX_SIZE );
        
        // Draw one quadrant of the box using a custom node
        final double w = BOX_SIZE.getWidth() / 2;
        final double h = BOX_SIZE.getHeight() / 2;
        QuadrantNode lowerRightNode = new QuadrantNode( w, h );
        lowerRightNode.setOffset( w, h );
        
        // Convert to an image
        Image image = lowerRightNode.toImage();
        
        // Draw the remaining quadrants using the image, relected about one or both axes.
        
        PImage upperRightNode = new PImage( image );
        AffineTransform upperRightTransform = new AffineTransform();
        upperRightTransform.translate( w, h );
        upperRightTransform.scale( 1, -1 ); // reflect about the horizontal axis
        upperRightNode.setTransform( upperRightTransform );
        
        PImage upperLeftNode = new PImage( image );
        AffineTransform upperLeftTransform = new AffineTransform();
        upperLeftTransform.translate( w, h );
        upperLeftTransform.scale( -1, -1 ); // reflect about both axes
        upperLeftNode.setTransform( upperLeftTransform );

        PImage lowerLeftNode = new PImage( image );
        AffineTransform lowerLeftTransform = new AffineTransform();
        lowerLeftTransform.translate( w, h );
        lowerLeftTransform.scale( -1, 1 ); // reflect about the vertical axis
        lowerLeftNode.setTransform( lowerLeftTransform );
        
        PLayer layer = canvas.getLayer();
        layer.addChild( lowerRightNode );
        layer.addChild( upperRightNode );
        layer.addChild( upperLeftNode );
        layer.addChild( lowerLeftNode );

        getContentPane().add( canvas );
        setSize( BOX_SIZE.width + 40, BOX_SIZE.height + 40 );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    /*
     * QuadrantNode draws one quadrant of the image.
     * The quadrant is itself divided into 4 quadrants,
     * each filled with a solid color.  Then we draw some
     * horizontal and vertical lines. This gives us lots of 
     * horizontal and vertical edges that we can check for alignment.
     */
    private static class QuadrantNode extends PNode {
        
        public QuadrantNode( double width, double height ) {
            setBounds( 0, 0, width, height );
        }
        
        protected void paint( PPaintContext paintContext ) {
            Graphics2D g2 = paintContext.getGraphics();
            Shape shape;
            
            // 4 colors, one in each quadrant
            {
                double x, y, w, h;

                // upper left
                x = 0;
                y = 0;
                w = getWidth() / 2;
                h = getHeight() / 2;
                shape = new Rectangle2D.Double( x, y, w, h );
                g2.setPaint( Color.RED );
                g2.fill( shape );

                // upper right
                x = getWidth() / 2;
                y = 0;
                w = getWidth() / 2;
                h = getHeight() / 2;
                shape = new Rectangle2D.Double( x, y, w, h );
                g2.setPaint( Color.GREEN );
                g2.fill( shape );

                // lower left
                x = 0;
                y = getHeight() / 2;
                w = getWidth() / 2;
                h = getHeight() / 2;
                shape = new Rectangle2D.Double( x, y, w, h );
                g2.setPaint( Color.BLUE );
                g2.fill( shape );

                // lower right
                x = getWidth() / 2;
                y = getHeight() / 2;
                w = getWidth() / 2;
                h = getHeight() / 2;
                shape = new Rectangle2D.Double( x, y, w, h );
                g2.setPaint( Color.ORANGE );
                g2.fill( shape );
            }
            
            // horizontal lines
            final int hLines = 6;
            {
                double x1, y1, x2, y2;
                double dy = getHeight() / ( hLines + 1 );
                g2.setStroke( new BasicStroke( 1f ) );
                g2.setPaint( Color.BLACK );
                for ( int i = 0; i < hLines; i++ ) {
                    x1 = 0;
                    y1 = ( i + 1 ) * dy;
                    x2 = getWidth();
                    y2 = y1;
                    shape = new Line2D.Double( x1, y1, x2, y2 );
                    g2.draw( shape );
                }
            }
            
            // vertical lines
            final int vLines = 6;
            {
                double x1, y1, x2, y2;
                double dx = getWidth() / ( vLines + 1 );
                g2.setStroke( new BasicStroke( 1f ) );
                g2.setPaint( Color.BLACK );
                for ( int i = 0; i < vLines; i++ ) {
                    x1 = ( i + 1 ) * dx;
                    y1 = 0;
                    x2 = x1;
                    y2 = getHeight();
                    shape = new Line2D.Double( x1, y1, x2, y2 );
                    g2.draw( shape );
                }
            }
        }
    }
}
 