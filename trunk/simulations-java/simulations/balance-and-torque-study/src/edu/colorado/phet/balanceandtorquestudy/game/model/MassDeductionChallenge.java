// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources;
import edu.colorado.phet.balanceandtorquestudy.common.model.ColumnState;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.ModelComponentTypes.massDeductionChallenge;

/**
 * A challenge, used in the balance game, in which the user attempts to
 * deduce the mass of a "mystery mass" using another mass of a known value.
 *
 * @author John Blanco
 */
public class MassDeductionChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( BalanceAndTorqueStudyResources.Strings.WHAT_IS_THE_MASS, true, false );

    /**
     * Constructor.
     *
     * @param fixedMasses
     * @param movableMasses
     * @param solutionToDisplay
     */
    public MassDeductionChallenge( final MassDistancePair fixedMasses, List<Mass> movableMasses, List<MassDistancePair> solutionToDisplay ) {
        super( ColumnState.NONE );
        List<MassDistancePair> fixedMassList = new ArrayList<MassDistancePair>() {{
            add( fixedMasses );
        }};
        this.fixedMassDistancePairs.addAll( fixedMassList );
        this.movableMasses.addAll( movableMasses );
        this.balancedConfiguration.addAll( solutionToDisplay );
    }

    @Override public ChallengeViewConfig getChallengeViewConfig() {
        return VIEW_CONFIG;
    }

    @Override public IModelComponentType getModelComponentType() {
        return massDeductionChallenge;
    }

    @Override public String getCorrectAnswerString() {
        assert fixedMassDistancePairs.size() == 1;
        return Double.toString( fixedMassDistancePairs.get( 0 ).mass.getMass() );
    }

    /**
     * Convenience function for create a mass deduction challenge.
     */
    public static MassDeductionChallenge create( Mass mysteryMass, double mysteryMassDistanceFromCenter, Mass knownMass ) {

        // Create the mass-distance pair for the mystery mass.
        MassDistancePair mysteryMassDistancePair = new MassDistancePair( mysteryMass, mysteryMassDistanceFromCenter );

        // Put the known mass on to a list.
        List<Mass> knownMassesList = new ArrayList<Mass>();
        knownMassesList.add( knownMass );

        // Create a valid solution for the challenge.
        List<MassDistancePair> solution = new ArrayList<MassDistancePair>();
        solution.add( new MassDistancePair( knownMass, -mysteryMass.getMass() * mysteryMassDistanceFromCenter / knownMass.getMass() ) );

        // Combine into challenge.
        return new MassDeductionChallenge( mysteryMassDistancePair, knownMassesList, solution );
    }
}
