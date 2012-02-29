// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.photon;

import java.awt.Color;
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
 * Origin is at the upper-left corner of the node's bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonNode extends PImage {

    private static final int PHOTON_COLOR_ALPHA = 130;
    private static final Color HILITE_COLOR = new Color( 255, 255, 255, 180 );
    private static final double CROSSHAIRS_ANGLE = 18; // degrees
    private static final Color CROSSHAIRS_COLOR = new Color( 255, 255, 255, 100 );

    private static final Color UV_IR_COLOR = new Color( 160, 160, 160 ); // gray
    private static final Color UV_CROSSHAIRS_COLOR = VisibleColor.wavelengthToColor( 400, UV_IR_COLOR, UV_IR_COLOR );
    private static final Color IR_CROSSHAIRS_COLOR = VisibleColor.wavelengthToColor( 715, UV_IR_COLOR, UV_IR_COLOR );

    /**
     * Constructs a PhotoNode for the specified wavelength and diameter.
     * Visible wavelengths are mapped to visible colors.
     * UV photons are rendered as gray orbs with violet crosshairs.
     * IR photos are rendered as gray orbs with red crosshairs.
     * <p>
     * If a different color mapping is desired, then map your wavelength
     * to the desired color before calling this constructor.
     *
     * @param wavelength
     * @param diameter
     */
    public PhotonNode( double wavelength, double diameter ) {

        Color photonColor = VisibleColor.wavelengthToColor( wavelength, UV_IR_COLOR, UV_IR_COLOR );

        PNode parentNode = new PNode();

        // Outer transparent ring
        final double outerDiameter = diameter;
        Shape outerShape = new Ellipse2D.Double( -outerDiameter / 2, -outerDiameter / 2, outerDiameter, outerDiameter );
        Color outerColor = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), 0 );
        Paint outerPaint = new RoundGradientPaint( 0, 0, photonColor, new Point2D.Double( 0.4 * outerDiameter, 0.4 * outerDiameter ), outerColor );
        PPath outerOrb = new PPath();
        outerOrb.setPathTo( outerShape );
        outerOrb.setPaint( outerPaint );
        outerOrb.setStroke( null );
        parentNode.addChild( outerOrb );

        // Inner orb, saturated color with hilite in center
        final double innerDiameter = 0.5 * diameter;
        Shape innerShape = new Ellipse2D.Double( -innerDiameter / 2, -innerDiameter / 2, innerDiameter, innerDiameter );
        Color photonColorTransparent = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), PHOTON_COLOR_ALPHA );
        Paint innerPaint = new RoundGradientPaint( 0, 0, HILITE_COLOR, new Point2D.Double( 0.25 * innerDiameter, 0.25 * innerDiameter ), photonColorTransparent );
        PPath innerOrb = new PPath();
        innerOrb.setPathTo( innerShape );
        innerOrb.setPaint( innerPaint );
        innerOrb.setStroke( null );
        parentNode.addChild( innerOrb );

        // Crosshairs
        PNode crosshairs = new PNode();
        {
            PNode bigCrosshair = createCrosshair( wavelength, 1.15 * innerDiameter );
            PNode smallCrosshair = createCrosshair( wavelength, 0.8 * innerDiameter );
            smallCrosshair.rotate( Math.toRadians( 45 ) );
            crosshairs.addChild( smallCrosshair );
            crosshairs.addChild( bigCrosshair );
        }
        crosshairs.rotate( Math.toRadians( CROSSHAIRS_ANGLE ) );
        parentNode.addChild( crosshairs );

        setImage( parentNode.toImage() );
    }

    /*
    * Creates the crosshairs that appear in the center of the image.
    */
    private static PNode createCrosshair( double wavelength, double diameter ) {

        Color crosshairsColor = CROSSHAIRS_COLOR;
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            crosshairsColor = UV_CROSSHAIRS_COLOR;
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            crosshairsColor = IR_CROSSHAIRS_COLOR;
        }

        final double crosshairWidth = diameter;
        final double crosshairHeight = 0.15 * crosshairWidth;
        Shape crosshairShape = new Ellipse2D.Double( -crosshairWidth / 2, -crosshairHeight / 2, crosshairWidth, crosshairHeight );

        PPath horizontalPart = new PPath();
        horizontalPart.setPathTo( crosshairShape );
        horizontalPart.setPaint( crosshairsColor );
        horizontalPart.setStroke( null );

        PPath verticalPart = new PPath();
        verticalPart.setPathTo( crosshairShape );
        verticalPart.setPaint( crosshairsColor );
        verticalPart.setStroke( null );
        verticalPart.rotate( Math.toRadians( 90 ) );

        PNode crosshairs = new PNode();
        crosshairs.addChild( horizontalPart );
        crosshairs.addChild( verticalPart );

        return crosshairs;
    }
}
