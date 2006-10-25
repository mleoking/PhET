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

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * SphericalNode draws a sphere, with origin at the center of the sphere.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SphericalNode extends PhetPNode {
    
    public SphericalNode( double diameter, Paint fillPaint, boolean convertToImage ) {
        this( diameter, fillPaint, null, null, convertToImage );
    }
        
    public SphericalNode( double diameter, Paint fillPaint, Stroke stroke, Paint strokePaint, boolean convertToImage ) {
        super();

        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        PPath pathNode = new PPath( shape );
        pathNode.setPaint( fillPaint );
        pathNode.setStroke( stroke );
        pathNode.setStrokePaint( strokePaint );
        
        if ( convertToImage ) {
            PImage imageNode = new PImage( pathNode.toImage() );
            // Move origin to center
            imageNode.setOffset( -imageNode.getFullBounds().getWidth() / 2, -imageNode.getFullBounds().getHeight() / 2 );
            addChild( imageNode );
        }
        else {
            addChild( pathNode );
        }
    }
    
    public double getDiameter() {
        return getFullBounds().getWidth();
    }
}
