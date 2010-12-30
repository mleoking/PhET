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

    private static final Color DEFAULT_HIGHLIGHT = Color.WHITE;
    private static final Color DEFAULT_SHADOW = Color.BLACK;

    private final SphericalNode sphericalNode;
    private final Color highlightColor, mainColor, shadowColor; // save colors to regenerate gradient if diameter changes

    /**
     * Constructs an atom node with default highlight and shadow colors.
     * @param diameter
     * @param color
     */
    public AtomNode( double diameter, Color color ) {
        this( diameter, color, DEFAULT_HIGHLIGHT, DEFAULT_SHADOW, false /* convertToImage */ );
    }

    /**
     * @param diameter
     * @param mainColor main color of the atom
     * @param highlightColor color used for the specular highlight
     * @param shadowColor color used for the shadow
     */
    public AtomNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor ) {
        this( diameter, mainColor, highlightColor, shadowColor, false /* convertToImage */ );
    }

    /**
     * @param diameter
     * @param mainColor main color of the atom
     * @param highlightColor color used for the specular highlight
     * @param shadowColor color used for the shadow
     * @param convertToImage gradient paint used herein is expensive, setting this to true converts to an image
     */
    public AtomNode( double diameter, Color mainColor, Color highlightColor, Color shadowColor, boolean convertToImage ) {
        this.mainColor = mainColor;
        this.highlightColor = highlightColor;
        this.shadowColor = shadowColor;
        sphericalNode = new SphericalNode( diameter, createPaint( diameter, highlightColor, mainColor, shadowColor ), convertToImage );
        addChild( sphericalNode );
    }

    /**
     * Sets the diameter.
     * This requires adjusting the paint, so that the gradient matches the new diameter.
     * @param diameter
     */
    public void setDiameter( double diameter ) {
        if ( diameter != sphericalNode.getDiameter() ) {
            sphericalNode.setPaint( createPaint( diameter, highlightColor, mainColor, shadowColor ) );
            sphericalNode.setDiameter( diameter );
        }
    }

    /*
     * Creates a 3-color gradient paint for the SphereNode, to mimic a specular highlight and shadow.
     * SphereNode's origin is at the center of the sphere.
     */
    private static Paint createPaint( double diameter, Color highlightColor, Color mainColor, Color shadowColor ) {
        double centerX = -diameter/6; // upper-left corner of the sphere
        double centerY = centerX;
        double highlightMainSpan = diameter/4; // distance for the gradient from highlightColor to mainColor
        double mainShadowSpan = 0.8 * diameter; // distance for the gradient from mainColor to shadowColor
        return new TriColorRoundGradientPaint( highlightColor, mainColor, shadowColor, centerX, centerY, highlightMainSpan, mainShadowSpan );
    }

    /*
     * A round gradient paint that is composed of 3 colors.
     */
    private static class TriColorRoundGradientPaint implements Paint {

        private final Color innerColor, middleColor, outerColor;
        private final Point2D center;
        private final double innerMiddleSpan, middleOuterSpan;

        /**
         * Constructor accepts a point and a color that describe the center of
         * the gradient, a radius, and a background color. The gradient blends
         * color from the center point to the background color over the length
         * of the radius.
         * @param innerColor
         * @param middleColor
         * @param outerColor
         * @param centerX x center of the gradient
         * @param centerY y center of the gradient
         * @param innerMiddleSpan distance across which the gradient blends from innerColor to middleColor
         * @param middleOuterSpan distance across which the gradient blends from middleColor to outerColor
         *
         */
        public TriColorRoundGradientPaint( Color innerColor, Color middleColor, Color outerColor,
                double centerX, double centerY, double innerMiddleSpan, double middleOuterSpan ) {

            if ( innerMiddleSpan <= 0 ) {
                throw new IllegalArgumentException( "innerMiddleSpan must be > 0." );
            }
            if ( middleOuterSpan <= 0 ) {
                throw new IllegalArgumentException( "middleOuterSpan must be > 0." );
            }

            this.middleColor = middleColor;
            this.innerColor = innerColor;
            this.outerColor = outerColor;
            this.center = new Point2D.Double( centerX, centerY );
            this.innerMiddleSpan = innerMiddleSpan;
            this.middleOuterSpan = middleOuterSpan;
        }

        /**
         * See Paint.createContext
         */
        public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints ) {
            Point2D transformedCenter = xform.transform( center, null );
            double transformedInnerMiddleSpan = xform.deltaTransform( new Point2D.Double( innerMiddleSpan, 0 ), null ).getX();
            double transformedMiddleOuterSpan = xform.deltaTransform( new Point2D.Double( middleOuterSpan, 0 ), null ).getX();
            return new TriColorRoundGradientContext( innerColor, middleColor, outerColor, transformedCenter, transformedInnerMiddleSpan, transformedMiddleOuterSpan );
        }

        /**
         * See Transparency.getTransparency
         */
        public int getTransparency() {
            int a1 = innerColor.getAlpha();
            int a2 = middleColor.getAlpha();
            return ( ( ( a1 & a2 ) == 0xff ) ? OPAQUE : TRANSLUCENT );
        }
    }

    private static class TriColorRoundGradientContext implements PaintContext {

        private final Color innerColor, middleColor, outerColor;
        private final Point2D center;
        private final double innerMiddleSpan, middleOuterSpan;
        private WritableRaster raster;

        /**
         * @param innerColor the inner-most color of the 3-color gradient
         * @param middleColor the middle color of the 3-color gradient
         * @param outerColor the outer-most color of the 3-color gradient
         * @param center the center point of the gradient
         * @param innerMiddleSpan the distance over which the gradient transitions from innerColor to middleColor
         * @param middleOuterSpan the distance over which the gradient transitions from middleColor to outerColor
         */
        public TriColorRoundGradientContext( Color innerColor, Color middleColor, Color outerColor, Point2D center, double innerMiddleSpan, double middleOuterSpan ) {
            this.innerColor = innerColor;
            this.middleColor = middleColor;
            this.outerColor = outerColor;
            this.center = center;
            this.innerMiddleSpan = innerMiddleSpan;
            this.middleOuterSpan = middleOuterSpan;
        }

        /**
         * @see java.awt.PaintContext#dispose()
         */
        public void dispose() {
        }

        /**
         * @see java.awt.PaintContext#getColorModel()
         */
        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        /**
         * @see java.awt.PaintContext#getRaster(int, int, int, int)
         */
        public Raster getRaster( int x, int y, int w, int h ) {
            // allocate raster on demand, or if we need a bigger raster
            if ( raster == null || w > raster.getWidth() || h > raster.getHeight()  ) {
                raster = getColorModel().createCompatibleWritableRaster( w, h );
            }
            paint( x, y, w, h, raster, innerColor, middleColor, outerColor, center, innerMiddleSpan, middleOuterSpan );
            return raster;
        }

        /*
         * Paints a 3-color round gradient.
         *
         * @param x the x coordinate of the area in device space for which colors are generated.
         * @param y the y coordinate of the area in device space for which colors are generated.
         * @param w the width of the area in device space
         * @param h the height of the area in device space
         * @param raster rectangular array of pixel data
         * @param innerColor the inner-most color of the 3-color gradient
         * @param middleColor the middle color of the 3-color gradient
         * @param outerColor the outer-most color of the 3-color gradient
         * @param innerMiddleSpan the distance over which the gradient transitions from innerColor to middleColor
         * @param middleOuterSpan the distance over which the gradient transitions from middleColor to outerColor
         */
        private static void paint( int x, int y, int w, int h, WritableRaster raster,
                Color innerColor, Color middleColor, Color outerColor,
                Point2D center, double innerMiddleSpan, double middleOuterSpan ) {

            int[] data = new int[w * h * 4];
            Color color1, color2;
            double ratio;  // ratio for interpolating between color1 and color2

            for ( int j = 0; j < h; j++ ) {
                for ( int i = 0; i < w; i++ ) {

                    // pick 2 colors and compute interpolation ration, based on our distance from the gradient's center
                    double distanceFromCenter = center.distance( x + i, y + j );
                    if ( distanceFromCenter <= innerMiddleSpan ) {
                        // we're in inner-to-middle part of the gradient
                        color1 = innerColor;
                        color2 = middleColor;
                        ratio = distanceFromCenter / innerMiddleSpan;
                    }
                    else {
                        // we're outside the inner-to-middle part of the gradient
                        color1 = middleColor;
                        color2 = outerColor;
                        ratio = ( distanceFromCenter - innerMiddleSpan ) / middleOuterSpan;
                        if ( ratio > 1.0 ) {
                            // we're past the point where the gradient becomes outerColor
                            ratio = 1.0;
                        }
                    }

                    // compute color components
                    int base = ( j * w + i ) * 4;
                    data[base + 0] = interpolateColorComponent( color1.getRed(), color2.getRed(), ratio );
                    data[base + 1] = interpolateColorComponent( color1.getGreen(), color2.getGreen(), ratio );
                    data[base + 2] = interpolateColorComponent( color1.getBlue(), color2.getBlue(), ratio );
                    data[base + 3] = interpolateColorComponent( color1.getAlpha(), color2.getAlpha(), ratio );
                }
            }
            raster.setPixels( 0, 0, w, h, data );
        }

        /*
         * Linear interpolation between 2 color components.
         * Ratios closer to 0 produce colors closer to color1.
         */
        private static int interpolateColorComponent( int component1, int component2, double ratio ) {
            assert( component1 >= 0 && component1 <= 255 );
            assert( component2 >= 0 && component2 <= 255 );
            assert( ratio >= 0.0 && ratio <= 1.0 );
            return (int) ( component1 + ratio * ( component2 - component1 ) );
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
