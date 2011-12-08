// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorque.common.model.Plank;
import edu.colorado.phet.balanceandtorque.common.model.masses.Barrel;
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
import edu.colorado.phet.balanceandtorque.common.model.masses.Television;
import edu.colorado.phet.balanceandtorque.common.model.masses.TinyRock;
import edu.colorado.phet.balanceandtorque.common.model.masses.Tire;
import edu.colorado.phet.balanceandtorque.common.model.masses.Woman;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * This class is a factory pattern class that generates sets of challenges for
 * use in the balance game.  In this class, the terminology used to distinguish
 * between the various levels of difficulty for the challenges are (in order of
 * increasing difficulty):
 * - Simple
 * - Easy
 * - Moderate
 * - Advanced
 * This is not to be confused with the numerical game levels, since there is
 * not necessarily a 1 to 1 correspondence between the numerical levels and
 * the difficulty of a given challenge.
 *
 * @author John Blanco
 */
public class BalanceGameChallengeFactory {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Used for randomization of challenges.
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
    private static final double MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS = ( Math.round( Plank.getLength() / Plank.INTER_SNAP_TO_MARKER_DISTANCE / 2 ) - 1 ) * Plank.INTER_SNAP_TO_MARKER_DISTANCE;

    // List of masses that can be used on either side of the balance challenges
    // or as the fixed masses in mass deduction challenges.
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
        add( new Barrel( false ) );
        add( new CinderBlock( false ) );
    }};

    // List of masses that can be used as "mystery objects" in the mass
    // deduction challenges.  These should not appear in other tabs, lest the
    // user could already know their mass.
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

    // Lists used to keep track of the challenges generated so far so that we
    // can avoid creating the same challenges multiple times.
    private static final List<BalanceGameChallenge> USED_BALANCE_CHALLENGES = new ArrayList<BalanceGameChallenge>();
    private static final List<BalanceGameChallenge> USED_MASS_DEDUCTION_CHALLENGES = new ArrayList<BalanceGameChallenge>();
    private static final List<BalanceGameChallenge> USED_TIP_PREDICTION_CHALLENGES = new ArrayList<BalanceGameChallenge>();

    // Wrap several of the methods into function objects so that they can be
    // used in the method that assures the uniqueness of challenges.
    private static final Function0<BalanceGameChallenge> SIMPLE_BALANCE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> SIMPLE_TIP_PREDICTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleTipPredictionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> SIMPLE_MASS_DEDUCTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleMassDeductionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> EASY_BALANCE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateEasyBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> EASY_MASS_DEDUCTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateEasyMassDeductionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> MODERATE_BALANCE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateModerateBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> MODERATE_MASS_DEDUCTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateModerateMassDeductionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> ADVANCED_BALANCE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateMultiMassBalanceChallenge();
        }
    };

    private static final Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> UNIQUE_MASSES_TEST = new Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean>() {
        public Boolean apply( BalanceGameChallenge balanceGameChallenge, List<BalanceGameChallenge> balanceGameChallenges ) {
            return usesUniqueMasses( balanceGameChallenge, balanceGameChallenges );
        }
    };

    private static final Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> UNIQUE_FIXED_MASSES_TEST = new Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean>() {
        public Boolean apply( BalanceGameChallenge balanceGameChallenge, List<BalanceGameChallenge> balanceGameChallenges ) {
            return usesUniqueFixedMasses( balanceGameChallenge, balanceGameChallenges );
        }
    };

    private static final Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST = new Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean>() {
        public Boolean apply( BalanceGameChallenge balanceGameChallenge, List<BalanceGameChallenge> balanceGameChallenges ) {
            return usesUniqueFixedMassesAndDistances( balanceGameChallenge, balanceGameChallenges );
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
     * Get a set of challenges for the specified level.
     *
     * @param level
     * @return
     */
    public static List<BalanceGameChallenge> generateChallengeSet( int level ) {
        List<BalanceGameChallenge> balanceChallengeList = new ArrayList<BalanceGameChallenge>();
        if ( level == 1 ) {
            balanceChallengeList.add( generateUniqueChallenge( SIMPLE_TIP_PREDICTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TIP_PREDICTION_CHALLENGES ) );
//            balanceChallengeList.add( generateUniqueChallenge( SIMPLE_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( SIMPLE_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
        }
        else if ( level == 2 ) {
            balanceChallengeList.add( generateUniqueChallenge( EASY_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
        }
        else if ( level == 3 ) {
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( EASY_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
        }
        else if ( level == 4 ) {
            balanceChallengeList.add( generateUniqueChallenge( ADVANCED_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( ADVANCED_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( MODERATE_MASS_DEDUCTION_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_TEST, USED_MASS_DEDUCTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( ADVANCED_BALANCE_CHALLENGE_GENERATOR, UNIQUE_MASSES_TEST, USED_BALANCE_CHALLENGES ) );
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
     * Generate a simple challenge where brick stacks of equal mass appear on
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
        }

        // Randomly choose a distance to use from the identified set.
        double fixedStackDistanceFromCenter = -validFixedStackDistances.get( RAND.nextInt( validFixedStackDistances.size() ) );

        // Create the challenge.
        return createTwoBrickStackChallenge( numBricksInFixedStack, fixedStackDistanceFromCenter, numBricksInMovableStack );
    }

    /**
     * Create a challenge in which one fixed mass must be balanced by another,
     * and the distance ratios can be more complex than in the simpler
     * challenges, e.g. 3:2.
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
            }
        }
        while ( !isChallengeSolvable( fixedMassPrototype.getMass(),
                                      movableMass.getMass(),
                                      Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                      MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS ) );

        // Randomly choose a distance to use for the fixed mass position.
        double fixedStackDistanceFromCenter = chooseRandomValidFixedMassDistance( fixedMassPrototype.getMass(), movableMass.getMass() );

        // Create the challenge.
        return BalanceMassesChallenge.create( fixedMassPrototype.createCopy(), fixedStackDistanceFromCenter, movableMass );
    }

    /**
     * Generate a challenge where there are multiple fixed masses that must be
     * balanced by a single movable mass.
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

    private static BalanceMassesChallenge createTwoBrickStackChallenge( int numBricksInFixedStack, double fixedStackDistanceFromCenter, int numBricksInMovableStack ) {
        return BalanceMassesChallenge.create( new BrickStack( numBricksInFixedStack ), fixedStackDistanceFromCenter, new BrickStack( numBricksInMovableStack ) );
    }

    private static double chooseRandomValidFixedMassDistance( double fixedMassValue, double movableMassValue ) {
        List<Double> validFixedMassDistances = getPossibleDistanceList( fixedMassValue, movableMassValue );

        // Randomly choose a distance to use from the identified set.
        return -validFixedMassDistances.get( RAND.nextInt( validFixedMassDistances.size() ) );
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
            return candidateMasses.get( RAND.nextInt( candidateMasses.size() ) ).createCopy();
        }

        // No matching masses.
        return null;
    }

    /**
     * Generate a mass deduction style challenge where the fixed mystery mass
     * is the same value as the known mass.
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
        return MassDeductionChallenge.create( mysteryMassPrototype.createCopy(), mysteryMassDistanceFromCenter, knownMass );
    }

    /**
     * Generate a simple tip-prediction style of challenge.  This one only
     * uses bricks, and never produces perfectly balanced challenges.
     *
     * @return
     */
    private static TipPredictionChallenge generateSimpleTipPredictionChallenge() {

        // Choose two different numbers between 1 and 4 (inclusive) for the
        // number of bricks in the two stacks.
        int numBricksInLeftStack = RAND.nextInt( 4 ) + 1;
        int numBricksInRightState = numBricksInLeftStack;
        while ( numBricksInRightState == numBricksInLeftStack ) {
            numBricksInRightState = RAND.nextInt( 4 ) + 1;
        }

        // Choose a distance from the center, which will be used for
        // positioning both stacks.  The max and min increment values can be
        // tweaked if desired to limit the range of distances generated.
        int maxIncrements = (int) Math.round( Plank.LENGTH / 2 / Plank.INTER_SNAP_TO_MARKER_DISTANCE ) - 3;
        int minIncrements = 1;
        assert maxIncrements > minIncrements;
        double distanceFromPlankCenter = ( RAND.nextInt( maxIncrements - minIncrements ) + minIncrements ) * Plank.INTER_SNAP_TO_MARKER_DISTANCE;

        // TODO: Stubbed for now, always produces same challenge.
        return TipPredictionChallenge.create( new BrickStack( numBricksInLeftStack ),
                                              distanceFromPlankCenter,
                                              new BrickStack( numBricksInRightState ),
                                              -distanceFromPlankCenter );
    }

    /**
     * Generate a mass deduction style challenge where the fixed mystery mass
     * is either twice as heavy or half as heavy as the known mass.
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
        return MassDeductionChallenge.create( mysteryMassPrototype.createCopy(), mysteryMassDistanceFromCenter, knownMass );
    }

    /**
     * Generate a mass deduction style challenge where the fixed mystery mass
     * is related to the movable mass by a ratio that is more complicate than
     * 2:1 or 1:2, e.g. 1:3.
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
        return MassDeductionChallenge.create( mysteryMassPrototype.createCopy(), mysteryMassDistanceFromCenter, knownMass );
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
                    return candidateMassPrototype.createCopy();
                }
            }
        }
        // If we made it to here, that means that there is no mass that
        // matches the specified criterion.
        return null;
    }

    /**
     * Method to generate a "unique" challenge, meaning one that the user
     * either hasn't seen before or at least hasn't seen recently.  The caller
     * provides function objects for generating the challenges and testing its
     * uniqueness, as well as a list of previous challenges to test against.
     */
    private static BalanceGameChallenge generateUniqueChallenge( Function0<BalanceGameChallenge> challengeGenerator,
                                                                 Function2<BalanceGameChallenge, List<BalanceGameChallenge>, Boolean> uniquenessTest,
                                                                 List<BalanceGameChallenge> previousChallenges ) {
        BalanceGameChallenge challenge = null;
        boolean uniqueChallengeGenerated = false;

        for ( int i = 0; i < MAX_HALVING_OF_PAST_LIST && !uniqueChallengeGenerated; i++ ) {
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
        if ( fixedMassValue * distanceIncrement > movableMassValue * maxDistance || fixedMassValue * maxDistance < movableMassValue * distanceIncrement ) {
            // The balance is not long enough to allow these masses to be balanced.
            return false;
        }

        return ( fixedMassValue / movableMassValue ) % distanceIncrement <= COMPARISON_TOLERANCE;

    }

    /**
     * Get the list of solvable balance game challenges that can be created
     * from the given set of two fixed masses and one movable mass.
     */
    private static List<BalanceGameChallenge> generateSolvableChallenges( Mass fixedMass1Prototype, Mass fixedMass2Prototype, Mass movableMassPrototype,
                                                                          double distanceIncrement, double maxDistance ) {
        List<BalanceGameChallenge> solvableChallenges = new ArrayList<BalanceGameChallenge>();
        for ( double fixedMass1Distance = distanceIncrement; fixedMass1Distance <= maxDistance; fixedMass1Distance += distanceIncrement ) {
            for ( double fixedMass2Distance = distanceIncrement; fixedMass2Distance <= maxDistance; fixedMass2Distance += distanceIncrement ) {
                if ( fixedMass1Distance == fixedMass2Distance || Math.abs( fixedMass1Distance - fixedMass2Distance ) < 1.1 * distanceIncrement ) {
                    // Skip cases where the fixed masses are at the same
                    // location or just one increment apart.
                    continue;
                }
                double fixedMassTorque = fixedMass1Prototype.getMass() * fixedMass1Distance + fixedMass2Prototype.getMass() * fixedMass2Distance;
                double movableMassDistance = fixedMassTorque / movableMassPrototype.getMass();
                if ( movableMassDistance >= distanceIncrement &&
                     movableMassDistance <= maxDistance &&
                     movableMassDistance % distanceIncrement == 0 ) {
                    // This is a solvable configuration.  Add it to the list.
                    solvableChallenges.add( BalanceMassesChallenge.create( fixedMass1Prototype.createCopy(), fixedMass1Distance,
                                                                           fixedMass2Prototype.createCopy(), fixedMass2Distance,
                                                                           movableMassPrototype.createCopy() ) );
                }
            }
        }

        return solvableChallenges;
    }

    /**
     * Test a challenge against a list of challenges to see if the given
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

    /**
     * Tests a challenge against a set of challenges to see whether the test
     * challenge has unique fixed masses compared to all of the challenges on
     * the list.  If any of the challenge on the comparison list have the same
     * fixed masses, this will return false, indicating that the challenge is
     * not unique.
     *
     * @param testChallenge
     * @param usedChallengeList
     * @return
     */
    private static boolean usesUniqueFixedMasses( BalanceGameChallenge testChallenge, List<BalanceGameChallenge> usedChallengeList ) {
        for ( BalanceGameChallenge usedChallenge : usedChallengeList ) {
            if ( usedChallenge.usesSameFixedMasses( testChallenge ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests a challenge against a set of challenges to see whether the test
     * challenge has unique fixed masses and distances compared to all of the
     * challenges on the comparison list.  If any of the challenge on the
     * comparison list have the same fixed masses at the same distances from
     * the center, this will return false, indicating that the test challenge
     * is not unique.
     *
     * @param testChallenge
     * @param usedChallengeList
     * @return
     */
    private static boolean usesUniqueFixedMassesAndDistances( BalanceGameChallenge testChallenge, List<BalanceGameChallenge> usedChallengeList ) {
        for ( BalanceGameChallenge usedChallenge : usedChallengeList ) {
            if ( usedChallenge.usesSameFixedMassesAndDistances( testChallenge ) ) {
                return false;
            }
        }
        return true;
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
}