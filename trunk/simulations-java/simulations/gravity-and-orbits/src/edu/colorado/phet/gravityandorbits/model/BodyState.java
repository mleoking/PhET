package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

public class BodyState {
    public final ImmutableVector2D position;
    public final ImmutableVector2D velocity;
    public final ImmutableVector2D acceleration;
    public final double mass;

    public BodyState( ImmutableVector2D position, ImmutableVector2D velocity, ImmutableVector2D acceleration, double mass ) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
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
