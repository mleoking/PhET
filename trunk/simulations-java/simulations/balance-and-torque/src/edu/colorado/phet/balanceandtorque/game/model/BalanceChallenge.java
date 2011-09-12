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
    public final List<MassDistancePair> massesToBeBalanced = new ArrayList<MassDistancePair>();

    // List of masses that the user will move into the appropriate positions
    // in order to balance out the other masses.
    public final List<Mass> movableMasses = new ArrayList<Mass>();

    /**
     * Constructor.
     *
     * @param massesToBeBalanced
     * @param movableMasses
     */
    public BalanceChallenge( List<MassDistancePair> massesToBeBalanced, List<Mass> movableMasses ) {
        this.massesToBeBalanced.addAll( massesToBeBalanced );
        this.movableMasses.addAll( movableMasses );
    }

    /**
     * Convenience class for pairing a mass with a distance from the center
     * of the balancing apparatus.
     */
    public static class MassDistancePair {
        public final Mass mass;       // In kg.
        public final double distance; // In meters.

        public MassDistancePair( Mass mass, double distance ) {
            this.mass = mass;
            this.distance = distance;
        }
    }
}
