/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.particle;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * PhotonNode is the visual representation of a photon.
 * The look is loosely based on examples that Wendy Adams found on a 
 * Disney website at http://disney.go.com/fairies/meetfairies.html.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhotonNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* draws an outline around the full bonds of the node */
    private static final boolean DEBUG_SHOW_FULL_DIAMETER = false;
    
    /* determines whether UV and IR photons are labeled */
    private static final boolean SHOW_UV_IR_LABELS = false;
    
    /* determines whether to color the crosshairs for UV and IR photons */
    private static final boolean SHOW_UV_IR_CROSSHAIRS = true;
    
    public static final double DIAMETER = 30;
    
    private static final int PHOTON_COLOR_ALPHA = 130;
    private static final Color HILITE_COLOR = new Color( 255, 255, 255, 180 );
    private static final double CROSSHAIRS_ANGLE = 18; // degrees
    private static final Color CROSSHAIRS_COLOR = new Color( 255, 255, 255, 100 );
    private static final Color UV_CROSSHAIRS_COLOR = ColorUtils.wavelengthToColor( 400 );
    private static final Color IR_CROSSHAIRS_COLOR = ColorUtils.wavelengthToColor( 715 );
    private static final Color UV_LABEL_COLOR = UV_CROSSHAIRS_COLOR;
    private static final Color IR_LABEL_COLOR = IR_CROSSHAIRS_COLOR;
    private static final Font UV_IR_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 9 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Photon _photon; // model element
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param photon
     */
    public PhotonNode( Photon photon ) {
        super();
        
        Image image = createPhotonImage( photon.getWavelength() );
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        // Move origin to center
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        
        if ( DEBUG_SHOW_FULL_DIAMETER ) {
            PPath diameterNode = new PPath( new Ellipse2D.Double( -DIAMETER/2, -DIAMETER/2, DIAMETER, DIAMETER ) );
            diameterNode.setStroke( new BasicStroke( 1f ) );
            diameterNode.setStrokePaint( Color.WHITE );
            addChild( diameterNode );
        }

        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            OriginNode originNode = new OriginNode( Color.BLACK );
            addChild( originNode );
        }
        
        _photon = photon;
        _photon.addObserver( this );
    }
    
    /**
     * Creates the image used to represent a photon.
     * 
     * @return Image
     */
    public static final Image createPhotonImage( double wavelength )
    {
        PNode parentNode = new PNode();
        
        Color photonColor = ColorUtils.wavelengthToColor( wavelength );
        
        // Outer transparent ring
        final double outerDiameter = DIAMETER;
        Shape outerShape = new Ellipse2D.Double( -outerDiameter/2, -outerDiameter/2, outerDiameter, outerDiameter );
        Color outerColor = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), 0 );
        Paint outerPaint = new RoundGradientPaint( 0, 0, photonColor, new Point2D.Double( 0.4 * outerDiameter, 0.4 * outerDiameter ), outerColor );
        PPath outerOrb = new PPath();
        outerOrb.setPathTo( outerShape );
        outerOrb.setPaint( outerPaint );
        outerOrb.setStroke( null );
        parentNode.addChild( outerOrb );
        
        // Inner orb, saturated color with hilite in center
        final double innerDiameter = 0.5 * DIAMETER;
        Shape innerShape = new Ellipse2D.Double( -innerDiameter/2, -innerDiameter/2, innerDiameter, innerDiameter );
        Color photonColorTransparent = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), PHOTON_COLOR_ALPHA );
        Paint innerPaint = new RoundGradientPaint( 0, 0, HILITE_COLOR, new Point2D.Double( 0.25 * innerDiameter, 0.25 * innerDiameter ), photonColorTransparent );
        PPath innerOrb = new PPath();
        innerOrb.setPathTo( innerShape );
        innerOrb.setPaint( innerPaint );
        innerOrb.setStroke( null );
        parentNode.addChild( innerOrb );

        // Crosshairs (disabled if we're showing UV/IR labels)
        if ( !SHOW_UV_IR_LABELS || ( SHOW_UV_IR_LABELS && wavelength >= VisibleColor.MIN_WAVELENGTH && wavelength <= VisibleColor.MAX_WAVELENGTH ) ) {
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
        }
        
        // Labels for UV and IR wavelengths
        if ( SHOW_UV_IR_LABELS ) {
            if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
                PText uvText = new PText( "UV" );
                uvText.setFont( UV_IR_FONT );
                uvText.setTextPaint( UV_LABEL_COLOR );
                uvText.setOffset( -uvText.getWidth() / 2, -uvText.getHeight() / 2 );
                parentNode.addChild( uvText );
            }
            else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
                PText irText = new PText( "IR" );
                irText.setFont( UV_IR_FONT );
                irText.setTextPaint( IR_LABEL_COLOR );
                irText.setOffset( -irText.getWidth() / 2, -irText.getHeight() / 2 );
                parentNode.addChild( irText );
            }
        }
        
        return parentNode.toImage();
    }
    
    /*
     * Creates the crosshairs that appear in the center of the image.
     */
    private static PNode createCrosshair( double wavelength, double diameter ) {

        Color crosshairsColor = CROSSHAIRS_COLOR;
        if ( SHOW_UV_IR_CROSSHAIRS ) {
            if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
                crosshairsColor = UV_CROSSHAIRS_COLOR;
            }
            else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
                crosshairsColor = IR_CROSSHAIRS_COLOR;
            }
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

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update( Observable o, Object arg ) {
        update();
    }
    
    private void update() {
        //TODO deal with orientation
        setOffset( ModelViewTransform.transform( _photon.getPosition() ) );
    }
}
