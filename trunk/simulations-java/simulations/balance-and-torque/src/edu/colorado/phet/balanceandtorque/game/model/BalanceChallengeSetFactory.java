// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BrickStack;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * This class is a factory pattern class that generates sets of challenges for
 * the balance game.
 *
 * @author John Blanco
 */
public class BalanceChallengeSetFactory {

    public static List<BalanceChallenge> getChallengeSet( int level, int numChallenges ) {
        List<BalanceChallenge> balanceChallengeList = new ArrayList<BalanceChallenge>();
        for ( int i = 0; i < numChallenges; i++ ) {
            balanceChallengeList.add( generateBalanceChallengeOneEqualMassEachSide() );
        }
        return balanceChallengeList;
    }

    private static BalanceChallenge generateBalanceChallengeOneEqualMassEachSide() {
        // TODO: Always creates the same one now.

        // Create the list of fixed masses.
        List<BalanceChallenge.MassDistancePair> fixedMassesList = new ArrayList<BalanceChallenge.MassDistancePair>();
        fixedMassesList.add( new BalanceChallenge.MassDistancePair( new BrickStack( 2 ), -1 ) );

        // Create the list of user-movable masses.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        movableMassesList.add( new BrickStack( 4 ) );

        return new BalanceChallenge( fixedMassesList, movableMassesList );
    }
}
