/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * RoundGradientPaint creates a round, or radial, gradient.
 * This gradient defines a color at a point; the gradient blends into another 
 * color as a function of the distance from that point. 
 * The end result is a big, fuzzy spot.
 * <p>
 * Adapted from an example in Chapter 4 of "Java 2D Graphics" by Jonathan Knudsen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RoundGradientPaint implements Paint {

    private Point2D _point;
    private Point2D _radius;
    private Color _pointColor, _backgroundColor;

    /**
     * Constructor accepts a point and a color that describe the center of 
     * the gradient, a radius, and a background color. The gradient blends
     * color from the center point to the background color over the length
     * of the radius.
     * 
     * @param x
     * @param y
     * @param pointColor
     * @param radius
     * @param backgroundColor
     */
    public RoundGradientPaint( double x, double y, Color pointColor, Point2D radius, Color backgroundColor ) {
        if ( radius.distance( 0, 0 ) <= 0 )
            throw new IllegalArgumentException( "Radius must be greater than 0." );
        _point = new Point2D.Double( x, y );
        _pointColor = pointColor;
        _radius = radius;
        _backgroundColor = backgroundColor;
    }

    /**
     * See Paint.createContext
     */
    public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints ) {
        Point2D transformedPoint = xform.transform( _point, null );
        Point2D transformedRadius = xform.deltaTransform( _radius, null );
        return new RoundGradientContext( transformedPoint, _pointColor, transformedRadius, _backgroundColor );
    }

    /**
     * See Transparency.getTransparency
     */
    public int getTransparency() {
        int a1 = _pointColor.getAlpha();
        int a2 = _backgroundColor.getAlpha();
        return ( ( ( a1 & a2 ) == 0xff ) ? OPAQUE : TRANSLUCENT );
    }

    /**
     * RoundGradientContext is the PaintContext used by a RoundGradientPaint.
     */
    private static class RoundGradientContext implements PaintContext {

        private Point2D _point;
        private Point2D _radius;
        private Color _color1, _color2;

        public RoundGradientContext( Point2D p, Color color1, Point2D r, Color color2 ) {
            _point = p;
            _color1 = color1;
            _radius = r;
            _color2 = color2;
        }

        public void dispose() {}

        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        public Raster getRaster( int x, int y, int w, int h ) {
            WritableRaster raster = getColorModel().createCompatibleWritableRaster( w, h );

            int[] data = new int[w * h * 4];
            for ( int j = 0; j < h; j++ ) {
                for ( int i = 0; i < w; i++ ) {
                    double distance = _point.distance( x + i, y + j );
                    double radius = _radius.distance( 0, 0 );
                    double ratio = distance / radius;
                    if ( ratio > 1.0 )
                        ratio = 1.0;

                    int base = ( j * w + i ) * 4;
                    data[base + 0] = (int) ( _color1.getRed() + ratio * ( _color2.getRed() - _color1.getRed() ) );
                    data[base + 1] = (int) ( _color1.getGreen() + ratio * ( _color2.getGreen() - _color1.getGreen() ) );
                    data[base + 2] = (int) ( _color1.getBlue() + ratio * ( _color2.getBlue() - _color1.getBlue() ) );
                    data[base + 3] = (int) ( _color1.getAlpha() + ratio * ( _color2.getAlpha() - _color1.getAlpha() ) );
                }
            }
            raster.setPixels( 0, 0, w, h, data );

            return raster;
        }
    }
}
