// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BrickStack;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * This class is a factory pattern class that generates sets of challenges for
 * the balance game.
 *
 * @author John Blanco
 */
public class BalanceChallengeSetFactory {

    private static final Random RAND = new Random();

    // Lists of ratios to use when generating challenges.
    private static final double[] SIMPLE_RATIO_LIST = new double[] { 0.5, 1, 2 };
    private static final double[] ADVANCED_RATIO_LIST = new double[] { ( 1 / 3 ), 0.5, ( 2 / 3 ), 1, 2, 1.5, 3 };

    /**
     * Get a set of challenges for the provided level.
     *
     * @param level
     * @param numChallenges
     * @return
     */
    public static List<BalanceChallenge> getChallengeSet( int level, int numChallenges ) {
        List<BalanceChallenge> balanceChallengeList = new ArrayList<BalanceChallenge>();
        if ( level == 1 ) {
            for ( int i = 0; i < numChallenges; i++ ) {
                if ( i == 0 ) {
                    // It was requested by the design team that the first
                    // problem of a level 1 set always be a simple 1:1 problem.
                    balanceChallengeList.add( generateChallengeEqualMassBricksEachSide() );
                }
                else {
                    balanceChallengeList.add( generateChallengeSimpleRatioBricks() );
                }
            }
        }
        else if ( level == 2 ) {
            for ( int i = 0; i < numChallenges; i++ ) {
                if ( i == 0 ) {
                    // User a simpler ratio problem for the first on the list.
                    balanceChallengeList.add( generateChallengeSimpleRatioBricks() );
                }
                else {
                    balanceChallengeList.add( generateChallengeAdvancedRatioBricks() );
                }
            }
        }
        else {
            // Not implemented yet.
            assert false;
        }
        return balanceChallengeList;
    }

    private static BalanceChallenge generateChallengeEqualMassBricksEachSide() {
        int numBricks = 1 + RAND.nextInt( 3 );
        double distance = -generateRandomValidPlankDistance();

        // Add the fixed brick stack.
        List<BalanceChallenge.MassDistancePair> fixedMassesList = new ArrayList<BalanceChallenge.MassDistancePair>();
        BalanceChallenge.MassDistancePair fixedMass = new BalanceChallenge.MassDistancePair( new BrickStack( numBricks ), distance );
        fixedMassesList.add( fixedMass );

        // Add the movable brick stack.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        BrickStack movableMass = new BrickStack( numBricks );
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<BalanceChallenge.MassDistancePair> solution = new ArrayList<BalanceChallenge.MassDistancePair>();
        solution.add( new BalanceChallenge.MassDistancePair( movableMass, -fixedMass.mass.getMass() * fixedMass.distance / movableMass.getMass() ) );

        // And we're done.
        return new BalanceChallenge( fixedMassesList, movableMassesList, solution );
    }

    /**
     * Generate a challenge that consists of brick stacks in simple ratios to
     * one another.  For instance, the fixed brick stack might be 2 bricks,
     * and the movable state be one brick.
     * <p/>
     * Ratios used are 1:1, 2:1, or 1:2.
     *
     * @return
     */
    private static BalanceChallenge generateChallengeSimpleRatioBricks() {

        int numBricksInFixedStack = 1;
        int numBricksInMovableStack = 1;
        List<Double> validFixedStackDistances = new ArrayList<Double>();

        while ( validFixedStackDistances.size() == 0 ) {
            // Choose the number of bricks in the fixed stack.  Must be 1, 2, or 4
            // in order to support the ratios used.
            numBricksInFixedStack = (int) Math.pow( 2, RAND.nextInt( 3 ) );

            // Decide on number of bricks in movable stack.
            numBricksInMovableStack = (int) Math.round( numBricksInFixedStack * SIMPLE_RATIO_LIST[RAND.nextInt( SIMPLE_RATIO_LIST.length )] );

            // Create a list of the distances at which the fixed stack may be
            // positioned that can be made to balance with the movable stack.
            for ( double testDistance = Plank.INTER_SNAP_TO_MARKER_DISTANCE; testDistance < Plank.getLength() / 2; testDistance += Plank.INTER_SNAP_TO_MARKER_DISTANCE ) {
                double possibleFixedStackDistance = testDistance * numBricksInMovableStack / numBricksInFixedStack;
                if ( possibleFixedStackDistance < Plank.getLength() / 2 &&
                     possibleFixedStackDistance >= Plank.INTER_SNAP_TO_MARKER_DISTANCE - 1E-6 &&
                     possibleFixedStackDistance % Plank.INTER_SNAP_TO_MARKER_DISTANCE < 1E-6 ) {
                    // This is a valid distance.
                    validFixedStackDistances.add( possibleFixedStackDistance );
                }
            }

            // TODO: For debug.
            if ( validFixedStackDistances.size() == 0 ) {
                System.out.println( "Warning: No solutions for this configuration, numBricksInFixedStack = " + numBricksInFixedStack + ", " + "numBricksInMovableStack = " + numBricksInMovableStack );
            }
        }

        // Randomly choose a distance to use from the identified set.
        double fixedStackDistanceFromCenter = -validFixedStackDistances.get( RAND.nextInt( validFixedStackDistances.size() ) );

        // Add the fixed brick stack.
        List<BalanceChallenge.MassDistancePair> fixedMassesList = new ArrayList<BalanceChallenge.MassDistancePair>();
        BalanceChallenge.MassDistancePair fixedMass = new BalanceChallenge.MassDistancePair( new BrickStack( numBricksInFixedStack ), fixedStackDistanceFromCenter );
        fixedMassesList.add( fixedMass );

        // Add the movable brick stack.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        BrickStack movableMass = new BrickStack( numBricksInMovableStack );
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<BalanceChallenge.MassDistancePair> solution = new ArrayList<BalanceChallenge.MassDistancePair>();
        solution.add( new BalanceChallenge.MassDistancePair( movableMass, -fixedMass.mass.getMass() * fixedMass.distance / movableMass.getMass() ) );

        // And we're done.
        return new BalanceChallenge( fixedMassesList, movableMassesList, solution );
    }

    private static BalanceChallenge generateChallengeAdvancedRatioBricks() {

        int numBricksInFixedStack = 1;
        int numBricksInMovableStack = 1;
        List<Double> validFixedStackDistances = new ArrayList<Double>();

        // Iterate through various combinations until one is found that can be
        // solved.
        while ( validFixedStackDistances.size() == 0 ) {
            // Randomly choose the number of bricks in the fixed stack.
            numBricksInFixedStack = RAND.nextInt( 4 ) + 1;

            // Randomly choose the number of bricks in the movable stack.
            numBricksInMovableStack = RAND.nextInt( 4 ) + 1;

            // Create a list of the distances at which the fixed stack may be
            // positioned that can be made to balance with the movable stack.
            for ( double testDistance = Plank.INTER_SNAP_TO_MARKER_DISTANCE; testDistance < Plank.getLength() / 2; testDistance += Plank.INTER_SNAP_TO_MARKER_DISTANCE ) {
                double possibleFixedStackDistance = testDistance * numBricksInMovableStack / numBricksInFixedStack;
                if ( possibleFixedStackDistance < Plank.getLength() / 2 &&
                     possibleFixedStackDistance >= Plank.INTER_SNAP_TO_MARKER_DISTANCE - 1E-6 &&
                     possibleFixedStackDistance % Plank.INTER_SNAP_TO_MARKER_DISTANCE < 1E-6 ) {
                    // This is a valid distance.
                    validFixedStackDistances.add( possibleFixedStackDistance );
                }
            }

            // TODO: For debug.
            if ( validFixedStackDistances.size() == 0 ) {
                System.out.println( "Warning: No solutions for this configuration, numBricksInFixedStack = " + numBricksInFixedStack + ", " + "numBricksInMovableStack = " + numBricksInMovableStack );
            }
        }

        // Randomly choose a distance to use from the identified set.
        double fixedStackDistanceFromCenter = -validFixedStackDistances.get( RAND.nextInt( validFixedStackDistances.size() ) );

        // Add the fixed brick stack.
        List<BalanceChallenge.MassDistancePair> fixedMassesList = new ArrayList<BalanceChallenge.MassDistancePair>();
        BalanceChallenge.MassDistancePair fixedMass = new BalanceChallenge.MassDistancePair( new BrickStack( numBricksInFixedStack ), fixedStackDistanceFromCenter );
        fixedMassesList.add( fixedMass );

        // Add the movable brick stack.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        BrickStack movableMass = new BrickStack( numBricksInMovableStack );
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<BalanceChallenge.MassDistancePair> solution = new ArrayList<BalanceChallenge.MassDistancePair>();
        solution.add( new BalanceChallenge.MassDistancePair( movableMass, -fixedMass.mass.getMass() * fixedMass.distance / movableMass.getMass() ) );

        // And we're done.
        return new BalanceChallenge( fixedMassesList, movableMassesList, solution );
    }

    /**
     * Convenience function that generates a valid random distance from the
     * center of the plank where a mass can be positioned.  The plank only
     * allows discrete distances, which is why this is needed.
     *
     * @return
     */
    private static double generateRandomValidPlankDistance() {
        double maxDistance = Plank.getLength() / 2;
        double increment = Plank.INTER_SNAP_TO_MARKER_DISTANCE;
        int maxIncrements = (int) Math.round( maxDistance / increment ) - 1;
        return ( RAND.nextInt( maxIncrements ) + 1 ) * increment;
    }

    public static void main( String[] args ) {
        for ( int i = 0; i < 10; i++ ) {
            System.out.println( generateRandomValidPlankDistance() );
        }
    }
}
