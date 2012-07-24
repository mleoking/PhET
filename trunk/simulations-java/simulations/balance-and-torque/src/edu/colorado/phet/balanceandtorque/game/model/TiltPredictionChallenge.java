// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;

/**
 * A challenge, used in the balance game, in which the user must predict which
 * way the plank will tilt when the supports are removed.
 *
 * @author John Blanco
 */
public class TiltPredictionChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( "What will happen?", false, true );

    // Number of attempts that the user gets to answer this challenge.
    private static final int NUM_ATTEMPTS_ALLOWED = 2;

    /**
     * Constructor.
     *
     * @param fixedMasses
     */
    public TiltPredictionChallenge( List<MassDistancePair> fixedMasses ) {
        super( ColumnState.DOUBLE_COLUMNS, NUM_ATTEMPTS_ALLOWED );
        this.fixedMassDistancePairs.addAll( fixedMasses );
        this.movableMasses.addAll( movableMasses );
    }

    @Override public ChallengeViewConfig getChallengeViewConfig() {
        return VIEW_CONFIG;
    }

    @Override public IModelComponentType getModelComponentType() {
        return BalanceAndTorqueSimSharing.ModelComponentTypes.tiltPredictionChallenge;
    }

    /**
     * Convenience method for creating this type of challenge.
     */
    public static TiltPredictionChallenge create( Mass fixedMass1, double fixedMass1DistanceFromCenter,
                                                  Mass fixedMass2, double fixedMass2DistanceFromCenter ) {
        // Add the fixed masses and their distances from the center of the balance.
        List<MassDistancePair> fixedMassesList = new ArrayList<MassDistancePair>();
        MassDistancePair fixedMassDistancePair1 = new MassDistancePair( fixedMass1, -fixedMass1DistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair1 );
        MassDistancePair fixedMassDistancePair2 = new MassDistancePair( fixedMass2, -fixedMass2DistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair2 );

        // Create the actual challenge.
        return new TiltPredictionChallenge( fixedMassesList );
    }
}
