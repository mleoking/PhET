/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * TotalEnergyBand
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TotalEnergyBand extends PImage {

    /**
     * Sole constructor.
     */
    public TotalEnergyBand() {
        super();
    }
    
    public void setSize( Dimension size ) {

        BufferedImage image = getImage( size );
        if ( image != null ) {
            setImage( image );
        }
    }
    
    
    public static BufferedImage getImage( Dimension size ) {

        BufferedImage image = null;
        
        final double w = size.getWidth();
        final double h = size.getHeight();
        
        if ( w > 0 && h > 0 ) {
 
            final double overlap = .05 * size.getHeight();
            Shape shape1 = new Rectangle2D.Double( 0, 0, w, ( h / 2 ) + overlap );
            Shape shape2 = new Rectangle2D.Double( 0, ( h / 2 ) - overlap, w, ( h / 2 ) + overlap );

            Color color1 = QTConstants.TOTAL_ENERGY_COLOR;
            Color color2 = new Color( color1.getRed(), color1.getGreen(), color1.getBlue(), 0 );

            GradientPaint gradient1 = new GradientPaint( 0f, 0f, color2, 0f, (float) h / 2f, color1 );
            GradientPaint gradient2 = new GradientPaint( 0f, (float) h / 2, color1, 0f, (float) h, color2 );

            image = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setPaint( gradient1 );
            g2.fill( shape1 );
            g2.setPaint( gradient2 );
            g2.fill( shape2 );
        }
        return image;
    }
    
    
    public static TexturePaint getTexturePaint( Dimension size ) {
        TexturePaint texture = null;
        
        BufferedImage image = getImage( size );
        if ( image != null ) {
            Rectangle2D anchor = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
            texture = new TexturePaint( image, anchor );
        }
        
        return texture;
    }

}
