// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorquestudy.common.model.Plank;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Barrel;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.BigRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Boy;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.BrickStack;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.CinderBlock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.FireHydrant;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.FlowerPot;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Girl;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.LargeBucket;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.LargeTrashCan;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Man;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MediumBucket;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MediumRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MediumTrashCan;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.PottedPlant;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.SmallBucket;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.SmallRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.SodaBottle;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Television;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.TinyRock;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Tire;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Woman;
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
    public static final int CHALLENGES_PER_SET = 8;

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

    // List of masses that can be used as "mystery masses" in the mass
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

    // List of masses that are "low profile", meaning that they are short.
    // This is needed for the tilt-prediction style of problem, since taller
    // masses end up going behind the tilt prediction selector.
    private static final List<Mass> LOW_PROFILE_MASSES = new ArrayList<Mass>() {{
        add( new TinyRock( false ) );
        add( new SmallRock( false ) );
        add( new MediumRock( false ) );
        add( new CinderBlock( false ) );
    }};

    // Lists used to keep track of the challenges generated so far so that we
    // can avoid creating the same challenges multiple times.
    private static final List<BalanceGameChallenge> USED_BALANCE_CHALLENGES = new ArrayList<BalanceGameChallenge>();
    private static final List<BalanceGameChallenge> USED_MASS_DEDUCTION_CHALLENGES = new ArrayList<BalanceGameChallenge>();
    private static final List<BalanceGameChallenge> USED_TILT_PREDICTION_CHALLENGES = new ArrayList<BalanceGameChallenge>();

    // Wrap several of the methods into function objects so that they can be
    // used in the method that assures the uniqueness of challenges.
    private static final Function0<BalanceGameChallenge> SIMPLE_BALANCE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> SIMPLE_TILT_PREDICTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateSimpleTiltPredictionChallenge();
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

    private static final Function0<BalanceGameChallenge> EASY_TILT_PREDICTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateEasyTiltPredictionChallenge();
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

    private static final Function0<BalanceGameChallenge> MODERATE_TILT_PREDICTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateModerateTiltPredictionChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> ADVANCED_BALANCE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateMultiMassBalanceChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> ADVANCED_TILT_PREDICTION_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateAdvancedTiltPredictionChallenge();
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

    // Challenge generators created for Stanford study.  The terminology used
    // for the challenge types were taken from Min Chi's description of the
    // desired challenges.  See #3537.
    private static final Function0<BalanceGameChallenge> TILT_PREDICTION_EQUAL_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateTiltPredictionEqualChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> TILT_PREDICTION_DOMINATE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateTiltPredictionDominateChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> TILT_PREDICTION_SUBORDINATE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateTiltPredictionSubordinateChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> TILT_PREDICTION_CONFLICT_DOMINATE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateTiltPredictionConflictDominateChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> TILT_PREDICTION_CONFLICT_SUBORDINATE_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateTiltPredictionConflictSubordinateChallenge();
        }
    };

    private static final Function0<BalanceGameChallenge> TILT_PREDICTION_CONFLICT_EQUAL_CHALLENGE_GENERATOR = new Function0<BalanceGameChallenge>() {
        public BalanceGameChallenge apply() {
            return generateTiltPredictionConflictEqualChallenge();
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
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_DOMINATE_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_EQUAL_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_SUBORDINATE_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_CONFLICT_DOMINATE_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_CONFLICT_EQUAL_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_CONFLICT_SUBORDINATE_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_CONFLICT_EQUAL_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
            balanceChallengeList.add( generateUniqueChallenge( TILT_PREDICTION_CONFLICT_SUBORDINATE_CHALLENGE_GENERATOR, UNIQUE_FIXED_MASSES_AND_DISTANCES_TEST, USED_TILT_PREDICTION_CHALLENGES ) );
        }
        else {
            // This level is either out of range or not implemented yet.
            throw new IllegalArgumentException( "Challenge level invalid, value = " + level );
        }

        // Check that the appropriate number of challenges are in the set.
        assert balanceChallengeList.size() == CHALLENGES_PER_SET;

        return balanceChallengeList;
    }

    private static final int MAX_BRICKS_IN_TP_CHALLENGE_STACKS = 6;

    private static BalanceGameChallenge generateTiltPredictionEqualChallenge() {
        int numBricks = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;

        // Choose a distance from the center, which will be used for
        // positioning both stacks.  The max and min values can be tweaked if
        // desired to limit the range of distances generated.
        double distanceFromPlankCenter = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                           Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                               distanceFromPlankCenter,
                                               new BrickStack( numBricks ),
                                               -distanceFromPlankCenter );
    }

    private static BalanceGameChallenge generateTiltPredictionDominateChallenge() {
        int numBricksInLeftStack = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
        int numBricksInRightState = numBricksInLeftStack;
        while ( numBricksInRightState == numBricksInLeftStack ) {
            numBricksInRightState = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
        }

        // Choose a distance from the center, which will be used for
        // positioning both stacks.  The max and min values can be tweaked if
        // desired to limit the range of distances generated.
        double distanceFromPlankCenter = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                           Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE * 3 );

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricksInLeftStack ),
                                               distanceFromPlankCenter,
                                               new BrickStack( numBricksInRightState ),
                                               -distanceFromPlankCenter );
    }

    private static BalanceGameChallenge generateTiltPredictionSubordinateChallenge() {
        int numBricks = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;

        // Choose a distance from the center, which will be used for
        // positioning both stacks.  The max and min values can be tweaked if
        // desired to limit the range of distances generated.
        double distance1 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                             Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );

        double distance2 = distance1;
        while ( distance2 == distance1 ) {
            distance2 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                          Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
        }

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                               distance1,
                                               new BrickStack( numBricks ),
                                               -distance2 );
    }

    private static BalanceGameChallenge generateTiltPredictionConflictDominateChallenge() {
        int numBricks1, numBricks2;
        double distance1, distance2;
        do {
            do {
                numBricks1 = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
                numBricks2 = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
            } while ( numBricks1 <= numBricks2 );

            do {
                distance1 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                              Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
                distance2 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                              Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
            } while ( distance1 >= distance2 );
        } while ( numBricks1 * distance1 <= numBricks2 * distance2 );

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                               distance1,
                                               new BrickStack( numBricks2 ),
                                               -distance2 );
    }

    private static BalanceGameChallenge generateTiltPredictionConflictSubordinateChallenge() {
        int numBricks1, numBricks2;
        double distance1, distance2;
        do {
            do {
                numBricks1 = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
                numBricks2 = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
            } while ( numBricks1 <= numBricks2 );

            do {
                distance1 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                              Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
                distance2 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                              Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
            } while ( distance1 >= distance2 );
        } while ( numBricks1 * distance1 >= numBricks2 * distance2 );

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                               distance1,
                                               new BrickStack( numBricks2 ),
                                               -distance2 );
    }

    private static BalanceGameChallenge generateTiltPredictionConflictEqualChallenge() {
        int numBricks1, numBricks2;
        double distance1, distance2;
        do {
            do {
                numBricks1 = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
                numBricks2 = RAND.nextInt( MAX_BRICKS_IN_TP_CHALLENGE_STACKS ) + 1;
            } while ( numBricks1 <= numBricks2 );

            do {
                distance1 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                              Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
                distance2 = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                              Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE );
            } while ( distance1 >= distance2 );
        } while ( numBricks1 * distance1 != numBricks2 * distance2 );

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                               distance1,
                                               new BrickStack( numBricks2 ),
                                               -distance2 );
    }

    /**
     * Generate a simple challenge where brick stacks of equal mass appear on
     * each side.
     */
    private static BalanceMassesChallenge generateSimpleBalanceChallenge() {
        int numBricks = 1 + RAND.nextInt( 5 );
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
     * center of the plank.  The plank only allows discrete distances (i.e. it
     * is quantized), which is why this is needed.
     */
    private static double generateRandomValidPlankDistance() {
        double maxDistance = Plank.getLength() / 2;
        double increment = Plank.INTER_SNAP_TO_MARKER_DISTANCE;
        int maxIncrements = (int) Math.round( maxDistance / increment ) - 1;
        return ( RAND.nextInt( maxIncrements ) + 1 ) * increment;
    }

    /**
     * Convenience function that generates a valid random distance from the
     * center of the plank between a given min and max distance.  The set of
     * potential return values is inclusive of the min and max values.
     */
    private static double generateRandomValidPlankDistance( double minDistance, double maxDistance ) {
        int minIncrements = (int) Math.ceil( minDistance / Plank.INTER_SNAP_TO_MARKER_DISTANCE );
        int maxIncrements = (int) Math.floor( maxDistance / Plank.INTER_SNAP_TO_MARKER_DISTANCE );
        assert maxIncrements > minIncrements;

        return ( RAND.nextInt( maxIncrements - minIncrements + 1 ) + minIncrements ) * Plank.INTER_SNAP_TO_MARKER_DISTANCE;
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
     * Generate a simple tilt-prediction style of challenge.  This one only
     * uses bricks, and never produces perfectly balanced challenges.
     *
     * @return
     */
    private static TiltPredictionChallenge generateSimpleTiltPredictionChallenge() {

        // Choose two different numbers between 1 and 4 (inclusive) for the
        // number of bricks in the two stacks.
        int numBricksInLeftStack = RAND.nextInt( 4 ) + 1;
        int numBricksInRightState = numBricksInLeftStack;
        while ( numBricksInRightState == numBricksInLeftStack ) {
            numBricksInRightState = RAND.nextInt( 4 ) + 1;
        }

        // Choose a distance from the center, which will be used for
        // positioning both stacks.  The max and min values can be tweaked if
        // desired to limit the range of distances generated.
        double distanceFromPlankCenter = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                           Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE * 3 );

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricksInLeftStack ),
                                               distanceFromPlankCenter,
                                               new BrickStack( numBricksInRightState ),
                                               -distanceFromPlankCenter );
    }

    /**
     * Generate an easy tilt-prediction style of challenge.  This one only
     * uses bricks, and the distances and masses may be the same or different.
     *
     * @return
     */
    private static TiltPredictionChallenge generateEasyTiltPredictionChallenge() {

        // Choose two different numbers between 1 and 4 (inclusive) for the
        // number of bricks in the two stacks.
        int numBricksInLeftStack = RAND.nextInt( 4 ) + 1;
        int numBricksInRightState = numBricksInLeftStack;

        // Generate distance for the left mass.
        double leftMassDistance = generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                    Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE * 3 );

        // Make a fixed proportion of these challenges balanced and the rest
        // not balanced.
        double rightMassDistance = -leftMassDistance;
        if ( RAND.nextDouble() > 0.2 ) {
            rightMassDistance = -generateRandomValidPlankDistance( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                   Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE * 3 );
        }

        // Create the actual challenge from the pieces.
        return TiltPredictionChallenge.create( new BrickStack( numBricksInLeftStack ),
                                               leftMassDistance,
                                               new BrickStack( numBricksInRightState ),
                                               rightMassDistance );
    }

    private static TiltPredictionChallenge generateModerateTiltPredictionChallenge() {

        // Select the masses, bricks on one side, non bricks on the other.
        Mass leftMass = LOW_PROFILE_MASSES.get( RAND.nextInt( LOW_PROFILE_MASSES.size() ) ).createCopy();
        Mass rightMass = new BrickStack( RAND.nextInt( 4 ) + 1 );
        if ( RAND.nextBoolean() ) {
            // Switch the masses.
            Mass tempMassPrototype = leftMass;
            leftMass = rightMass;
            rightMass = tempMassPrototype;
        }

        // Make the masses almost but not quite balanced.
        List<MassDistancePair> massDistancePairs = positionMassesCloseToBalancing( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                                   Plank.LENGTH / 2 - 2 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                                   leftMass,
                                                                                   rightMass );

        return new TiltPredictionChallenge( massDistancePairs );
    }

    private static TiltPredictionChallenge generateAdvancedTiltPredictionChallenge() {
        // Choose three random masses.
        // Select the masses, bricks on one side, non bricks on the other.
        Mass mass1 = LOW_PROFILE_MASSES.get( RAND.nextInt( LOW_PROFILE_MASSES.size() ) ).createCopy();
        Mass mass2 = LOW_PROFILE_MASSES.get( RAND.nextInt( LOW_PROFILE_MASSES.size() ) ).createCopy();
        Mass mass3 = new BrickStack( RAND.nextInt( 4 ) + 1 );

        // Get a set of mass-distance pairs comprised of these masses
        // positioned in such a way that they are almost, but not quite, balanced.
        List<MassDistancePair> massDistancePairs = positionMassesCloseToBalancing( Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                                   Plank.LENGTH / 2 - Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                                   mass1,
                                                                                   mass2,
                                                                                   mass3 );

        // Create the actual challenge from the pieces.
        return new TiltPredictionChallenge( massDistancePairs );
    }

    /**
     * Take a list of masses and return a set of mass-distance pairs that
     * position the masses such that they are close to being balanced but are
     * NOT balanced.  This is a convenience method that was written to
     * consolidate some code written for generating tilt-prediction challenges.
     */
    private static List<MassDistancePair> positionMassesCloseToBalancing( double minDistance, double maxDistance, Mass... masses ) {
        double bestNetTorque = Double.POSITIVE_INFINITY;
        double minAcceptableTorque = 1; // Determined empirically.
        List<Double> distanceList = new ArrayList<Double>();
        List<Double> bestDistanceList = distanceList;
        for ( int i = 0; i < MAX_GEN_ATTEMPTS; i++ ) {
            distanceList.clear();
            // Generate a set of unique, random, and valid distances for the
            // placement of the masses.
            for ( int j = 0; distanceList.size() < masses.length && j < MAX_GEN_ATTEMPTS; j++ ) {
                double candidateDistance = generateRandomValidPlankDistance( minDistance, maxDistance );
                if ( j == 0 ) {
                    // Randomly invert (or don't) the first random distance.
                    candidateDistance = RAND.nextBoolean() ? -candidateDistance : candidateDistance;
                }
                else {
                    // Make the sign of this distance be the opposite of the
                    // previous one.
                    candidateDistance = distanceList.get( distanceList.size() - 1 ) > 0 ? -candidateDistance : candidateDistance;
                }
                // Check if unique.
                if ( !distanceList.contains( candidateDistance ) ) {
                    distanceList.add( candidateDistance );
                }
            }
            // Handle the unlikely case where enough unique distances couldn't
            // be found.
            if ( distanceList.size() != masses.length ) {
                distanceList.clear();
                for ( int j = 0; j < masses.length; j++ ) {
                    // Just add a linear set of distances.
                    distanceList.add( minDistance + Plank.INTER_SNAP_TO_MARKER_DISTANCE * j );
                    // Output a warning.
                    System.out.println( " Warning: Unable to find enough unique distances for positioning masses." );
                }
            }
            // Calculate the net torque for this set of masses.
            double netTorque = 0;
            for ( int j = 0; j < masses.length; j++ ) {
                netTorque += masses[j].getMass() * distanceList.get( j );
            }
            netTorque = Math.abs( netTorque );
            if ( netTorque < bestNetTorque && netTorque > minAcceptableTorque ) {
                bestNetTorque = netTorque;
                bestDistanceList = new ArrayList<Double>( distanceList );
            }
        }

        // Create the array of mass-distance pairs from the original set of
        // masses and the best randomly-generated distances.
        List<MassDistancePair> repositionedMasses = new ArrayList<MassDistancePair>();
        for ( int i = 0; i < masses.length; i++ ) {
            repositionedMasses.add( new MassDistancePair( masses[i], bestDistanceList.get( i ) ) );
        }
        return repositionedMasses;
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