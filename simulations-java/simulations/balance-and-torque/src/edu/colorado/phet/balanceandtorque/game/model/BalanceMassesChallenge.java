// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.balanceandtorque.common.BalanceAndTorqueSharedConstants;
import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.Plank;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.DISTANCE_VALUE_FORMATTER;
import static edu.colorado.phet.balanceandtorque.common.BalanceAndTorqueSharedConstants.USE_QUARTER_METER_INCREMENTS;

/**
 * A challenge, used in the balance game, in which the user must attempt to
 * place a movable mass in the correct so that when the support column is
 * removed, the movable mass will balance the fixed mass that is initially on
 * the plank.
 *
 * @author John Blanco
 */
public class BalanceMassesChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( BalanceAndTorqueResources.Strings.BALANCE_ME, false, false );

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

    @Override public IModelComponentType getModelComponentType() {
        return BalanceAndTorqueSimSharing.ModelComponentTypes.balanceMassesChallenge;
    }

    /**
     * Convenience method for creating this type of challenge.
     */
    public static BalanceMassesChallenge create( Mass fixedMass, double fixedMassDistanceFromCenter, Mass movableMass ) {

        // Add the fixed mass and its distance from the center of the balance.
        List<MassDistancePair> fixedMassesList = new ArrayList<MassDistancePair>();
        MassDistancePair fixedMassDistancePair = new MassDistancePair( fixedMass, fixedMassDistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair );

        // Add the movable mass.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<MassDistancePair> solution = new ArrayList<MassDistancePair>();
        solution.add( new MassDistancePair( movableMass, -fixedMassDistancePair.mass.getMass() * fixedMassDistancePair.distance / movableMass.getMass() ) );

        // And we're done.
        return new BalanceMassesChallenge( fixedMassesList, movableMassesList, solution );
    }

    /**
     * Convenience method for creating this type of challenge.
     */
    public static BalanceMassesChallenge create( Mass fixedMass1, double fixedMass1DistanceFromCenter,
                                                 Mass fixedMass2, double fixedMass2DistanceFromCenter, Mass movableMass ) {
        // Add the fixed masses and their distances from the center of the balance.
        List<MassDistancePair> fixedMassesList = new ArrayList<MassDistancePair>();
        MassDistancePair fixedMassDistancePair1 = new MassDistancePair( fixedMass1, -fixedMass1DistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair1 );
        MassDistancePair fixedMassDistancePair2 = new MassDistancePair( fixedMass2, -fixedMass2DistanceFromCenter );
        fixedMassesList.add( fixedMassDistancePair2 );

        // Add the movable mass.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<MassDistancePair> solutionList = new ArrayList<MassDistancePair>();
        double fixedMassTorque = fixedMassDistancePair1.mass.getMass() * fixedMassDistancePair1.distance +
                                 fixedMassDistancePair2.mass.getMass() * fixedMassDistancePair2.distance;
        MassDistancePair solution = new MassDistancePair( movableMass, -fixedMassTorque / movableMass.getMass() );
        assert solution.distance % Plank.INTER_SNAP_TO_MARKER_DISTANCE == 0; // Verify that this is really a workable solution.
        solutionList.add( solution );

        // Create the actual challenge.
        return new BalanceMassesChallenge( fixedMassesList, movableMassesList, solutionList );
    }

    @Override public String getCorrectAnswerString() {
        // NOTE: It is conceivable that challenges where multiple masses need
        // to be placed in multiple locations could be supported, but as of
        // this writing, they are not.  If they ever are, the assertion below
        // will fail, and this method will need enhancement.
        assert movableMasses.size() == 1;
        for ( MassDistancePair massDistancePair : balancedConfiguration ) {
            if ( massDistancePair.distance > 0 ){
                return DISTANCE_VALUE_FORMATTER.format( USE_QUARTER_METER_INCREMENTS ? massDistancePair.distance * 4 : massDistancePair.distance );
            }
        }
        return "NoSolution";
    }
}
