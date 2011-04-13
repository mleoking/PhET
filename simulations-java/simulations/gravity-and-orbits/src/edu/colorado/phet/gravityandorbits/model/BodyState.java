// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

//REVIEW Is this really a memento pattern? I don't see any rollback/undo being done. Body is the originator, but who is the caretaker in this case?

/**
 * Immutable memento for updating the physics of a single Body.
 */
public class BodyState implements Serializable {
    public final ImmutableVector2D position;
    public final ImmutableVector2D velocity;
    public final ImmutableVector2D acceleration;
    public final double mass;
    public final boolean exploded;

    public BodyState( ImmutableVector2D position, ImmutableVector2D velocity, ImmutableVector2D acceleration, double mass, boolean exploded ) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
        this.exploded = exploded;
    }

    public double distanceSquared( ImmutableVector2D position ) {
        return this.position.getSubtractedInstance( position ).getMagnitudeSq();
    }

    @Override
    public String toString() {
        return "BodyState{" +
               "position=" + position +
               ", velocity=" + velocity +
               ", acceleration=" + acceleration +
               ", mass=" + mass +
               '}';
    }
}
