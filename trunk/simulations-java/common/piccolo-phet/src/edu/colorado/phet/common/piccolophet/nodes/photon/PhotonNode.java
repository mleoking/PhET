// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.photon;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * PhET's standard representation of a photon.
 * The look is loosely based on examples that Wendy Adams found on a
 * Disney website at http://disney.go.com/fairies/meetfairies.html.
 * A round "orb" has an outer "halo" and a whimsical "sparkle" in the middle.
 * Origin is at the upper-left corner of the node's bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonNode extends PImage {

    // Orb
    private static final int ORB_ALPHA = 130;
    private static final Color ORB_CENTER_COLOR = new Color( 255, 255, 255, 180 );
    private static final Color UV_ORB_COLOR = new Color( 160, 160, 160 ); // gray
    private static final Color IR_ORB_COLOR = UV_ORB_COLOR;

    // Sparkle
    private static final double SPARKLE_ANGLE = Math.toRadians( 18 );
    private static final Color SPARKLE_COLOR = new Color( 255, 255, 255, 100 ); // sparkle color for visible wavelengths
    private static final Color UV_SPARKLE_COLOR = VisibleColor.wavelengthToColor( 400, UV_ORB_COLOR, UV_ORB_COLOR );
    private static final Color IR_SPARKLE_COLOR = VisibleColor.wavelengthToColor( 715, UV_ORB_COLOR, UV_ORB_COLOR );

    /**
     * Constructs a PhotoNode for the specified wavelength and diameter.
     * <p>
     * Visible wavelengths are mapped to visible colors.
     * UV photons are rendered as gray orbs with violet crosshairs.
     * IR photos are rendered as gray orbs with red crosshairs.
     * <p>
     * If a different color mapping is desired, then map your wavelength
     * to the desired color before calling this constructor.
     *
     * @param wavelength
     * @param diameter diameter of the outer halo
     */
    public PhotonNode( double wavelength, double diameter ) {

        // map wavelength to colors
        final Color orbColor = getOrbColor( wavelength );
        final Color sparkleColor = getSparkleColor( wavelength );

        // assemble the components of the node
        PNode parentNode = new PNode();
        parentNode.addChild( new HaloNode( orbColor, diameter ) );
        parentNode.addChild( new OrbNode( orbColor, 0.5 * diameter ) );
        parentNode.addChild( new SparkleNode( sparkleColor, 0.575 * diameter, SPARKLE_ANGLE ) );

        // convert to image
        setImage( parentNode.toImage() );
    }

    // The orb that is the main body of the photon. Origin at center.
    private static class OrbNode extends PPath {

        public OrbNode( Color color, double diameter ) {
            Shape shape = new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter );
            Color outerColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), ORB_ALPHA );
            Paint paint = new RoundGradientPaint( 0, 0, ORB_CENTER_COLOR, new Point2D.Double( 0.25 * diameter, 0.25 * diameter ), outerColor );
            setPathTo( shape );
            setPaint( paint );
            setStroke( null );
        }
    }

    // The halo that surrounds the orb. Origin at center.
    private static class HaloNode extends PPath {

        public HaloNode( Color color, double diameter ) {
            Shape shape = new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter );
            Color outerColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), 0 );
            Paint paint = new RoundGradientPaint( 0, 0, color, new Point2D.Double( 0.4 * diameter, 0.4 * diameter ), outerColor );
            setPathTo( shape );
            setPaint( paint );
            setStroke( null );
        }
    }

    // Sparkle at the center of the photon. Origin at center.
    private static class SparkleNode extends PNode {

        public SparkleNode( Color color, double diameter, double angle ) {
            PNode bigCrosshairs = new CrosshairsNode( color, diameter );
            PNode smallCrosshairs = new CrosshairsNode( color, 0.7 * diameter );
            smallCrosshairs.rotate( Math.toRadians( 45 ) );
            addChild( smallCrosshairs );
            addChild( bigCrosshairs );
            rotate( angle );
        }
    }

    // Crosshairs used to create the sparkle. Origin at center.
    private static class CrosshairsNode extends PNode {

        public CrosshairsNode( Color color, double diameter ) {

            final double crosshairWidth = diameter;
            final double crosshairHeight = 0.15 * crosshairWidth;
            Shape crosshairShape = new Ellipse2D.Double( -crosshairWidth / 2, -crosshairHeight / 2, crosshairWidth, crosshairHeight );

            PPath horizontalPart = new PPath( crosshairShape );
            horizontalPart.setPaint( color );
            horizontalPart.setStroke( null );

            PPath verticalPart = new PPath( crosshairShape );
            verticalPart.setPaint( color );
            verticalPart.setStroke( null );
            verticalPart.rotate( Math.toRadians( 90 ) );

            addChild( horizontalPart );
            addChild( verticalPart );
        }
    }

    // Override this if you want a different mapping of wavelength to orb color.
    protected Color getOrbColor( double wavelength ) {
       return VisibleColor.wavelengthToColor( wavelength, UV_ORB_COLOR, IR_ORB_COLOR );
    }

    // Override this if you want a different mapping of wavelength to sparkle color.
    protected Color getSparkleColor( double wavelength ) {
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            return UV_SPARKLE_COLOR;
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            return IR_SPARKLE_COLOR;
        }
        else {
            return SPARKLE_COLOR;
        }
    }

    // Convenience method for creating photon images
    public static Image createImage( double wavelength, double diameter ) {
        return new PhotonNode( wavelength, diameter ).toImage();
    }
}
