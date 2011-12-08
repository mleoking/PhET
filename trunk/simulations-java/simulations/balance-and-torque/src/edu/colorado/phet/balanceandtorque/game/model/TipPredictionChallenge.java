// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;

/**
 * A challenge, used in the balance game, in which the user must predict which
 * way the plank will tip when the supports are removed.
 *
 * @author John Blanco
 */
public class TipPredictionChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( "What will happen?", false, true );

    // This type of challenge can only be attempted once.
    private static final int NUM_ATTEMPTS_ALLOWED = 1;

    /**
     * Constructor.
     *
     * @param fixedMasses
     */
    public TipPredictionChallenge( List<MassDistancePair> fixedMasses ) {
        super( ColumnState.DOUBLE_COLUMNS, NUM_ATTEMPTS_ALLOWED );
        this.fixedMassDistancePairs.addAll( fixedMasses );
        this.movableMasses.addAll( movableMasses );
    }

    @Override public ChallengeViewConfig getChallengeViewConfig() {
        return VIEW_CONFIG;
    }

    /**
     * Convenience method for creating this type of challenge.
     */
    public static TipPredictionChallenge create( Mass fixedMass1, double fixedMass1DistanceFromCenter,
                                                 Mass fixedMass2, double fixedMass2DistanceFromCenter ) {
        // Add the fixed masses and their distances from the center of the balance.
        List<MassDistancePair> fixedMassesList = new ArrayList<MassDistancePair>();
        MassDistancePair fixedMassDistancePair1 = new MassDistancePair( fixedMass1, -fixedMass1DistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair1 );
        MassDistancePair fixedMassDistancePair2 = new MassDistancePair( fixedMass2, -fixedMass2DistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair2 );

        // Create the actual challenge.
        return new TipPredictionChallenge( fixedMassesList );
    }
}
