// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * A single challenge, a.k.a. a problem, for the balance game.  It consists of
 * weights and positions that go on the plank that the user is expected to
 * attempt to balance.
 *
 * @author John Blanco
 */
public class BalanceChallenge {

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

    /**
     * Constructor.
     *
     * @param fixedMasses
     * @param movableMasses
     */
    public BalanceChallenge( List<MassDistancePair> fixedMasses, List<Mass> movableMasses, List<MassDistancePair> solutionToDisplay ) {
        this.fixedMasses.addAll( fixedMasses );
        this.movableMasses.addAll( movableMasses );
        this.solutionToPresent.addAll( solutionToDisplay );
        // Parameter checking: Verify that the mass or masses used in the
        // solution are present on the list of movable masses.
        for ( MassDistancePair massDistancePair : solutionToDisplay ) {
            if ( !movableMasses.contains( massDistancePair.mass ) ) {
                throw ( new IllegalArgumentException( "One or more of the masses in the solution are not on the list of movable masses." ) );
            }
        }
    }

    @Override
    public boolean equals( Object o ) {
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
