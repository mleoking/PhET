// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.view;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * To debug the origin of a composite node, add an instance of this node as a child.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugOriginNode extends PPath {

    private static final double DIAMETER = 6;

    public DebugOriginNode() {
        this( Color.RED );
    }

    public DebugOriginNode( Color color ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        setPathTo( new Ellipse2D.Double( -( DIAMETER / 2 ), -( DIAMETER ) / 2, DIAMETER, DIAMETER ) );
        setStroke( null );
        setPaint( color );
    }

    @Override public void setOffset( double x, double y ) {
        throw new UnsupportedOperationException( "don't translate me" );
    }

    @Override public void translate( double dx, double dy ) {
        throw new UnsupportedOperationException( "don't translate me" );
    }

    @Override public void setTransform( AffineTransform transform ) {
        throw new UnsupportedOperationException( "don't transform me" );
    }
}
