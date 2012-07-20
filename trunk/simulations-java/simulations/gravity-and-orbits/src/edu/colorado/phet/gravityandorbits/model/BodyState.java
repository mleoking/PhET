// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Immutable state returned by the physics engine update algorithm, it is applied to the mutable Body.
 */
public class BodyState implements Serializable {
    public final Vector2D position;
    public final Vector2D velocity;
    public final Vector2D acceleration;
    public final double mass;
    public final boolean exploded;

    public BodyState( Vector2D position, Vector2D velocity, Vector2D acceleration, double mass, boolean exploded ) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
        this.exploded = exploded;
    }

    public double distanceSquared( Vector2D position ) {
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
