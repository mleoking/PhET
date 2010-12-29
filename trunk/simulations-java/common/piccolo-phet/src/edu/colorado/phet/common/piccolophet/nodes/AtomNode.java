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
 * It has a 3D look with a specular highlight at the upper left.
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
    private final Color mainColor, highlightColor, shadowColor;

    public AtomNode( double diameter, Color color ) {
        this( diameter, color, DEFAULT_HILITE, DEFAULT_SHADOW, false );
    }

    public AtomNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor, boolean convertToImage ) {
        this.mainColor = mainColor;
        this.highlightColor = highlightColor;
        this.shadowColor = shadowColor;
        sphericalNode = new SphericalNode( diameter, createPaint( diameter, mainColor, highlightColor, shadowColor ), convertToImage );
        addChild( sphericalNode );
    }

    public void setDiameter( double diameter ) {
        sphericalNode.setPaint( createPaint( diameter, mainColor, highlightColor, shadowColor ) );
        sphericalNode.setDiameter( diameter );
    }

    private static Paint createPaint( double diameter, Color mainColor, Color highlightColor, Color shadowColor ) {
        return new AtomGradientPaint( mainColor, highlightColor, shadowColor, -diameter/6, -diameter/6, diameter/4, -diameter/10, -diameter/10, diameter );
    }

    private static class AtomGradientPaint implements Paint {

        private final Color mainColor, highlightColor, shadowColor;
        private final Point2D highlightCenter, shadowCenter;
        private final double highlightRadius, shadowRadius;

        /**
         * Constructor accepts a point and a color that describe the center of
         * the gradient, a radius, and a background color. The gradient blends
         * color from the center point to the background color over the length
         * of the radius.
         *
         * @param mainColor
         * @param highlightColor
         * @param shadowColor
         * @param highlightCenterX x center of the highlight gradient
         * @param highlightCenterY y center of the highlight gradient
         * @param highlightRadius radius of the gradient blend between the highlight and main colors@param highlightCenterX x center of the highlight
         * @param shadowCenterX x center of the shadow gradient
         * @param shadowCenterY y center of the shadow gradient
         * @param shadow Radius radius of the gradient blend between the shadow and main colors
         *
         */
        public AtomGradientPaint( Color mainColor, Color highlightColor, Color shadowColor,
                double highlightCenterX, double highlightCenterY, double highlightRadius,
                double shadowCenterX, double shadowCenterY, double shadowRadius ) {

            this.mainColor = mainColor;
            this.highlightColor = highlightColor;
            this.shadowColor = shadowColor;
            this.highlightCenter = new Point2D.Double( highlightCenterX, highlightCenterY );
            this.highlightRadius = highlightRadius;
            this.shadowCenter = new Point2D.Double( shadowCenterX, shadowCenterY );
            this.shadowRadius = shadowRadius;

            if ( highlightRadius <= 0 ) {
                throw new IllegalArgumentException( "highlightRadius must be greater than 0." );
            }
            if ( shadowRadius <= 0 ) {
                throw new IllegalArgumentException( "shadowRadius must be greater than 0." );
            }
        }

        /**
         * See Paint.createContext
         */
        public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints ) {
            Point2D transformedHiliteCenter = xform.transform( highlightCenter, null );
            double transformedHiliteRadius = xform.deltaTransform( new Point2D.Double( highlightRadius, 0 ), null ).getX();
            Point2D transformedShadowCenter = xform.transform( shadowCenter, null );
            double transformedShadowRadius = xform.deltaTransform( new Point2D.Double( shadowRadius, 0 ), null ).getX();
            return new AtomGradientContext( mainColor, highlightColor, shadowColor, transformedHiliteCenter, transformedHiliteRadius, transformedShadowCenter, transformedShadowRadius );
        }

        /**
         * See Transparency.getTransparency
         */
        public int getTransparency() {
            int a1 = highlightColor.getAlpha();
            int a2 = mainColor.getAlpha();
            return ( ( ( a1 & a2 ) == 0xff ) ? OPAQUE : TRANSLUCENT );
        }
    }

    private static class AtomGradientContext implements PaintContext {

        private final Color mainColor, highlightColor, shadowColor;
        private final Point2D highlightCenter, shadowCenter;
        private final double highlightRadius, shadowRadius;
        private WritableRaster _raster;

        public AtomGradientContext( Color mainColor, Color highlightColor, Color shadowColor, Point2D highlightCenter, double highlightRadius, Point2D shadowCenter, double shadowRadius ) {
            this.mainColor = mainColor;
            this.highlightColor = highlightColor;
            this.shadowColor = shadowColor;
            this.highlightCenter = highlightCenter;
            this.highlightRadius = highlightRadius;
            this.shadowCenter = shadowCenter;
            this.shadowRadius = shadowRadius;
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
            // shadow gradient
            paintGradient( x, y, w, h, raster, mainColor, shadowColor, shadowCenter, shadowRadius );
            // highlight gradient
//            paintGradient( x, y, w, h, raster, highlightColor, mainColor, highlightCenter, highlightRadius );
        }

        /*
         * Paints a round gradient.
         */
        private static void paintGradient( int x, int y, int w, int h, WritableRaster raster, Color innerColor, Color outerColor, Point2D center, double radius ) {
            int[] data = new int[w * h * 4];
            for ( int j = 0; j < h; j++ ) {
                for ( int i = 0; i < w; i++ ) {
                    double distance = center.distance( x + i, y + j );
                    double ratio = distance / radius;
                    if ( ratio > 1.0 ) {
                        ratio = 1.0;
                    }

                    int base = ( j * w + i ) * 4;
                    data[base + 0] = (int) ( innerColor.getRed() + ratio * ( outerColor.getRed() - innerColor.getRed() ) );
                    data[base + 1] = (int) ( innerColor.getGreen() + ratio * ( outerColor.getGreen() - innerColor.getGreen() ) );
                    data[base + 2] = (int) ( innerColor.getBlue() + ratio * ( outerColor.getBlue() - innerColor.getBlue() ) );
                    data[base + 3] = (int) ( innerColor.getAlpha() + ratio * ( outerColor.getAlpha() - innerColor.getAlpha() ) );
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
