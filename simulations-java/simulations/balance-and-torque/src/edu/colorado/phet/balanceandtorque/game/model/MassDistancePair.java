// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;

/**
 * Convenience class for pairing a mass with a distance from the center
 * of the balancing apparatus.  This is used primarily for balance game
 * challenges.
 */
public class MassDistancePair {
    public final Mass mass;       // Class containing mass info.
    public final double distance; // Distance from plank center, in meters.

    public MassDistancePair( Mass mass, double distance ) {
        this.mass = mass;
        this.distance = distance;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        MassDistancePair that = (MassDistancePair) o;

        if ( that.distance != distance ) {
            return false;
        }

        if ( mass.getMass() != that.mass.getMass() ) {
            return false;
        }

        return true;
    }
}
