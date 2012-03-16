// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;

/**
 * Interface that chooses where how to position a child PNode, based on layout constraints such as max size and accumulated location thus far.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public interface PositionStrategy {
    Point2D getRelativePosition( PNode node, double maxSize, double location /* x or y coordinate, depending on orientation of box */ );
}
