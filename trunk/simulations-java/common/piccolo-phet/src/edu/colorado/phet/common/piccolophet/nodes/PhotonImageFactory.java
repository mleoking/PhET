/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * PhotonImageFactory creates images that represent photons.
 * The look is loosely based on examples that Wendy Adams found on a
 * Disney website at http://disney.go.com/fairies/meetfairies.html.
 * <p>
 * UV photons are rendered as a gray orb with violet crosshairs.
 * IR photos are rendered as a gray orb with red crosshairs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonImageFactory extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int PHOTON_COLOR_ALPHA = 130;
    private static final Color HILITE_COLOR = new Color( 255, 255, 255, 180 );
    private static final double CROSSHAIRS_ANGLE = 18; // degrees
    private static final Color CROSSHAIRS_COLOR = new Color( 255, 255, 255, 100 );

    private static final Color UV_IR_COLOR = new Color( 160, 160, 160 ); // gray
    private static final Color UV_CROSSHAIRS_COLOR = VisibleColor.wavelengthToColor( 400, UV_IR_COLOR, UV_IR_COLOR );
    private static final Color IR_CROSSHAIRS_COLOR = VisibleColor.wavelengthToColor( 715, UV_IR_COLOR, UV_IR_COLOR );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /* not intended for instantiation */
    private PhotonImageFactory() {}
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /**
     * Creates the image used to represent a photon.
     *
     * @param wavelength
     * @param diameter
     * @return Image
     */
    public static final Image createPhotonImage( double wavelength, double diameter ) {
        PNode parentNode = new PNode();

        Color photonColor = VisibleColor.wavelengthToColor( wavelength, UV_IR_COLOR, UV_IR_COLOR );

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

        return parentNode.toImage();
    }

    /*
     * Creates the crosshairs that appear in the center of the image.
     */
    private static PNode createCrosshair( double wavelength, double diameter ) {

        Color crosshairsColor = CROSSHAIRS_COLOR;
        if( wavelength < VisibleColor.MIN_WAVELENGTH ) {
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