// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BigRock;
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

    // Max number of attempts to generate a workable or unique challenge.
    private static final int MAX_GEN_ATTEMPTS = 100;

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
                    BalanceChallenge balanceChallenge = null;
                    for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {
                        balanceChallenge = generateChallengeSimpleRatioBricks();
                        if ( !balanceChallengeList.contains( balanceChallenge ) ) {
                            // This is a unique one, so we're done.
                            break;
                        }
                        assert j < MAX_GEN_ATTEMPTS - 1; // Catch it if we ever can't find a unique challenge.
                    }
                    balanceChallengeList.add( balanceChallenge );
                }
            }
        }
        else if ( level == 2 ) {
            for ( int i = 0; i < numChallenges; i++ ) {
                BalanceChallenge balanceChallenge = null;
                for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {
                    balanceChallenge = generateChallengeAdvancedRatioBricks();
                    if ( !balanceChallengeList.contains( balanceChallenge ) ) {
                        // This is a unique one, so we're done.
                        break;
                    }
                    assert j < MAX_GEN_ATTEMPTS - 1; // Catch it if we ever can't find a unique challenge.
                }
                balanceChallengeList.add( balanceChallenge );
            }
        }
        else {
            // This level is either out of range or not implemented yet.
            throw new IllegalArgumentException( "Challenge level invalid, value = " + level );
        }

        return balanceChallengeList;
    }

    /**
     * Create a simple challenge where equal mass brick stacks appear on each
     * side.
     *
     * @return
     */
    private static BalanceChallenge generateChallengeEqualMassBricksEachSide() {
        int numBricks = 1 + RAND.nextInt( 3 );
        double distance = -generateRandomValidPlankDistance();

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricks, numBricks, distance );
    }

    private static BalanceChallenge generateRockToBrickChallenge() {
        // Add the fixed mass.
        List<BalanceChallenge.MassDistancePair> fixedMassesList = new ArrayList<BalanceChallenge.MassDistancePair>();
        BalanceChallenge.MassDistancePair fixedMass = new BalanceChallenge.MassDistancePair( new BigRock(), -1.5 );
        fixedMassesList.add( fixedMass );

        // Add the movable mass.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        BrickStack movableMass = new BrickStack( 3 );
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
            validFixedStackDistances.addAll( getPossibleDistanceList( numBricksInFixedStack * BrickStack.BRICK_MASS, numBricksInMovableStack * BrickStack.BRICK_MASS ) );

            // TODO: For debug.
            if ( validFixedStackDistances.size() == 0 ) {
                System.out.println( "Warning: No solutions for this configuration, numBricksInFixedStack = " + numBricksInFixedStack + ", " + "numBricksInMovableStack = " + numBricksInMovableStack );
            }
        }

        // Randomly choose a distance to use from the identified set.
        double fixedStackDistanceFromCenter = -validFixedStackDistances.get( RAND.nextInt( validFixedStackDistances.size() ) );

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricksInFixedStack, numBricksInMovableStack, fixedStackDistanceFromCenter );
    }

    private static BalanceChallenge generateChallengeAdvancedRatioBricks() {

        int numBricksInFixedStack = 1;
        int numBricksInMovableStack = 1;
        List<Double> validFixedStackDistances = new ArrayList<Double>();

        // Iterate through various combinations of fixed and movable masses
        // until a solvable combination is found.
        while ( validFixedStackDistances.size() == 0 ) {
            // Randomly choose the number of bricks in the fixed stack.
            numBricksInFixedStack = RAND.nextInt( 4 ) + 1;

            // Randomly choose the number of bricks in the movable stack, but
            // avoid the same number.
            numBricksInMovableStack = numBricksInFixedStack;
            while ( numBricksInMovableStack == numBricksInFixedStack ) {
                numBricksInMovableStack = RAND.nextInt( 4 ) + 1;
            }

            // Create a list of the distances at which the fixed stack may be
            // positioned that can be made to balance with the movable stack.
            validFixedStackDistances.addAll( getPossibleDistanceList( numBricksInFixedStack * BrickStack.BRICK_MASS, numBricksInMovableStack * BrickStack.BRICK_MASS ) );

            // TODO: For debug.  I (jblanco) just want to see if this happens and, if so, how often.
            if ( validFixedStackDistances.size() == 0 ) {
                System.out.println( "Warning: No solutions for this configuration, numBricksInFixedStack = " + numBricksInFixedStack + ", " + "numBricksInMovableStack = " + numBricksInMovableStack );
            }
        }

        // Randomly choose a distance to use from the identified set.
        double fixedStackDistanceFromCenter = -validFixedStackDistances.get( RAND.nextInt( validFixedStackDistances.size() ) );

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricksInFixedStack, numBricksInMovableStack, fixedStackDistanceFromCenter );
    }

    private static BalanceChallenge createTwoBrickStackChallenge( int numBricksInFixedStack, int numBricksInMovableStack, double fixedStackDistanceFromCenter ) {
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
     * Get a list of the distances at which the fixed mass could be positioned
     * that would allow the movable mass to be positioned somewhere on the
     * other side of the fulcrum and balance the fixed mass.
     */
    private static List<Double> getPossibleDistanceList( double massOfFixedItem, double massOfMovableItem ) {
        List<Double> validFixedMassDistances = new ArrayList<Double>();
        for ( double testDistance = Plank.INTER_SNAP_TO_MARKER_DISTANCE; testDistance < Plank.getLength() / 2; testDistance += Plank.INTER_SNAP_TO_MARKER_DISTANCE ) {
            double possibleFixedMassDistance = testDistance * massOfMovableItem / massOfFixedItem;
            if ( possibleFixedMassDistance < Plank.getLength() / 2 &&
                 possibleFixedMassDistance >= Plank.INTER_SNAP_TO_MARKER_DISTANCE - 1E-6 &&
                 possibleFixedMassDistance % Plank.INTER_SNAP_TO_MARKER_DISTANCE < 1E-6 ) {
                // This is a valid distance.
                validFixedMassDistances.add( possibleFixedMassDistance );
            }
        }
        return validFixedMassDistances;
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
}
