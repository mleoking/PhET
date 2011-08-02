// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A convenience class that combines a standard vector with a second vector
 * that describes the point of origination.
 *
 * @author John Blanco
 */
public class PositionedVector {
    public final ImmutableVector2D origin;
    public final ImmutableVector2D vector;

    PositionedVector( ImmutableVector2D origin, ImmutableVector2D vector ) {
        this.origin = new ImmutableVector2D( origin );
        this.vector = new ImmutableVector2D( vector );
    }
}
