// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.dev;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * To debug the origin of a composite node, add an instance of this node as a child.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugOriginNode extends PhetPNode {

    private static final double DIAMETER = 6;

    public DebugOriginNode() {
        this( Color.RED );
    }

    public DebugOriginNode( Color color ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        PPath originNode = new PPath();
        originNode.setPathTo( new Ellipse2D.Double( -( DIAMETER / 2 ), -( DIAMETER ) / 2, DIAMETER, DIAMETER ) );
        originNode.setStroke( null );
        originNode.setPaint( color );
        addChild( originNode );
    }
}
