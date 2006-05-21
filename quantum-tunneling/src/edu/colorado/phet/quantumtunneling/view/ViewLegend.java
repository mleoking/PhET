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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.quantumtunneling.QTConstants;

/**
 * ViewLegend creates the icons used to label the views in the control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ViewLegend {

    /* Not intended for instantiation */
    private ViewLegend() {}
    
    /**
     * Creates a color key icon by drawing a solid horizontal line.
     * 
     * @param color
     * @return Icon
     */
    public static Icon createColorKey( Color color ) {
        
        // Draw into a buffered image
        final int imageWidth = 25;
        final int imageHeight = 3;
        BufferedImage image = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB );
        
        // Draw a filled rectangle
        Graphics2D g2 = image.createGraphics();
        Rectangle2D r = new Rectangle2D.Double( 0, 0, imageWidth, imageHeight );
        g2.setPaint( color );
        g2.fill( r );
        
        // Create the icon
        Icon icon = new ImageIcon( image );
        return icon;
    }
    
    /**
     * Creates a color key icon for phase by drawing the phase color series.
     * The string "0" is to the left of the colors, and the string "2PI" 
     * is to the right of the colors.
     * <p>
     * Note that this was tweaked until it looked good, so there are lots
     * of little offset constants in the code.
     * 
     * @return Icon
     */
    public static Icon createPhaseKey() {
        
        final int phaseBandWidth = 25;
        final int phaseBandHeight = 10;
        
        // Draw into a buffered image
        final int imageWidth = phaseBandWidth + 50;
        final int imageHeight = phaseBandHeight;
        BufferedImage image = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        
        // Draw "0"...
        double x = 2;
        g2.setColor( Color.BLACK );
        g2.drawString( "0", (int)x, 10 );
        x += 12;
        
        // Draw the phase band...
        Rectangle2D r = new Rectangle2D.Double();
        for ( int i = 0; i < 360; i++ ) {
            x += phaseBandWidth / 360.0;
            r.setRect( x, 0, phaseBandWidth / 360.0, phaseBandHeight );
            Color color = Color.getHSBColor( i / 360f, 1f, 1f );
            g2.setColor( color );
            g2.fill( r );
        }
        
        // Draw "2PI"...
        g2.setColor( Color.BLACK );
        x += 5;
        g2.drawString( "2" + QTConstants.C_PI, (int)x, 10 );
        
        // Create the icon
        Icon icon = new ImageIcon( image );
        return icon;
    }
}
