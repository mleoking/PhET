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
import edu.umd.cs.piccolo.nodes.PPath;


public class PhotonOrbNode extends PhetPNode {

    private static final double DIAMETER = 30;
    
    public PhotonOrbNode( Color photonColor ) {
        super();
        
        double outerDiameter = DIAMETER;
        Shape outerShape = new Ellipse2D.Double( -outerDiameter/2, -outerDiameter/2, outerDiameter, outerDiameter );
        Color outerColor = new Color( photonColor.getRed(), photonColor.getGreen(), photonColor.getBlue(), 0 );
        Paint outerPaint = new RoundGradientPaint( 0, 0, photonColor, new Point2D.Double( .4 * outerDiameter, .4 * outerDiameter ), outerColor );
        PPath outerOrb = new PPath();
        outerOrb.setPathTo( outerShape );
        outerOrb.setPaint( outerPaint );
        outerOrb.setStroke( null );
        
        double innerDiameter = .6 * DIAMETER;
        Shape innerShape = new Ellipse2D.Double( -innerDiameter/2, -innerDiameter/2, innerDiameter, innerDiameter );
        Paint innerPaint = new RoundGradientPaint( 0, 0, new Color( 255, 255, 255, 180 ), new Point2D.Double( .25 * innerDiameter, .25 * innerDiameter ), photonColor );
        PPath innerOrb = new PPath();
        innerOrb.setPathTo( innerShape );
        innerOrb.setPaint( innerPaint );
        innerOrb.setStroke( null );
        
        addChild( outerOrb );
        addChild( innerOrb );
    }
}
