// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Rectangle;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

/**
 * Node that translates its content so that the origin is at (0,0).  This is to help with layouts for nodes that don't default to this coordinate frame.
 *
 * @author Sam Reid
 */
public class ZeroOffsetNode extends PNode {
    public ZeroOffsetNode( PNode node ) {
        addChild( node );

        //Take away any local offset applied to the node before standardizing, otherwise will be off by that amount
        node.setOffset( 0, 0 );

        //Put the new origin to be at the (0,0)
        node.setOffset( -node.getFullBounds().getMinX(), -node.getFullBounds().getY() );
    }

    public static void main( String[] args ) {
//        Rectangle rect = new Rectangle(  );
        new PFrame() {{
            PhetPPath pathNode = new PhetPPath( new Rectangle( 1000, 1000, 50, 50 ) );
            debug( pathNode );
            pathNode.setOffset( 500, 500 );
            debug( pathNode );
            ZeroOffsetNode child = new ZeroOffsetNode( pathNode );
            debug( child );
            getCanvas().getLayer().addChild( child );
        }}.setVisible( true );
    }

    private static void debug( PNode child ) {
        System.out.println( "x = " + child.getFullBounds().getX() + ", minx = " + child.getFullBounds().getMinX() );
    }
}
