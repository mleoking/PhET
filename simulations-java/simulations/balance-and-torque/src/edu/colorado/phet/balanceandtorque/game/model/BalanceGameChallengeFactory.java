// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BigRock;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Boy;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.BrickStack;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Girl;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Man;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.MysteryObjectFactory;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.SmallRock;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Woman;

/**
 * This class is a factory pattern class that generates sets of challenges for
 * the balance game.
 * <p/>
 * Note: In this class, the terminology used to distinguish between the various
 * levels of difficulty for the challenges are (in order of increasing
 * difficulty)
 * - Simple
 * - Easy
 * - Moderate
 * - Hard
 * This is not to be confused with the numerical game levels, since there is
 * not necessarily a 1 to 1 correspondence between the numerical levels and
 * the difficulty of a given challenge.
 *
 * @author John Blanco
 */
public class BalanceGameChallengeFactory {

    private static final Random RAND = new Random();

    // Max number of attempts to generate a workable or unique challenge.
    private static final int MAX_GEN_ATTEMPTS = 100;

    // Tolerance value used when comparing floating-point calculations.
    private static final double COMPARISON_TOLERANCE = 1E-6;

    // Determine the min and max distances from the center of the plank where
    // masses may be positioned.
    private static final double MIN_DISTANCE_FROM_BALANCE_CENTER_TO_MASS = Plank.INTER_SNAP_TO_MARKER_DISTANCE;
    private static final double MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS = ( Math.round( Plank.getLength() / Plank.INTER_SNAP_TO_MARKER_DISTANCE / 2 ) - 1 ) * Plank.INTER_SNAP_TO_MARKER_DISTANCE;

    // List of masses that can be used in balance challenges.  This list is not
    // used for creating all challenges, just the more advanced ones.
    private static final List<Mass> BALANCE_CHALLENGE_MASSES = new ArrayList<Mass>() {{
        add( new BrickStack( 1 ) );
        add( new BrickStack( 2 ) );
        add( new BrickStack( 3 ) );
        add( new BrickStack( 4 ) );
        add( new SmallRock() );
        add( new BigRock() );
        add( new Boy() );
        add( new Girl() );
        add( new Woman() );
        add( new Man() );
    }};

    private static final List<Mass> MYSTERY_MASSES = new ArrayList<Mass>() {{
        for ( int i = 0; i < MysteryObjectFactory.getNumAvailableMysteryObjects(); i++ ) {
            add( MysteryObjectFactory.createUnlabeledMysteryObject( i ) );
        }
    }};

    /**
     * Get a set of challenges for the provided level.
     *
     * @param level
     * @param numChallenges
     * @return
     */
    public static List<BalanceGameChallenge> getChallengeSet( int level, int numChallenges ) {
        List<BalanceGameChallenge> balanceChallengeList = new ArrayList<BalanceGameChallenge>();
        if ( level == 1 ) {
            for ( int i = 0; i < numChallenges; i++ ) {
                if ( i == 0 ) {
                    // It was requested by the design team that the first
                    // problem of a level 1 set always be a simple 1:1 problem.
//                    balanceChallengeList.add( generateSimpleBalanceChallenge() );
                    balanceChallengeList.add( generateSimpleDeduceTheMassChallenge() );
                }
                else {
                    BalanceGameChallenge balanceChallenge = null;
                    for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {
                        balanceChallenge = generateEasyBalanceChallenge();
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
                BalanceGameChallenge balanceChallenge = null;
                for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {
                    balanceChallenge = generateModerateBalanceChallenge();
                    if ( !balanceChallengeList.contains( balanceChallenge ) ) {
                        // This is a unique one, so we're done.
                        break;
                    }
                    assert j < MAX_GEN_ATTEMPTS - 1; // Catch it if we ever can't find a unique challenge.
                }
                balanceChallengeList.add( balanceChallenge );
            }
        }
        else if ( level == 3 ) {
            for ( int i = 0; i < numChallenges; i++ ) {
                BalanceGameChallenge balanceChallenge = null;
                for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {
                    balanceChallenge = generateChallengeRandomMasses();
                    if ( !balanceChallengeList.contains( balanceChallenge ) ) {
                        // This is a unique one, so we're done.
                        break;
                    }
                    assert j < MAX_GEN_ATTEMPTS - 1; // Catch it if we ever can't find a unique challenge.
                }
                balanceChallengeList.add( balanceChallenge );
            }
        }
        else if ( level == 4 ) {
            for ( int i = 0; i < numChallenges; i++ ) {
                BalanceGameChallenge balanceChallenge = null;
                for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {
                    balanceChallenge = generateChallengeSimpleDeduceMass( j );
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
     * Create a simple challenge where brick stacks of equal mass appear on
     * each side.
     */
    private static BalanceMassesChallenge generateSimpleBalanceChallenge() {
        int numBricks = 1 + RAND.nextInt( 3 );
        double distance = -generateRandomValidPlankDistance();

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricks, distance, numBricks );
    }

    /**
     * Generate a challenge that consists of brick stacks in simple ratios to
     * one another.  For instance, the fixed brick stack might be 2 bricks,
     * and the movable state be one brick.
     * <p/>
     * Ratios used are 2:1 or 1:2.
     *
     * @return
     */
    private static BalanceMassesChallenge generateEasyBalanceChallenge() {

        int numBricksInFixedStack = 1;
        int numBricksInMovableStack = 1;
        List<Double> validFixedStackDistances = new ArrayList<Double>();

        while ( validFixedStackDistances.size() == 0 ) {
            // Choose the number of bricks in the fixed stack.  Must be 1, 2, or 4
            // in order to support the ratios used.
            numBricksInFixedStack = (int) Math.pow( 2, RAND.nextInt( 3 ) );

            // Decide on number of bricks in movable stack.
            if ( numBricksInFixedStack == 1 || RAND.nextBoolean() ) {
                numBricksInMovableStack = 2 * numBricksInFixedStack;
            }
            else {
                numBricksInMovableStack = numBricksInFixedStack / 2;
            }

            // Create a list of the distances at which the fixed stack may be
            // positioned that can be made to balance with the movable stack.
            validFixedStackDistances.addAll( getPossibleDistanceList( numBricksInFixedStack * BrickStack.BRICK_MASS, numBricksInMovableStack * BrickStack.BRICK_MASS ) );

            // TODO: For debug, remove eventually.
            if ( validFixedStackDistances.size() == 0 ) {
                System.out.println( "Warning: No solutions for this configuration, numBricksInFixedStack = " + numBricksInFixedStack + ", " + "numBricksInMovableStack = " + numBricksInMovableStack );
            }
        }

        // Randomly choose a distance to use from the identified set.
        double fixedStackDistanceFromCenter = -validFixedStackDistances.get( RAND.nextInt( validFixedStackDistances.size() ) );

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricksInFixedStack, fixedStackDistanceFromCenter, numBricksInMovableStack );
    }

    /**
     * Create a challenge where random masses are chosen, one for the fixed
     * mass and one for the movable mass.
     */
    private static BalanceMassesChallenge generateChallengeRandomMasses() {

        Mass fixedMass;
        Mass movableMass;
        // Iterate through randomly chosen pairs of masses until a combination
        // is found that is solvable.
        do {
            fixedMass = getRandomMass( 0, Double.POSITIVE_INFINITY );
            do {
                movableMass = getRandomMass( fixedMass.getMass() * MIN_DISTANCE_FROM_BALANCE_CENTER_TO_MASS / MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS,
                                             fixedMass.getMass() * MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS / MIN_DISTANCE_FROM_BALANCE_CENTER_TO_MASS );
            }
            while ( movableMass.getMass() == fixedMass.getMass() ); // Make sure that movable mass is different from fixed mass.
        }
        while ( !isChallengeSolvable( fixedMass.getMass(), movableMass.getMass(), Plank.INTER_SNAP_TO_MARKER_DISTANCE, Plank.getLength() / 2 ) );

        return createTwoMassChallenge( fixedMass, chooseRandomValidFixedMassDistance( fixedMass.getMass(), movableMass.getMass() ), movableMass );
    }

    /**
     * Create a challenge in which one stack of bricks must be balanced by
     * another, and the distance ratios can be more complex than in the
     * simpler challenges, e.g. 3:2.
     *
     * @return
     */
    private static BalanceMassesChallenge generateModerateBalanceChallenge() {

        int numBricksInFixedStack = 1;
        int numBricksInMovableStack = 1;

        do {
            // Randomly choose the number of bricks in the fixed stack.
            numBricksInFixedStack = RAND.nextInt( 4 ) + 1;

            // Randomly choose the number of bricks in the movable stack, but
            // avoid the same number.
            numBricksInMovableStack = numBricksInFixedStack;
            while ( numBricksInMovableStack == numBricksInFixedStack ) {
                numBricksInMovableStack = RAND.nextInt( 4 ) + 1;
            }
        }
        while ( !isChallengeSolvable( numBricksInFixedStack * BrickStack.BRICK_MASS,
                                      numBricksInMovableStack * BrickStack.BRICK_MASS,
                                      Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                      MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS ) );

        // Randomly choose a distance to use for the fixed mass position.
        double fixedStackDistanceFromCenter = chooseRandomValidFixedMassDistance( numBricksInFixedStack * BrickStack.BRICK_MASS,
                                                                                  numBricksInMovableStack * BrickStack.BRICK_MASS );

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricksInFixedStack, fixedStackDistanceFromCenter, numBricksInMovableStack );
    }

    private static BalanceMassesChallenge createTwoBrickStackChallenge( int numBricksInFixedStack, double fixedStackDistanceFromCenter, int numBricksInMovableStack ) {
        return createTwoMassChallenge( new BrickStack( numBricksInFixedStack ), fixedStackDistanceFromCenter, new BrickStack( numBricksInMovableStack ) );
    }

    private static double chooseRandomValidFixedMassDistance( double fixedMassValue, double movableMassValue ) {
        List<Double> validFixedMassDistances = getPossibleDistanceList( fixedMassValue, movableMassValue );

        // Randomly choose a distance to use from the identified set.
        return -validFixedMassDistances.get( RAND.nextInt( validFixedMassDistances.size() ) );
    }

    /**
     * Convenience method for assembling two masses into a balance challenge.
     */
    private static BalanceMassesChallenge createTwoMassChallenge( Mass fixedMass, double fixedStackDistanceFromCenter, Mass movableMass ) {
        // Add the fixed mass and its distance from the center of the balance.
        List<MassDistancePair> fixedMassesList = new ArrayList<MassDistancePair>();
        MassDistancePair fixedMassDistancePair = new MassDistancePair( fixedMass, fixedStackDistanceFromCenter );
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

    private static Mass getRandomMass( double minMass, double maxMass ) {
        List<Mass> candidateMasses = new ArrayList<Mass>();
        for ( Mass mass : BALANCE_CHALLENGE_MASSES ) {
            if ( mass.getMass() >= minMass && mass.getMass() <= maxMass ) {
                // This mass meets the constraints, so add it to the list of candidates.
                candidateMasses.add( mass );
            }
        }

        if ( candidateMasses.size() > 0 ) {
            // Choose randomly from the list.
            return candidateMasses.get( RAND.nextInt( candidateMasses.size() ) ).clone();
        }

        // No matching masses.
        return null;
    }

    /**
     * Generate a deduce-the-mass style challenge where the fixed mystery mass
     * is the same value as the known mass.
     *
     * @return
     */
    private static DeduceTheMassChallenge generateSimpleDeduceTheMassChallenge() {
        int indexOffset = RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() );
        Mass counterBalanceMass = null;
        Mass mysteryMassPrototype = null;

        for ( int i = 0; i < MYSTERY_MASSES.size() && counterBalanceMass == null; i++ ) {
            mysteryMassPrototype = MYSTERY_MASSES.get( ( i + indexOffset ) % MYSTERY_MASSES.size() );
            counterBalanceMass = createMassByRatio( mysteryMassPrototype.getMass(), 1 );
        }

        // There must be at least one combination that works.  If not, it's a
        // major problem in the code that must be fixed.
        assert counterBalanceMass != null;

        // Since the masses are equal, any position for the mystery mass should
        // create a solvable challenge.
        double mysteryMassDistanceFromCenter = -generateRandomValidPlankDistance();

        // Create the challenge.
        return createDeduceTheMassChallengeFromParts( mysteryMassPrototype.clone(), mysteryMassDistanceFromCenter, counterBalanceMass );
    }

    /**
     * Create a mass from the list of available given an original mass value
     * and a list of ratios.  The created mass will have a mass value that
     * equals the original value multiplied by one of the given ratios.
     *
     * @param massValue
     * @param ratio     (massValue / createdMassValue)
     * @return
     */
    private static Mass createMassByRatio( double massValue, double ratio ) { // TODO: Not yet a list.
        int indexOffset = RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() );
        for ( int i = 0; i < BALANCE_CHALLENGE_MASSES.size(); i++ ) {
            Mass candidateMassPrototype = BALANCE_CHALLENGE_MASSES.get( ( i + indexOffset ) % BALANCE_CHALLENGE_MASSES.size() );
            if ( candidateMassPrototype.getMass() * ratio == massValue ) {
                // We have found a matching mass.  Clone it and return it.
                return candidateMassPrototype.clone();
            }
        }
        // If we made it to here, that means that there is no mass that
        // matches the specified criterion.
        return null;
    }

    private static DeduceTheMassChallenge generateChallengeSimpleDeduceMass( int index ) {
        // TODO: static generation for now, need to make random once approved.

        // Add the fixed mass and its distance from the center of the balance.
        MassDistancePair mysteryMassDistancePair = null;
        Mass movableMass = null;

        switch( index ) {
            case 0:
                mysteryMassDistancePair = new MassDistancePair( MysteryObjectFactory.createUnlabeledMysteryObject( 3 ), -1.5 );
                movableMass = new BrickStack( 2 );
                break;
            case 1:
                mysteryMassDistancePair = new MassDistancePair( MysteryObjectFactory.createUnlabeledMysteryObject( 4 ), -2 );
                movableMass = new BrickStack( 2 );
                break;
            case 2:
                mysteryMassDistancePair = new MassDistancePair( MysteryObjectFactory.createUnlabeledMysteryObject( 2 ), -0.5 );
                movableMass = new BrickStack( 1 );
                break;
            case 3:
                mysteryMassDistancePair = new MassDistancePair( MysteryObjectFactory.createUnlabeledMysteryObject( 0 ), -1 );
                movableMass = new BrickStack( 2 );
                break;
            case 4:
                mysteryMassDistancePair = new MassDistancePair( MysteryObjectFactory.createUnlabeledMysteryObject( 6 ), -.75 );
                movableMass = new BrickStack( 3 );
                break;
            default:
                assert false;
        }

        // Add the movable mass.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<MassDistancePair> solution = new ArrayList<MassDistancePair>();
        solution.add( new MassDistancePair( movableMass, -mysteryMassDistancePair.mass.getMass() * mysteryMassDistancePair.distance / movableMass.getMass() ) );

        // Combine into challenge.
        return new DeduceTheMassChallenge( mysteryMassDistancePair, movableMassesList, solution );
    }

    // TODO: If this is kept, move it into a constructor for the challenge.  Do for all similar convenience functions.
    private static DeduceTheMassChallenge createDeduceTheMassChallengeFromParts( Mass mysteryMass, double mysteryMassDistanceFromCenter, Mass knownMass ) {
        // Create the mass-distance pair for the mystery object.
        MassDistancePair mysteryMassDistancePair = new MassDistancePair( mysteryMass, mysteryMassDistanceFromCenter );

        // Put the known mass on to a list.
        List<Mass> knownMassesList = new ArrayList<Mass>();
        knownMassesList.add( knownMass );

        // Create a valid solution for the challenge.
        List<MassDistancePair> solution = new ArrayList<MassDistancePair>();
        solution.add( new MassDistancePair( knownMass, -mysteryMass.getMass() * mysteryMassDistanceFromCenter / knownMass.getMass() ) );

        // Combine into challenge.
        return new DeduceTheMassChallenge( mysteryMassDistancePair, knownMassesList, solution );
    }

    /**
     * Test the given mass values and balance information and determine whether
     * a balance challenge with these constraints can be solved.
     */
    private static boolean isChallengeSolvable( double massValue1, double massValue2, double distanceIncrement, double maxDistance ) {
        double minDistance = distanceIncrement;
        if ( massValue1 * minDistance > massValue2 * maxDistance || massValue1 * maxDistance < massValue2 * minDistance ) {
            // The balance is not long enough to allow these masses to be balanced.
            return false;
        }

        if ( ( massValue1 / massValue2 ) % distanceIncrement > COMPARISON_TOLERANCE ) {
            return false;
        }

        return true;
    }

    public static void main( String[] args ) {
        System.out.println( BalanceGameChallengeFactory.isChallengeSolvable( 10, 5, 0.25, 2 ) );
        System.out.println( BalanceGameChallengeFactory.isChallengeSolvable( 5, 10, 0.25, 2 ) );
        System.out.println( BalanceGameChallengeFactory.isChallengeSolvable( 10, 7, 0.25, 2 ) );
        System.out.println( BalanceGameChallengeFactory.isChallengeSolvable( 10, 100, 0.25, 2 ) );
    }
}
