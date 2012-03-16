// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Gradient paint in which the color depends on the angle but not the radius from the center.
 * This was originally created for the face of the charge meter in Build an Atom, but may be useful elsewhere.
 * Adapted from RoundGradientPaint in phetcommon.
 * <p/>
 * NOTE! This paint is relatively expensive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class CircularGradientPaint implements Paint {

    private final Color pointColor;
    private final Color backgroundColor;
    private final Point2D center;

    /**
     * Constructor accepts a point and a color that describe the center of
     * the gradient, a radius, and a background color. The gradient blends
     * color from the center point to the background color over the length
     * of the radius.
     *
     * @param pointColor      color at the center of the gradient
     * @param backgroundColor color at the outer edges of the gradient
     */
    public CircularGradientPaint( Point2D center, Color pointColor, Color backgroundColor ) {
        this.center = center;
        this.pointColor = pointColor;
        this.backgroundColor = backgroundColor;
    }

    /**
     * See Paint.createContext
     */
    public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints ) {
        Point2D transformedPoint = xform.transform( center, null );
        return new RoundGradientContext( transformedPoint, pointColor, backgroundColor );
    }

    /**
     * See Transparency.getTransparency
     */
    public int getTransparency() {
        int a1 = pointColor.getAlpha();
        int a2 = backgroundColor.getAlpha();
        return ( ( ( a1 & a2 ) == 0xff ) ? OPAQUE : TRANSLUCENT );
    }

    /**
     * RoundGradientContext is the PaintContext used by a RoundGradientPaint.
     */
    private static class RoundGradientContext implements PaintContext {

        private final Point2D _point;
        private final Color _color1, _color2;
        private WritableRaster _raster;

        public RoundGradientContext( Point2D p, Color color1, Color color2 ) {
            _point = p;
            _color1 = color1;
            _color2 = color2;
        }

        public void dispose() {
        }

        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        public Raster getRaster( int x, int y, int w, int h ) {
            // allocate raster on demand, or if we need a bigger raster
            if ( _raster == null || w > _raster.getWidth() || h > _raster.getHeight() ) {
                _raster = getColorModel().createCompatibleWritableRaster( w, h );
            }
            paint( x, y, w, h, _raster );
            return _raster;
        }

        private void paint( int x, int y, int w, int h, WritableRaster raster ) {
            int[] data = new int[w * h * 4];
            for ( int j = 0; j < h; j++ ) {
                for ( int i = 0; i < w; i++ ) {
                    double angle = new Vector2D( _point, new Point2D.Double( x + i, y + j ) ).getAngle();
                    double ratio = angle;
                    ratio = Math.abs( ratio / Math.PI ) * 2;

                    int base = ( j * w + i ) * 4;
                    data[base + 0] = (int) ( _color1.getRed() + ratio * ( _color2.getRed() - _color1.getRed() ) );
                    data[base + 1] = (int) ( _color1.getGreen() + ratio * ( _color2.getGreen() - _color1.getGreen() ) );
                    data[base + 2] = (int) ( _color1.getBlue() + ratio * ( _color2.getBlue() - _color1.getBlue() ) );
                    data[base + 3] = (int) ( _color1.getAlpha() + ratio * ( _color2.getAlpha() - _color1.getAlpha() ) );
                }
            }
            raster.setPixels( 0, 0, w, h, data );
        }
    }

    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new JPanel() {
                @Override
                protected void paintComponent( Graphics g ) {
                    super.paintComponent( g );
                    Graphics2D g2 = (Graphics2D) g;

                    Arc2D.Double ellipse2 = new Arc2D.Double( 0, 0, 200, 200, 0, 90, Arc2D.PIE );
                    CircularGradientPaint rgp2 = new CircularGradientPaint( new Point2D.Double( 100, 100 ), Color.red, Color.white );
                    g2.setPaint( rgp2 );
                    g2.fill( ellipse2 );

                    Arc2D.Double ellipse = new Arc2D.Double( 0, 0, 200, 200, 90, 90, Arc2D.PIE );
                    CircularGradientPaint rgp = new CircularGradientPaint( new Point2D.Double( 100, 100 ), Color.white, Color.blue );
                    g2.setPaint( rgp );
                    g2.fill( ellipse );

                }
            } );
            setSize( 800, 700 );
            setVisible( true );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }};
    }

}
