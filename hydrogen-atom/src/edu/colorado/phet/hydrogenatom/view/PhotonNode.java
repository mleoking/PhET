/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;


public class PhotonNode extends PhetPNode implements Observer {

    private static final boolean DEBUG_SHOW_FULL_DIAMETER = false;
    
    private static final double DEFAULT_DIAMETER = 30;
    private static final double CROSSHAIRS_ANGLE = 18; // degrees
    private static final Color CROSSHAIRS_COLOR = new Color( 255, 255, 255, 100 );
    private static final Color HILITE_COLOR = new Color( 255, 255, 255, 180 );
    private static final int PHOTON_COLOR_ALPHA = 130;
    
    private Photon _photon;
    
    public PhotonNode( Photon photon ) {
        super();
        
        Image image = createPhotonImage( photon.getColor() );
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        // Move origin to center
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        
        if ( DEBUG_SHOW_FULL_DIAMETER ) {
            PPath diameterNode = new PPath( new Ellipse2D.Double( -DEFAULT_DIAMETER/2, -DEFAULT_DIAMETER/2, DEFAULT_DIAMETER, DEFAULT_DIAMETER ) );
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
    
    public static final Image createPhotonImage( Color photonColor )
    {
        PNode parentNode = new PNode();
        
        // Outer transparent ring
        final double outerDiameter = DEFAULT_DIAMETER;
        Shape outerShape = new Ellipse2D.Double( -outerDiameter/2, -outerDiameter/2, outerDiameter, outerDiameter );
        Color outerColor = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), 0 );
        Paint outerPaint = new RoundGradientPaint( 0, 0, photonColor, new Point2D.Double( 0.4 * outerDiameter, 0.4 * outerDiameter ), outerColor );
        PPath outerOrb = new PPath();
        outerOrb.setPathTo( outerShape );
        outerOrb.setPaint( outerPaint );
        outerOrb.setStroke( null );
        
        // Inner orb, saturated color with hilite in center
        final double innerDiameter = 0.5 * DEFAULT_DIAMETER;
        Shape innerShape = new Ellipse2D.Double( -innerDiameter/2, -innerDiameter/2, innerDiameter, innerDiameter );
        Color photonColorTransparent = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), PHOTON_COLOR_ALPHA );
        Paint innerPaint = new RoundGradientPaint( 0, 0, HILITE_COLOR, new Point2D.Double( 0.25 * innerDiameter, 0.25 * innerDiameter ), photonColorTransparent );
        PPath innerOrb = new PPath();
        innerOrb.setPathTo( innerShape );
        innerOrb.setPaint( innerPaint );
        innerOrb.setStroke( null );

        // Crosshairs
        PNode crosshairs = new PNode();
        {
            PNode bigCrosshair = createCrosshair( 1.15 * innerDiameter );
            PNode smallCrosshair = createCrosshair( 0.8 * innerDiameter );
            smallCrosshair.rotate( Math.toRadians( 45 ) );
            crosshairs.addChild( smallCrosshair );
            crosshairs.addChild( bigCrosshair );
        }
        crosshairs.rotate( Math.toRadians( CROSSHAIRS_ANGLE ) );

        parentNode.addChild( outerOrb );
        parentNode.addChild( innerOrb );
        parentNode.addChild( crosshairs );
        
        return parentNode.toImage();
    }
    
    private static PNode createCrosshair( double diameter ) {

        final double crosshairWidth = diameter;
        final double crosshairHeight = 0.15 * crosshairWidth;
        Shape crosshairShape = new Ellipse2D.Double( -crosshairWidth / 2, -crosshairHeight / 2, crosshairWidth, crosshairHeight );

        PPath horizontalPart = new PPath();
        horizontalPart.setPathTo( crosshairShape );
        horizontalPart.setPaint( CROSSHAIRS_COLOR );
        horizontalPart.setStroke( null );

        PPath verticalPart = new PPath();
        verticalPart.setPathTo( crosshairShape );
        verticalPart.setPaint( CROSSHAIRS_COLOR );
        verticalPart.setStroke( null );
        verticalPart.rotate( Math.toRadians( 90 ) );

        PNode crosshairs = new PNode();
        crosshairs.addChild( horizontalPart );
        crosshairs.addChild( verticalPart );
        
        return crosshairs;
    }

    public void update( Observable o, Object arg ) {
        update();
    }
    
    private void update() {
        rotate( _photon.getOrientation() );
        setOffset( ModelViewTransform.translate( _photon.getPosition() ) );
    }
}
