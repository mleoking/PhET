/**
 * Class: Boundary
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 18, 2003
 */
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.bounds;

/**
 * A Boundary represents the area within which a graphic is controllable.
 * This is typically done in view coordinates.
 */
public interface Boundary {
    /**
     * Determine whether this boundary contains the specified point.
     *
     * @param x
     * @param y
     * @return true if this boundary contains the specified point.
     */
    public boolean contains( int x, int y );
}
