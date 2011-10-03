// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * A challenge, used in the balance game, in which the user must attempt to
 * place a movable mass in the correct so that when the support column is
 * removed, the movable mass will balance the fixed mass that is initially on
 * the plank.
 *
 * @author John Blanco
 */
public class DeduceTheMassChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( BalanceAndTorqueResources.Strings.WHAT_IS_THE_MASS, true );

    /**
     * Constructor.
     *
     * @param fixedMasses
     * @param movableMasses
     */
    public DeduceTheMassChallenge( final MassDistancePair fixedMasses, List<Mass> movableMasses, List<MassDistancePair> solutionToDisplay ) {
        super( ColumnState.NONE );
        List<MassDistancePair> fixedMassList = new ArrayList<MassDistancePair>() {{
            add( fixedMasses );
        }};
        this.fixedMasses.addAll( fixedMassList );
        this.movableMasses.addAll( movableMasses );
        this.balancedConfiguration.addAll( solutionToDisplay );
    }

    @Override public ChallengeViewConfig getChallengeViewConfig() {
        return VIEW_CONFIG;
    }
}
