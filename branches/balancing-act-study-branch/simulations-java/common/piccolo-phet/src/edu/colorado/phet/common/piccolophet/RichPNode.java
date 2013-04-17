// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.umd.cs.piccolo.PNode;

/**
 * Adds useful methods to PNode, but does not change the behavior of existing functionality in any way.
 * This class is named "Rich" meaning it has a "rich" interface, enriched by the new methods!
 *
 * @author Sam Reid
 */
public class RichPNode extends PNode {

    public RichPNode( PNode... children ) {
        for ( PNode child : children ) {
            addChild( child );
        }
    }

    public RichPNode( final Collection<? extends PNode> children ) {
        for ( PNode child : children ) {
            addChild( child );
        }
    }

    /*
    * Returns an iterator that iterates over the children of this pnode, in the rendering order.
    */
    public ArrayList<PNode> getChildren() {
        return new ArrayList<PNode>() {{
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                add( getChild( i ) );
            }
        }};
    }

    public double getFullWidth() {
        return getFullBoundsReference().getWidth();
    }

    public double getFullHeight() {
        return getFullBoundsReference().getHeight();
    }

    public double getMaxX() {
        return getFullBoundsReference().getMaxX();
    }

    public double getMaxY() {
        return getFullBoundsReference().getMaxY();
    }

    //Note the mismatch between getMinX and getX
    public double getMinX() {
        return getFullBoundsReference().getMinX();
    }

    //Note the mismatch between getMinY and getY
    public double getMinY() {
        return getFullBoundsReference().getMinY();
    }

    public double getCenterY() {
        return getFullBoundsReference().getCenterY();
    }

    public double getCenterX() {
        return getFullBoundsReference().getCenterX();
    }

    public void centerFullBoundsOnPoint( Point2D point ) {
        super.centerFullBoundsOnPoint( point.getX(), point.getY() );
    }

    public void centerFullBoundsOnPoint( Vector2D point ) {
        super.centerFullBoundsOnPoint( point.getX(), point.getY() );
    }
}