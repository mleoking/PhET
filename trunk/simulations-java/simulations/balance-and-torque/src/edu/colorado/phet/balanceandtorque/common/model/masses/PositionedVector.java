// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * A convenience class that combines a standard vector with a second vector
 * that describes the point of origination.
 *
 * @author John Blanco
 */
public class PositionedVector {
    public final Vector2D origin;
    public final Vector2D vector;

    public PositionedVector( Vector2D origin, Vector2D vector ) {
        this.origin = new Vector2D( origin );
        this.vector = new Vector2D( vector );
    }
}
