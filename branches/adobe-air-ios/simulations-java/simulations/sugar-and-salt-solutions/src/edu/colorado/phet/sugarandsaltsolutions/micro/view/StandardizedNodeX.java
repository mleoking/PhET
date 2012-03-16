// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Node that translates its content so that in the x-direction the origin is at 0, the y direction is unchanged.
 * This is to help with layouts for nodes that don't default to this coordinate frame.
 *
 * @author Sam Reid
 */
public class StandardizedNodeX extends PNode {
    public StandardizedNodeX( PNode node ) {
        addChild( node );
        node.setOffset( -node.getFullBounds().getX(), node.getYOffset() );
    }
}
