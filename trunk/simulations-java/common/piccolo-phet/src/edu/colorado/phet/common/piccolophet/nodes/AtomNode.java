/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PCanvas;

/**
 * PhET's visual representation of an atom.
 * It has a 3D look with a specular hilite at the upper left.
 * <p>
 * This implementation uses SphericalNode via composition instead of subclassing, 
 * because SphericalNode's interface uses Paint, and we're constrained to Color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AtomNode extends PhetPNode {
    
    private static final Color DEFAULT_HILITE = Color.WHITE;
    private static final Color DEFAULT_SHADOW = Color.BLACK;
    
    private final SphericalNode sphericalNode;
    private final Color mainColor, hiliteColor, shadowColor;
    
    public AtomNode( double diameter, Color color ) {
        this( diameter, color, DEFAULT_HILITE, DEFAULT_SHADOW, false );
    }

    public AtomNode( double diameter, Color mainColor, Color hiliteColor, Color shadowColor, boolean convertToImage ) {
        this.mainColor = mainColor;
        this.hiliteColor = hiliteColor;
        this.shadowColor = shadowColor;
        sphericalNode = new SphericalNode( diameter, createPaint( diameter, mainColor, hiliteColor, shadowColor ), convertToImage );
        addChild( sphericalNode );
    }
    
    public void setDiameter( double diameter ) {
        sphericalNode.setPaint( createPaint( diameter, mainColor, hiliteColor, shadowColor ) );
        sphericalNode.setDiameter( diameter );
    }

    private static Paint createPaint( double diameter, Color mainColor, Color hiliteColor, Color shadowColor ) {
        return new AtomGradientPaint( mainColor, hiliteColor, shadowColor, -diameter/6, -diameter/6, diameter/4, diameter/4 );
    }
    
    private static class AtomGradientPaint implements Paint {

        private final Color mainColor, hiliteColor, shadowColor;
        private final Point2D hiliteCenter;
        private final Point2D hiliteRadius;
       
        /**
         * Constructor accepts a point and a color that describe the center of
         * the gradient, a radius, and a background color. The gradient blends
         * color from the center point to the background color over the length
         * of the radius.
         *
         * @param mainColor
         * @param hiliteColor
         * @param shadowColor 
         * @param hiliteCenterX x center of the hilite
         * @param hiliteCenterY y center of the hilite
         * @param hiliteRadius radius of the gradient blend between the hilite and main colors
         * 
         */
        public AtomGradientPaint( Color mainColor, Color hiliteColor, Color shadowColor, double hiliteCenterX, double hiliteCenterY, double hiliteRadiusX, double hiliteRadiusY ) {
            this.mainColor = mainColor;
            this.hiliteColor = hiliteColor;
            this.shadowColor = shadowColor;
            this.hiliteCenter = new Point2D.Double( hiliteCenterX, hiliteCenterY );
            this.hiliteRadius = new Point2D.Double( hiliteRadiusX, hiliteRadiusY );
            if ( hiliteRadius.distance( 0, 0 ) <= 0 ) {
                throw new IllegalArgumentException( "hiliteRadius must be greater than 0." );
            }
        }

        /**
         * See Paint.createContext
         */
        public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints ) {
            Point2D transformedPoint = xform.transform( hiliteCenter, null );
            Point2D transformedRadius = xform.deltaTransform( hiliteRadius, null );
            return new AtomGradientContext( transformedPoint, hiliteColor, transformedRadius, mainColor );
        }

        /**
         * See Transparency.getTransparency
         */
        public int getTransparency() {
            int a1 = hiliteColor.getAlpha();
            int a2 = mainColor.getAlpha();
            return ( ( ( a1 & a2 ) == 0xff ) ? OPAQUE : TRANSLUCENT );
        }
    }
    
    private static class AtomGradientContext implements PaintContext {

        private final Point2D _point;
        private final Point2D _radius;
        private final Color _color1, _color2;
        private WritableRaster _raster;
        
        public AtomGradientContext( Point2D p, Color color1, Point2D r, Color color2 ) {
            _point = p;
            _color1 = color1;
            _radius = r;
            _color2 = color2;
        }

        public void dispose() {
        }

        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        public Raster getRaster( int x, int y, int w, int h ) {
            // allocate raster on demand, or if we need a bigger raster
            if ( _raster == null || w > _raster.getWidth() || h > _raster.getHeight()  ) {
                _raster = getColorModel().createCompatibleWritableRaster( w, h );
            }
            paint( x, y, w, h, _raster );
            return _raster;
        }

        private void paint( int x, int y, int w, int h, WritableRaster raster ) {
            int[] data = new int[w * h * 4];
            for ( int j = 0; j < h; j++ ) {
                for ( int i = 0; i < w; i++ ) {
                    double distance = _point.distance( x + i, y + j );
                    double radius = _radius.distance( 0, 0 );
                    double ratio = distance / radius;
                    if ( ratio > 1.0 ) {
                        ratio = 1.0;
                    }

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
        // node
        AtomNode node = new AtomNode( 50, Color.RED );
        node.setOffset( 100, 100 );
        // canvas
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 400, 400 ) );
        canvas.getLayer().addChild( node );
        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
