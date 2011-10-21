// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorque.common.model.Plank;
import edu.colorado.phet.balanceandtorque.common.model.masses.BigRock;
import edu.colorado.phet.balanceandtorque.common.model.masses.Boy;
import edu.colorado.phet.balanceandtorque.common.model.masses.BrickStack;
import edu.colorado.phet.balanceandtorque.common.model.masses.CinderBlock;
import edu.colorado.phet.balanceandtorque.common.model.masses.FireHydrant;
import edu.colorado.phet.balanceandtorque.common.model.masses.FlowerPot;
import edu.colorado.phet.balanceandtorque.common.model.masses.Girl;
import edu.colorado.phet.balanceandtorque.common.model.masses.LargeBucket;
import edu.colorado.phet.balanceandtorque.common.model.masses.LargeTrashCan;
import edu.colorado.phet.balanceandtorque.common.model.masses.Man;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.common.model.masses.MediumBucket;
import edu.colorado.phet.balanceandtorque.common.model.masses.MediumRock;
import edu.colorado.phet.balanceandtorque.common.model.masses.MediumTrashCan;
import edu.colorado.phet.balanceandtorque.common.model.masses.PottedPlant;
import edu.colorado.phet.balanceandtorque.common.model.masses.SmallBucket;
import edu.colorado.phet.balanceandtorque.common.model.masses.SmallRock;
import edu.colorado.phet.balanceandtorque.common.model.masses.SodaBottle;
import edu.colorado.phet.balanceandtorque.common.model.masses.StatueOfOdysseus;
import edu.colorado.phet.balanceandtorque.common.model.masses.Television;
import edu.colorado.phet.balanceandtorque.common.model.masses.TinyRock;
import edu.colorado.phet.balanceandtorque.common.model.masses.Tire;
import edu.colorado.phet.balanceandtorque.common.model.masses.Woman;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

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
 * <p/>
 * TODO: This class requires a lot of cleaning up, making the terminology
 * consistent (e.g. create versus generate), and consolidating duplicated code.
 * It's not worth the time now because the game is in such a state of flux, but
 * don't forget about it.
 *
 * @author John Blanco
 */
public class BalanceGameChallengeFactory {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Random RAND = new Random();

    // Challenges per challenge set.
    public static final int CHALLENGES_PER_SET = 5;

    // Parameters that control how many attempts are made to generate a unique
    // balance challenge.
    private static final int MAX_GEN_ATTEMPTS = 50;
    private static final int MAX_HALVING_OF_PAST_LIST = 3;

    // Tolerance value used when comparing floating-point calculations.
    private static final double COMPARISON_TOLERANCE = 1E-6;

    // Determine the min and max distances from the center of the plank where
    // masses may be positioned.
    private static final double MIN_DISTANCE_FROM_BALANCE_CENTER_TO_MASS = Plank.INTER_SNAP_TO_MARKER_DISTANCE;
    private static final double MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS = ( Math.round( Plank.getLength() / Plank.INTER_SNAP_TO_MARKER_DISTANCE / 2 ) - 1 ) * Plank.INTER_SNAP_TO_MARKER_DISTANCE;

    // List of masses that can be used in balance challenges or as the fixed
    // masses in "deduce the mass" challenges.
    private static final List<Mass> BALANCE_CHALLENGE_MASSES = new ArrayList<Mass>() {{
        add( new BrickStack( 1 ) );
        add( new BrickStack( 2 ) );
        add( new BrickStack( 3 ) );
        add( new BrickStack( 4 ) );
        add( new TinyRock( false ) );
        add( new SmallRock( false ) );
        add( new MediumRock( false ) );
        add( new BigRock( false ) );
        add( new Boy() );
        add( new Girl() );
        add( new Woman() );
        add( new Man() );
        add( new StatueOfOdysseus( false ) );
        add( new CinderBlock( false ) );
    }};

    // List of mystery objects that the user has not seen on the other tab(s).
    private static final List<Mass> MYSTERY_MASSES = new ArrayList<Mass>() {{
        add( new FireHydrant( true ) );
        add( new Television( true ) );
        add( new LargeTrashCan( true ) );
        add( new MediumTrashCan( true ) );
        add( new FlowerPot( true ) );
        add( new SmallBucket( true ) );
        add( new MediumBucket( true ) );
        add( new LargeBucket( true ) );
        add( new PottedPlant( true ) );
        add( new SodaBottle( true ) );
        add( new Tire( true ) );
    }};

    // Structures used to keep track of the challenges generated so far so that
    // we can avoid creating the same challenges multiple times.
    private static final List<BalanceGameChallenge> usedBalanceChallenges = new ArrayList<BalanceGameChallenge>();
    private static final List<BalanceGameChallenge> usedMassDeductionChallenges = new ArrayList<BalanceGameChallenge>();

    // Wrap several of the methods into function objects so that they can be
    // used in the method that assures the uniqueness of challenges.
    private static final Function0<BalanceGameChallenge> simpleBalanceChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> simpleMassDeductionChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleMassDeductionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> easyBalanceChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateEasyBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> easyMassDeductionChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateEasyMassDeductionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> moderateBalanceChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateModerateBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> moderateMassDeductionChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateModerateMassDeductionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> advancedBalanceChallengeGenerator = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateMultiMassBalanceChallenge();
        }
    };

    private static final Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> uniqueMassesTest = new Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean>() {
        public Boolean apply( BalanceGameChallenge balanceGameChallenge, List<BalanceGameChallenge> balanceGameChallenges ) {
            return usesUniqueMasses( balanceGameChallenge, balanceGameChallenges );
        }
    };

    private static final Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> uniqueFixedMassesTest = new Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean>() {
        public Boolean apply( BalanceGameChallenge balanceGameChallenge, List<BalanceGameChallenge> balanceGameChallenges ) {
            return usesUniqueFixedMasses( balanceGameChallenge, balanceGameChallenges );
        }
    };

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    private BalanceGameChallengeFactory() {
        // Not meant to be instantiated.
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a set of challenges for the provided level.
     *
     * @param level
     * @return
     */
    public static List<BalanceGameChallenge> generateChallengeSet( int level ) {
        List<BalanceGameChallenge> balanceChallengeList = new ArrayList<BalanceGameChallenge>();
        if ( level == 1 ) {
            balanceChallengeList.add( generateUniqueChallenge( simpleBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( easyBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( simpleMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( easyBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( simpleMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
        }
        else if ( level == 2 ) {
            balanceChallengeList.add( generateUniqueChallenge( easyBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( easyMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( easyBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( moderateBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( easyMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
        }
        else if ( level == 3 ) {
            balanceChallengeList.add( generateUniqueChallenge( moderateBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( easyBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( moderateBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( moderateBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( moderateMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
        }
        else if ( level == 4 ) {
            balanceChallengeList.add( generateUniqueChallenge( advancedBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( moderateMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( advancedBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( moderateMassDeductionChallengeGenerator, uniqueFixedMassesTest, usedMassDeductionChallenges ) );
            balanceChallengeList.add( generateUniqueChallenge( advancedBalanceChallengeGenerator, uniqueMassesTest, usedBalanceChallenges ) );
        }
        else {
            // This level is either out of range or not implemented yet.
            throw new IllegalArgumentException( "Challenge level invalid, value = " + level );
        }

        // Check that the appropriate number of challenges are in the set.
        assert balanceChallengeList.size() == CHALLENGES_PER_SET;

        return balanceChallengeList;
    }

    /**
     * Create a simple challenge where brick stacks of equal mass appear on
     * each side.
     */
    private static BalanceMassesChallenge generateSimpleBalanceChallenge() {
        int numBricks = 1 + RAND.nextInt( 4 );
        double distance = -generateRandomValidPlankDistance();
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
            // Choose the number of bricks in the fixed stack.  Must be 1, 2,
            // or 4 in order to support the ratios used.
            numBricksInFixedStack = (int) Math.pow( 2, RAND.nextInt( 3 ) );

            // Choose the number of bricks in movable stack.
            if ( numBricksInFixedStack == 1 || RAND.nextBoolean() ) {
                numBricksInMovableStack = 2 * numBricksInFixedStack;
            }
            else {
                numBricksInMovableStack = numBricksInFixedStack / 2;
            }

            // Create a list of the distances at which the fixed stack may be
            // positioned to balance the movable stack.
            validFixedStackDistances.addAll( getPossibleDistanceList( numBricksInFixedStack * BrickStack.BRICK_MASS,
                                                                      numBricksInMovableStack * BrickStack.BRICK_MASS ) );

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

        return createBalanceChallengeFromParts( fixedMass, chooseRandomValidFixedMassDistance( fixedMass.getMass(), movableMass.getMass() ), movableMass );
    }

    /**
     * Create a challenge in which one stack of bricks must be balanced by
     * another, and the distance ratios can be more complex than in the
     * simpler challenges, e.g. 3:2.
     *
     * @return
     */
    private static BalanceMassesChallenge generateModerateBalanceChallengeOld() {

        int numBricksInFixedStack;
        int numBricksInMovableStack;

        // Create random challenges until a solvable one is created.
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

    /**
     * Create a challenge in which one fixed mass must be balanced by another,
     * and the distance ratios can be more complex than in the simpler
     * challenges, e.g. 3:2.
     *
     * @return
     */
    private static BalanceMassesChallenge generateModerateBalanceChallenge() {

        Mass fixedMassPrototype;
        Mass movableMass;

        // Create random challenges until a solvable one is created.
        do {
            // Randomly choose a fixed mass.
            fixedMassPrototype = BALANCE_CHALLENGE_MASSES.get( RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() ) );

            // Choose a mass at one of the desired ratios.
            movableMass = createMassByRatio( fixedMassPrototype.getMass(), 3.0 / 1.0, 1.0 / 3.0, 3.0 / 2.0, 2.0 / 3.0, 4.0 / 1.0, 1.0 / 4.0 );
            if ( movableMass == null ) {
                // If this happens, it means that one of the fixed masses has
                // no masses that match the provided ratios.  This should be
                // fixed.
                assert false;
                continue;
            }
        }
        while ( !isChallengeSolvable( fixedMassPrototype.getMass(),
                                      movableMass.getMass(),
                                      Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                      MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS ) );

        // Randomly choose a distance to use for the fixed mass position.
        double fixedStackDistanceFromCenter = chooseRandomValidFixedMassDistance( fixedMassPrototype.getMass(), movableMass.getMass() );

        // Create the challenge.
        return createBalanceChallengeFromParts( fixedMassPrototype.clone(), fixedStackDistanceFromCenter, movableMass );
    }

    /**
     * Generate a challenge where there are multiple fixed masses that must be
     * balanced.
     *
     * @return
     */
    private static BalanceGameChallenge generateMultiMassBalanceChallenge() {

        List<BalanceGameChallenge> solvableChallenges;

        do {
            Mass fixedMass1Prototype = BALANCE_CHALLENGE_MASSES.get( RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() ) );
            Mass fixedMass2Prototype = BALANCE_CHALLENGE_MASSES.get( RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() ) );
            Mass movableMassPrototype = BALANCE_CHALLENGE_MASSES.get( RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() ) );
            solvableChallenges = generateSolvableChallenges( fixedMass1Prototype, fixedMass2Prototype, movableMassPrototype,
                                                             Plank.INTER_SNAP_TO_MARKER_DISTANCE, Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );

        } while ( solvableChallenges.size() == 0 );

        // Choose one of the solvable configurations at random.
        return solvableChallenges.get( RAND.nextInt( solvableChallenges.size() ) );
    }


    /**
     * Generate a challenge where there are multiple fixed masses that must be
     * balanced.
     *
     * @return
     */
    private static BalanceMassesChallenge generateMultiMassBalanceChallengeOld() {

        // TODO: Stubbed for now, always produces the same challenge.
        // Add the first fixed mass and its distance from the center of the balance.
        List<MassDistancePair> fixedMassesList = new ArrayList<MassDistancePair>();
        MassDistancePair fixedMassDistancePair1 = new MassDistancePair( new BrickStack( 1 ), -1 );
        MassDistancePair fixedMassDistancePair2 = new MassDistancePair( new BrickStack( 2 ), -0.5 );
        fixedMassesList.add( fixedMassDistancePair1 );
        fixedMassesList.add( fixedMassDistancePair2 );

        // Add the movable mass.
        List<Mass> movableMassesList = new ArrayList<Mass>();
        Mass movableMass = new Boy();
        movableMassesList.add( movableMass );

        // Create a valid solution for the challenge.
        List<MassDistancePair> solution = new ArrayList<MassDistancePair>();
        solution.add( new MassDistancePair( movableMass, 0.5 ) );

        // And we're done.
        return new BalanceMassesChallenge( fixedMassesList, movableMassesList, solution );
    }

    private static BalanceMassesChallenge createTwoBrickStackChallenge( int numBricksInFixedStack, double fixedStackDistanceFromCenter, int numBricksInMovableStack ) {
        return createBalanceChallengeFromParts( new BrickStack( numBricksInFixedStack ), fixedStackDistanceFromCenter, new BrickStack( numBricksInMovableStack ) );
    }

    private static double chooseRandomValidFixedMassDistance( double fixedMassValue, double movableMassValue ) {
        List<Double> validFixedMassDistances = getPossibleDistanceList( fixedMassValue, movableMassValue );

        // Randomly choose a distance to use from the identified set.
        return -validFixedMassDistances.get( RAND.nextInt( validFixedMassDistances.size() ) );
    }

    /**
     * Convenience method for assembling one fixed mass and one movable mass
     * into a balance challenge.
     */
    private static BalanceMassesChallenge createBalanceChallengeFromParts( Mass fixedMass, double fixedMassDistanceFromCenter, Mass movableMass ) {
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

    // TODO: Created for duplicating a bug with the positioning of 5kg bricks, remove before final publication.
    private static BalanceMassesChallenge generateProblematicChallenge() {
        return createBalanceChallengeFromParts( new BrickStack( 1 ), 2, new BrickStack( 1 ), 1.75, new SmallRock( true ) );
    }

    /**
     * Convenience method for assembling two fixed masses and one movable mass
     * into a balance challenge.  All distances should be positive values, and
     * the sign conversion happens when the challenge is created.
     */
    private static BalanceMassesChallenge createBalanceChallengeFromParts( Mass fixedMass1, double fixedMass1DistanceFromCenter,
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
    private static MassDeductionChallenge generateSimpleMassDeductionChallenge() {
        int indexOffset = RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() );
        Mass knownMass = null;
        Mass mysteryMassPrototype = null;

        // Select a mystery mass and a known mass with the same mass value.
        for ( int i = 0; i < MYSTERY_MASSES.size() && knownMass == null; i++ ) {
            mysteryMassPrototype = MYSTERY_MASSES.get( ( i + indexOffset ) % MYSTERY_MASSES.size() );
            knownMass = createMassByRatio( mysteryMassPrototype.getMass(), 1 );
        }

        // There must be at least one combination that works.  If not, it's a
        // major problem in the code that must be fixed.
        assert knownMass != null;

        // Since the masses are equal, any position for the mystery mass should
        // create a solvable challenge.
        double mysteryMassDistanceFromCenter = -generateRandomValidPlankDistance();

        // Create the challenge.
        return createDeduceTheMassChallengeFromParts( mysteryMassPrototype.clone(), mysteryMassDistanceFromCenter, knownMass );
    }

    /**
     * Generate a deduce-the-mass style challenge where the fixed mystery mass
     * is either twice as heavy or half as heavy as the known mass.
     *
     * @return
     */
    private static MassDeductionChallenge generateEasyMassDeductionChallenge() {
        int indexOffset = RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() );
        Mass knownMass = null;
        Mass mysteryMassPrototype = null;

        for ( int i = 0; i < MYSTERY_MASSES.size() && knownMass == null; i++ ) {
            mysteryMassPrototype = MYSTERY_MASSES.get( ( i + indexOffset ) % MYSTERY_MASSES.size() );
            knownMass = createMassByRatio( mysteryMassPrototype.getMass(), 2, 0.5 );
        }

        // There must be at least one combination that works.  If not, it's a
        // major problem in the code that must be fixed.
        assert knownMass != null;

        // Choose a distance for the mystery mass.
        List<Double> possibleDistances = getPossibleDistanceList( mysteryMassPrototype.getMass(), knownMass.getMass() );
        double mysteryMassDistanceFromCenter = -possibleDistances.get( RAND.nextInt( possibleDistances.size() ) );

        // Create the challenge.
        return createDeduceTheMassChallengeFromParts( mysteryMassPrototype.clone(), mysteryMassDistanceFromCenter, knownMass );
    }

    /**
     * Generate a deduce-the-mass style challenge where the fixed mystery mass
     * is related to the movable mass by a ratio that is more complicate than
     * 2:1 or 1:2, e.g. 1:3.
     *
     * @return
     */
    private static MassDeductionChallenge generateModerateMassDeductionChallenge() {
        int indexOffset = RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() );
        Mass knownMass = null;
        Mass mysteryMassPrototype = null;

        for ( int i = 0; i < MYSTERY_MASSES.size() && knownMass == null; i++ ) {
            mysteryMassPrototype = MYSTERY_MASSES.get( ( i + indexOffset ) % MYSTERY_MASSES.size() );
            knownMass = createMassByRatio( mysteryMassPrototype.getMass(), 1.5, 3, ( 1.0 / 3.0 ), ( 2.0 / 3.0 ), 4, ( 1.0 / 4.0 ) );
        }

        // There must be at least one combination that works.  If not, it's a
        // major problem in the code that must be fixed.
        assert knownMass != null;

        // Choose a distance for the mystery mass.
        List<Double> possibleDistances = getPossibleDistanceList( mysteryMassPrototype.getMass(), knownMass.getMass() );
        double mysteryMassDistanceFromCenter = -possibleDistances.get( RAND.nextInt( possibleDistances.size() ) );

        // Create the challenge.
        return createDeduceTheMassChallengeFromParts( mysteryMassPrototype.clone(), mysteryMassDistanceFromCenter, knownMass );
    }

    /**
     * Create a mass from the list of available given an original mass value
     * and a list of ratios.  The created mass will have a mass value that
     * equals the original value multiplied by one of the given ratios.
     *
     * @param massValue
     * @param ratios    - List of ratios (massValue / createdMassValue) which are
     *                  acceptable.
     * @return
     */
    private static Mass createMassByRatio( double massValue, double... ratios ) {
        int indexOffset = RAND.nextInt( BALANCE_CHALLENGE_MASSES.size() );
        for ( int i = 0; i < BALANCE_CHALLENGE_MASSES.size(); i++ ) {
            Mass candidateMassPrototype = BALANCE_CHALLENGE_MASSES.get( ( i + indexOffset ) % BALANCE_CHALLENGE_MASSES.size() );
            for ( Double ratio : ratios ) {
                if ( candidateMassPrototype.getMass() * ratio == massValue ) {
                    // We have found a matching mass.  Clone it and return it.
                    return candidateMassPrototype.clone();
                }
            }
        }
        // If we made it to here, that means that there is no mass that
        // matches the specified criterion.
        return null;
    }

    // TODO: If this is kept, move it into a constructor for the challenge.  Do for all similar convenience functions.
    private static MassDeductionChallenge createDeduceTheMassChallengeFromParts( Mass mysteryMass, double mysteryMassDistanceFromCenter, Mass knownMass ) {
        // Create the mass-distance pair for the mystery object.
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

    /**
     * Method to generate a "unique" challenge, meaning one that the user
     * either hasn't seen before or at least hasn't seen recently.
     *
     * @param challengeGenerator
     * @return
     */
    private static BalanceGameChallenge generateUniqueChallenge( Function0<BalanceGameChallenge> challengeGenerator,
                                                                 Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> uniquenessTest,
                                                                 List<BalanceGameChallenge> previousChallenges ) {
        BalanceGameChallenge challenge = null;
        boolean uniqueChallengeGenerated = false;

        for ( int i = 0; i < MAX_HALVING_OF_PAST_LIST && uniqueChallengeGenerated == false; i++ ) {
            for ( int j = 0; j < MAX_GEN_ATTEMPTS; j++ ) {

                // Create a challenge.
                challenge = challengeGenerator.apply();

                // Check whether the challenge is unique.
                if ( uniquenessTest.apply( challenge, previousChallenges ) ) {
                    // If so, we're done.
                    uniqueChallengeGenerated = true;
                    break;
                }
            }
            if ( !uniqueChallengeGenerated ) {
                // Several attempts did not yield a unique challenge, so
                // reduce the number of past challenges on the list in order
                // to make it easier, and then try again.
                removeOldestHalfOfList( previousChallenges );
            }
        }
        previousChallenges.add( challenge );
        return challenge;
    }

    /**
     * Determine whether a challenge with the given values for the fixed and
     * movable masses and the given constraints on the plank can be solved.
     */
    private static boolean isChallengeSolvable( double fixedMassValue, double movableMassValue, double distanceIncrement, double maxDistance ) {
        double minDistance = distanceIncrement;
        if ( fixedMassValue * minDistance > movableMassValue * maxDistance || fixedMassValue * maxDistance < movableMassValue * minDistance ) {
            // The balance is not long enough to allow these masses to be balanced.
            return false;
        }

        if ( ( fixedMassValue / movableMassValue ) % distanceIncrement > COMPARISON_TOLERANCE ) {
            return false;
        }

        return true;
    }

    /**
     * Get the list of solvable balance game challenges that can be created
     * from the given parameters.
     */
    private static List<BalanceGameChallenge> generateSolvableChallenges( Mass fixedMass1Prototype, Mass fixedMass2Prototype, Mass movableMassPrototype,
                                                                          double distanceIncrement, double maxDistance ) {
        List<BalanceGameChallenge> solvableChallenges = new ArrayList<BalanceGameChallenge>();
        for ( double fixedMass1Distance = distanceIncrement; fixedMass1Distance <= maxDistance; fixedMass1Distance += distanceIncrement ) {
            for ( double fixedMass2Distance = distanceIncrement; fixedMass2Distance <= maxDistance; fixedMass2Distance += distanceIncrement ) {
                if ( fixedMass1Distance == fixedMass2Distance ) {
                    // Skip these cases, since the masses can't be in the same place.
                    continue;
                }
                double fixedMassTorque = fixedMass1Prototype.getMass() * fixedMass1Distance + fixedMass2Prototype.getMass() * fixedMass2Distance;
                double movableMassDistance = fixedMassTorque / movableMassPrototype.getMass();
                if ( movableMassDistance >= distanceIncrement &&
                     movableMassDistance <= maxDistance &&
                     movableMassDistance % distanceIncrement == 0 ) {
                    // This is a solvable configuration.  Add it to the list.
                    solvableChallenges.add( createBalanceChallengeFromParts( fixedMass1Prototype.clone(), fixedMass1Distance,
                                                                             fixedMass2Prototype.clone(), fixedMass2Distance,
                                                                             movableMassPrototype.clone() ) );
                }
            }
        }

        return solvableChallenges;
    }

    /**
     * Test a challenge against a list of challenges to see if the text
     * challenge uses unique mass values for the movable and fixed masses.
     * Distances are ignored, so if a challenge is tested against a set that
     * contains one with the same masses but different distances, this will
     * return false, indicating that the challenge is non-unique.
     *
     * @param testChallenge
     * @param usedChallengeList
     * @return
     */
    private static boolean usesUniqueMasses( BalanceGameChallenge testChallenge, List<BalanceGameChallenge> usedChallengeList ) {
        for ( BalanceGameChallenge usedChallenge : usedChallengeList ) {
            if ( usedChallenge.usesSameMasses( testChallenge ) ) {
                return false;
            }
        }
        return true;
    }

    private static boolean usesUniqueFixedMasses( BalanceGameChallenge testChallenge, List<BalanceGameChallenge> usedChallengeList ) {
        for ( BalanceGameChallenge usedChallenge : usedChallengeList ) {
            if ( usedChallenge.usesSameFixedMasses( testChallenge ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * This class takes a list of items at construction and then, when asked
     * through the 'get' method will randomly pick an item from the original
     * list.  It will go through all items in the list before returning the
     * same one again, at which point it will "reset" back to the original
     * list.
     *
     * @param <T>
     */
    private static class RandomItemBag<T> {
        private static Random RAND = new Random();

        List<T> copyOfOriginalItems;
        List<T> unusedItems;

        private RandomItemBag( List<T> originalItemList ) {
            copyOfOriginalItems = new ArrayList<T>( originalItemList );
        }

        public T getRandomItem() {
            assert unusedItems.size() > 0; // There is a bug in the code if this happens.
            T item = unusedItems.get( RAND.nextInt( unusedItems.size() ) );
            unusedItems.remove( item );
            if ( unusedItems.size() == 0 ) {
                // All items have been used, time to reset the list.
                unusedItems.addAll( copyOfOriginalItems );
            }
            return item;
        }

        public void returnItem( T item ) {
            if ( unusedItems.contains( item ) ) {
                assert false; // This is to catch misuse of this class.
                System.out.println( getClass().getName() + " - Warning: Attempt to return an item that is already in the bag." );
                return;
            }
            unusedItems.add( item );
        }
    }

    /**
     * Convenience function for removing the oldest half of a list.
     */
    private static void removeOldestHalfOfList( List list ) {
        int halfLength = list.size() / 2;
        for ( int i = 0; i < halfLength; i++ ) {
            list.remove( 0 );
        }
    }

    // Test harness, changes as code evolves, not meant to test all aspects of
    // this class.
    public static void main( String[] args ) {
//        for ( int i = 0; i < 100; i++ ) {
//            BalanceGameChallenge challenge = generateSimpleBalanceChallenge();
//            System.out.println( "------" );
//            System.out.println( "challenge.fixedMasses.get( 0 ) = " + challenge.fixedMassDistancePairs.get( 0 ).mass.getMass() );
//            System.out.println( "challenge.movableMasses.get( 0 ) = " + challenge.movableMasses.get( 0 ).getMass() );
//        }
//        for ( int i = 0; i < 100; i++ ) {
//            BalanceGameChallenge challenge = generateEasyBalanceChallenge();
//            System.out.println( "------" );
//            System.out.println( "challenge.fixedMasses.get( 0 ) = " + challenge.fixedMassDistancePairs.get( 0 ).mass.getMass() );
//            System.out.println( "challenge.movableMasses.get( 0 ) = " + challenge.movableMasses.get( 0 ).getMass() );
//        }
//        for ( int i = 0; i < 100; i++ ) {
//            BalanceGameChallenge challenge = generateUniqueChallenge( simpleMassDeductionChallengeGenerator, uniqueMassesTest, usedMassDeductionChallenges );
//            System.out.println( "------" );
//            System.out.println( "Fixed mass = " + challenge.fixedMassDistancePairs.get( 0 ).mass.getClass().getName() );
//            System.out.println( "challenge.fixedMasses.get( 0 ) = " + challenge.fixedMassDistancePairs.get( 0 ).mass.getMass() );
//            System.out.println( "Movable mass = " + challenge.movableMasses.get( 0 ).getClass().getName() );
//            System.out.println( "challenge.movableMasses.get( 0 ) = " + challenge.movableMasses.get( 0 ).getMass() );
//        }
        return;
    }

}
