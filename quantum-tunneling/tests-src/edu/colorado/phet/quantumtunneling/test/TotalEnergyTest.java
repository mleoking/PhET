/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * TotalEnergyTest demonstrates how to represent total energy when a wave packet's center 
 * is in a well.  About the top of the well, we want to show a gradient band that represents
 * a probability distribution of energies.  Below the top of the well, we want to show 
 * discrete eignestate energies, whose colors indicate probability.
 * <p>
 * We pull this off by drawing drawing the entire gradient band, and then creating
 * a mask that shows the full graident above the top of the well, and horizontal lines 
 * below the top of the well.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergyTest {

    private static final int W = 600; // width
    private static final int H = 300; // height
    
    private static final Dimension FRAME_SIZE = new Dimension( W, H );
    
    private static final Color CENTER_COLOR = new Color( 0, 255, 0, 255 ); // opaque green
    private static final Color EDGE_COLOR = new Color( 0, 255, 0, 0 ); // transparent green
    
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color GRIDLINES_COLOR = Color.LIGHT_GRAY;
    private static final Paint TOP_PAINT = new GradientPaint( 0, 0, EDGE_COLOR, 0, H/2, CENTER_COLOR );
    private static final Paint BOTTOM_PAINT = new GradientPaint( 0, H, EDGE_COLOR, 0, H/2, CENTER_COLOR );
    private static final Color MASK_COLOR = new Color( 255, 0, 0, 255 );  // opaque red!
    
    public static final Stroke GRIDLINES_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    
    private static final Rectangle2D BACKGROUND_RECT = new Rectangle2D.Double( 0, 0, W, H );
    private static final Rectangle2D TOP_RECT = new Rectangle2D.Double( 0, 0, W, H/2 );
    private static final Rectangle2D BOTTOM_RECT = new Rectangle2D.Double( 0, H/2, W, H/2 );
    
    public static void main( String[] args ) {

        JPanel panel = new JPanel() {
            
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                
                // Create the gradient band...
                BufferedImage bi = new BufferedImage( W, H, BufferedImage.TYPE_INT_ARGB );
                {
                    Graphics2D g2 = bi.createGraphics();
                    g2.setPaint( TOP_PAINT );
                    g2.fill( TOP_RECT );
                    g2.setPaint( BOTTOM_PAINT );
                    g2.fill( BOTTOM_RECT );
                }
                
                // Create a mask that shows the gradient at the top and discrete lines at the bottom.
                // The opaque parts of this mask determine what parts of the gradient will be visible.
                BufferedImage mask = new BufferedImage( W, H, BufferedImage.TYPE_INT_ARGB );
                {
                    Graphics2D g2 = mask.createGraphics();
                    g2.setComposite( AlphaComposite.Src );
                    g2.setPaint( MASK_COLOR );
                    g2.fill( TOP_RECT );
                    final int numberOfLines = 30;
                    for ( int i = 0; i < numberOfLines; i++ ) {
                        int y = i * H / numberOfLines;
                        g2.drawLine( 0, y, W, y );
                    }
                }
                
                // Apply the mask to the gradient band.
                // The gradient shows through the opaque part of the mask.
                BufferedImage compositeImage = new BufferedImage( W, H, BufferedImage.TYPE_INT_ARGB );
                {
                    Graphics2D g2 = compositeImage.createGraphics();
                    AffineTransform xform = new AffineTransform();
                    g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
                    g2.drawRenderedImage( bi, xform );
                    g2.setComposite( AlphaComposite.DstIn );
                    g2.drawRenderedImage( mask, xform );
                }
                
                // Draw the composite image on top of a "chart" that has vertical gridlines...
                {
                    Graphics2D g2 = (Graphics2D)g;
                    AffineTransform xform = new AffineTransform();
                    g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
                    g2.setPaint( BACKGROUND_COLOR );
                    g2.fill( BACKGROUND_RECT );
                    g2.setPaint( GRIDLINES_COLOR );
                    g2.setStroke( GRIDLINES_STROKE );
                    final int numberOfGridlines = 10;
                    for ( int i = 0; i < numberOfGridlines; i++ ) {
                        int x = i * W / numberOfGridlines;
                        g2.drawLine( x, 0, x, H );
                    }
                    g2.drawRenderedImage( compositeImage, xform );
                }
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
