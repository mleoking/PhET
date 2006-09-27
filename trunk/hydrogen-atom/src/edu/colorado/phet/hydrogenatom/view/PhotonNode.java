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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;


public class PhotonNode extends PhetPNode {

    private static final Color DEFAULT_COLOR = Color.MAGENTA;
    private static final double DEFAULT_DIAMETER = 30;
    private static final Color CROSSHAIRS_COLOR = new Color( 255, 255, 255, 100 );
    private static final Color HILITE_COLOR = new Color( 255, 255, 255, 180 );
    
    public PhotonNode() {
        this( DEFAULT_COLOR );
    }
    
    public PhotonNode( Color photonColor ) {
        this( photonColor, DEFAULT_DIAMETER );
    }
    
    public PhotonNode( Color photonColor, final double diameter ) {
        super();
        PImage imageNode = createPhotonImage( photonColor, diameter );
        addChild( imageNode );
        
    }
    
    private static PImage createPhotonImage( Color photonColor, final double diameter )
    {
        PNode parentNode = new PNode();
        
        // Outer transparent ring
        final double outerDiameter = diameter;
        Shape outerShape = new Ellipse2D.Double( -outerDiameter/2, -outerDiameter/2, outerDiameter, outerDiameter );
        Color outerColor = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), 0 );
        Paint outerPaint = new RoundGradientPaint( 0, 0, photonColor, new Point2D.Double( 0.4 * outerDiameter, 0.4 * outerDiameter ), outerColor );
        PPath outerOrb = new PPath();
        outerOrb.setPathTo( outerShape );
        outerOrb.setPaint( outerPaint );
        outerOrb.setStroke( null );
        
        // Inner orb, saturated color with hilite in center
        final double innerDiameter = 0.6 * diameter;
        Shape innerShape = new Ellipse2D.Double( -innerDiameter/2, -innerDiameter/2, innerDiameter, innerDiameter );
        Color photonColorTransparent = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), 200 );
        Paint innerPaint = new RoundGradientPaint( 0, 0, HILITE_COLOR, new Point2D.Double( 0.25 * innerDiameter, 0.25 * innerDiameter ), photonColorTransparent );
        PPath innerOrb = new PPath();
        innerOrb.setPathTo( innerShape );
        innerOrb.setPaint( innerPaint );
        innerOrb.setStroke( null );

        // Crosshairs
        PNode bigCrosshairs = createCrosshairs( 0.75 * outerDiameter );
        bigCrosshairs.rotate( Math.toRadians( 45 ) );
        PNode smallCrosshairs = createCrosshairs( 0.75 * innerDiameter );

        parentNode.addChild( outerOrb );
        parentNode.addChild( innerOrb );
        parentNode.addChild( bigCrosshairs );
        parentNode.addChild( smallCrosshairs );
        
        return new PImage( parentNode.toImage() );
    }
    
    private static PNode createCrosshairs( double diameter ) {

        final double crosshairWidth = diameter;
        final double crosshairHeight = 0.15 * crosshairWidth;
        Shape crosshairShape = new Ellipse2D.Double( -crosshairWidth / 2, -crosshairHeight / 2, crosshairWidth, crosshairHeight );

        PPath crosshair1 = new PPath();
        crosshair1.setPathTo( crosshairShape );
        crosshair1.setPaint( CROSSHAIRS_COLOR );
        crosshair1.setStroke( null );

        PPath crosshair2 = new PPath();
        crosshair2.setPathTo( crosshairShape );
        crosshair2.setPaint( CROSSHAIRS_COLOR );
        crosshair2.setStroke( null );
        crosshair2.rotate( Math.toRadians( 90 ) );

        PNode crosshairs = new PNode();
        crosshairs.addChild( crosshair1 );
        crosshairs.addChild( crosshair2 );
        
        return crosshairs;
    }
}
