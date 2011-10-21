// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.List;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;

/**
 * A challenge, used in the balance game, in which the user must attempt to
 * place a movable mass in the correct so that when the support column is
 * removed, the movable mass will balance the fixed mass that is initially on
 * the plank.
 *
 * @author John Blanco
 */
public class BalanceMassesChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( BalanceAndTorqueResources.Strings.BALANCE_ME, false );

    /**
     * Constructor.
     *
     * @param fixedMasses
     * @param movableMasses
     */
    public BalanceMassesChallenge( List<MassDistancePair> fixedMasses, List<Mass> movableMasses, List<MassDistancePair> solutionToDisplay ) {
        super( ColumnState.SINGLE_COLUMN );
        this.fixedMassDistancePairs.addAll( fixedMasses );
        this.movableMasses.addAll( movableMasses );
        this.balancedConfiguration.addAll( solutionToDisplay );
        // Parameter checking: Verify that the mass or masses used in the
        // solution are present on the list of movable masses.
        for ( MassDistancePair massDistancePair : solutionToDisplay ) {
            if ( !movableMasses.contains( massDistancePair.mass ) ) {
                throw ( new IllegalArgumentException( "One or more of the masses in the solution are not on the list of movable masses." ) );
            }
        }
    }

    @Override public ChallengeViewConfig getChallengeViewConfig() {
        return VIEW_CONFIG;
    }
}
