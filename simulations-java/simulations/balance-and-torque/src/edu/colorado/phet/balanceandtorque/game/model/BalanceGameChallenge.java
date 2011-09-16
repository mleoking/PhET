// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * Base class for a single "challenge" (a.k.a. problem) that is presented to
 * the user during the balance game.
 *
 * @author John Blanco
 */
public abstract class BalanceGameChallenge {

    // List of masses that will initially be sitting on the balance, and which
    // the user will not manipulate.
    public final List<MassDistancePair> fixedMasses = new ArrayList<MassDistancePair>();

    // List of masses that the user will move into the appropriate positions
    // in order to balance out the other masses.
    public final List<Mass> movableMasses = new ArrayList<Mass>();

    // Solution to show to the user if they are unable to solve the challenge.
    // Some challenges could potentially have multiple solutions, so this is
    // only the one that we have chosen to present, and not necessarily the
    // only correct one.
    public final List<MassDistancePair> solutionToPresent = new ArrayList<MassDistancePair>();

    // State of the support column or columns when the challenge is initially
    // presented to the user.
    public final ColumnState initialColumnState;

    public BalanceGameChallenge( ColumnState initialColumnState ) {
        this.initialColumnState = initialColumnState;
    }


    @Override public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        BalanceChallenge that = (BalanceChallenge) o;

        for ( MassDistancePair massDistancePair : fixedMasses ) {
            if ( !that.fixedMasses.contains( massDistancePair ) ) {
                return false;
            }
        }

        if ( movableMasses.size() != that.movableMasses.size() ) {
            return false;
        }

        List<Mass> copyOfThatMovableMasses = new ArrayList<Mass>( that.movableMasses );
        for ( Mass thisMass : movableMasses ) {
            for ( Mass thatMass : new ArrayList<Mass>( copyOfThatMovableMasses ) ) {
                if ( thisMass.getMass() == thatMass.getMass() ) {
                    if ( copyOfThatMovableMasses.contains( thatMass ) ) {
                        copyOfThatMovableMasses.remove( thatMass );
                        break;
                    }
                }
            }
        }

        if ( copyOfThatMovableMasses.size() != 0 ) {
            return false;
        }

        if ( !solutionToPresent.equals( that.solutionToPresent ) ) {
            return false;
        }

        return true;
    }

    /**
     * Convenience class for pairing a mass with a distance from the center
     * of the balancing apparatus.
     */
    public static class MassDistancePair {
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
}
