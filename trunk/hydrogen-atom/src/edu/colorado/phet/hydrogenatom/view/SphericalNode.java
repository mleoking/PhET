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

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * SphericalNode draws a sphere, with origin at the center of the sphere.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SphericalNode extends PhetPNode {

    private PPath _pathNode;
    
    public SphericalNode( double diameter, Paint fillPaint, Stroke stroke, Paint strokePaint ) {
        super();

        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        _pathNode = new PPath( shape );
        _pathNode.setPaint( fillPaint );
        _pathNode.setStroke( stroke );
        _pathNode.setStrokePaint( strokePaint );
        
        addChild( _pathNode );
    }
    
    public double getDiameter() {
        return getFullBounds().getWidth();
    }
}
