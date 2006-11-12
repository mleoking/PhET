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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * OriginNode is the node used to indicate the (0,0) origin of a composite node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OriginNode extends PhetPNode {

    private static final double DIAMETER = 3;
    
    public OriginNode( Color color ) {
        super();
        PPath originNode = new PPath();
        originNode.setPathTo( new Ellipse2D.Double(-(DIAMETER/2), -(DIAMETER)/2, DIAMETER, DIAMETER ) );
        originNode.setStroke( null );
        originNode.setPaint( color );
        addChild( originNode );
    }
    
    public void setOffset( double x, double y ) {
        throw new UnsupportedOperationException( "don't do this to an OriginNode" );
    }
    
    public void setOffset( Point2D p ) {
        throw new UnsupportedOperationException( "don't do this to an OriginNode" );
    }
    
    public void setTransform( AffineTransform xform ) {
        throw new UnsupportedOperationException( "don't do this to an OriginNode" );
    }
}
