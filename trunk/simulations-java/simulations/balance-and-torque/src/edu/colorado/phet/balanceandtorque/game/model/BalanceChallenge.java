// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * A challenge, used in the balance game, in which the user must attempt to
 * place a movable mass in the correct so that when the support column is
 * removed, the plank will become balanced.
 *
 * @author John Blanco
 */
public class BalanceChallenge extends BalanceGameChallenge {

    /**
     * Constructor.
     *
     * @param fixedMasses
     * @param movableMasses
     * @param initialColumnState
     */
    public BalanceChallenge( List<MassDistancePair> fixedMasses, List<Mass> movableMasses, List<MassDistancePair> solutionToDisplay, ColumnState initialColumnState ) {
        super( initialColumnState );
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

}
