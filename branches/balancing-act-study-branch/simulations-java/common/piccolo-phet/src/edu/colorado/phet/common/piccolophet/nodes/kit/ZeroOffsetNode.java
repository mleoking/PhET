// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Rectangle;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

/**
 * Node that translates its content so that the origin is at (0,0).
 * This is to help with layouts for nodes that don't default to this coordinate frame.
 * It extends RichPNode to make it easier to write layout code using the methods provided in RichPNode.
 * <p>
 * NOTE: Because the offset is computed in the constructor, and is not updated if bounds change,
 * any transforms on the node hierarchy wrapped by ZeroOffsetNode must be applied before instantiation.
 * Any translation applied to the node passed to ZeroOffsetNode will be overwritten.
 * </p>
 *
 * @author Sam Reid
 */
public class ZeroOffsetNode extends RichPNode {
    public ZeroOffsetNode( PNode node ) {
        addChild( node );

        zeroNodeOffset( node );
    }

    public static void zeroNodeOffset( PNode node ) {
        // The following line makes sure that the bounds of the PNode are accurate.  Usually this is superfluous, but
        // we have seen occasions where this was needed in order for this class to work as intended.
        node.getFullBoundsReference();

        //Take away any local offset applied to the node before standardizing, otherwise will be off by that amount
        node.setOffset( 0, 0 );

        //Put the new origin to be at the (0,0)
        node.setOffset( -node.getFullBounds().getMinX(), -node.getFullBounds().getY() );
    }

    public static void main( String[] args ) {
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