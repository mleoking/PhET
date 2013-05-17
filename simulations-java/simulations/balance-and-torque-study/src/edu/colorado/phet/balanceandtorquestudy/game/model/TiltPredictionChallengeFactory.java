// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorquestudy.common.model.Plank;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.BrickStack;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.ModelComponentTypes.*;

/**
 * This is a specialized version of a factory class that generates challenges
 * for the game.  This version was created specifically for the Stanford study
 * and only creates tilt prediction challenges, and does so in a way that was
 * specified by the Stanford researchers.
 *
 * @author John Blanco
 */
public class TiltPredictionChallengeFactory {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Random RAND = new Random();
    private static final int MAX_NUM_BRICKS_IN_STACK = 6;
    private static final int MAX_DISTANCE_INCREMENTS = 8;

    // Lists of all valid tilt-prediction challenges for each of the defined types.
    private static final List<TiltPredictionChallenge> DOMINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> EQUAL_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> SUBORDINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();

    // Initialize the possible challenge lists.
    static {
        for ( int numBricks = 1; numBricks <= MAX_NUM_BRICKS_IN_STACK; numBricks++ ) {
            for ( int distanceIncrement = 1; distanceIncrement <= MAX_DISTANCE_INCREMENTS; distanceIncrement++ ) {
                DOMINATE_TILT_PREDICTION_CHALLENGES.addAll( generateDominateChallengeList( numBricks, distanceIncrement ) );
                EQUAL_TILT_PREDICTION_CHALLENGES.addAll( generateEqualChallengeList( numBricks, distanceIncrement ) );
                SUBORDINATE_TILT_PREDICTION_CHALLENGES.addAll( generateSubordinateChallengeList( numBricks, distanceIncrement ) );
                CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES.addAll( generateConflictDominateChallengeList( numBricks, distanceIncrement ) );
                CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.addAll( generateConflictEqualChallengeList( numBricks, distanceIncrement ) );
                CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.addAll( generateConflictSubordinateChallengeList( numBricks, distanceIncrement ) );
            }
        }
    }

    // Indexes for each challenge type.
    private static int dominateChallengeIndex = RAND.nextInt( DOMINATE_TILT_PREDICTION_CHALLENGES.size() );
    private static int equalChallengeIndex = RAND.nextInt( EQUAL_TILT_PREDICTION_CHALLENGES.size() );
    private static int subordinateChallengeIndex = RAND.nextInt( SUBORDINATE_TILT_PREDICTION_CHALLENGES.size() );
    private static int conflictDominateChallengeIndex = RAND.nextInt( CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES.size() );
    private static int conflictEqualChallengeIndex = RAND.nextInt( CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.size() );
    private static int conflictSubordinateChallengeIndex = RAND.nextInt( CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.size() );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    private TiltPredictionChallengeFactory() {
        // Not meant to be instantiated.
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /*
     * Create a set of tilt-prediction challenges.
     */
    public static List<BalanceGameChallenge> generateChallengeSet() {
        List<BalanceGameChallenge> balanceChallengeList = new ArrayList<BalanceGameChallenge>();
        balanceChallengeList.add( DOMINATE_TILT_PREDICTION_CHALLENGES.get( dominateChallengeIndex ) );
        dominateChallengeIndex = dominateChallengeIndex + 1 % DOMINATE_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( EQUAL_TILT_PREDICTION_CHALLENGES.get( equalChallengeIndex ) );
        equalChallengeIndex = equalChallengeIndex + 1 % EQUAL_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( SUBORDINATE_TILT_PREDICTION_CHALLENGES.get( subordinateChallengeIndex ) );
        subordinateChallengeIndex = subordinateChallengeIndex + 1 % SUBORDINATE_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES.get( conflictDominateChallengeIndex ) );
        conflictDominateChallengeIndex = conflictDominateChallengeIndex + 1 % CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.get( conflictEqualChallengeIndex ) );
        conflictEqualChallengeIndex = conflictEqualChallengeIndex + 1 % CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.get( conflictSubordinateChallengeIndex ) );
        conflictSubordinateChallengeIndex = conflictSubordinateChallengeIndex + 1 % CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.get( conflictEqualChallengeIndex ) );
        conflictEqualChallengeIndex = conflictEqualChallengeIndex + 1 % CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.size();
        balanceChallengeList.add( CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.get( conflictSubordinateChallengeIndex ) );
        conflictSubordinateChallengeIndex = conflictSubordinateChallengeIndex + 1 % CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.size();

        return balanceChallengeList;
    }

    private static List<TiltPredictionChallenge> generateDominateChallengeList( int numBricks1, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        for ( int numBricks2 = 1; numBricks2 < numBricks1; numBricks2++ ) {
            challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                                               distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                               new BrickStack( numBricks2 ),
                                                               -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                               tiltPredictionDominateChallenge ) );
        }
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateEqualChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           tiltPredictionEqualChallenge ) );
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateSubordinateChallengeList( int numBricks, int distanceIncrement1 ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        for ( int distanceIncrement2 = 1; distanceIncrement2 < distanceIncrement1; distanceIncrement2++ ) {
            challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                               distanceIncrement1 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                               new BrickStack( numBricks ),
                                                               -distanceIncrement2 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                               tiltPredictionSubordinateChallenge ) );
        }
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateConflictDominateChallengeList( int numBricks1, int distanceIncrement1 ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        for ( int numBricks2 = 1; numBricks2 < numBricks1; numBricks2++ ) {
            for ( int distanceIncrement2 = distanceIncrement1 + 1; distanceIncrement2 <= MAX_DISTANCE_INCREMENTS; distanceIncrement2++ ) {
                if ( numBricks1 * distanceIncrement1 > numBricks2 * distanceIncrement2 ) {
                    challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                                                       distanceIncrement1 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                       new BrickStack( numBricks2 ),
                                                                       -distanceIncrement2 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                       tiltPredictionConflictDominateChallenge ) );

                }
            }
        }
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateConflictEqualChallengeList( int numBricks1, int distanceIncrement1 ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        for ( int numBricks2 = 1; numBricks2 < numBricks1; numBricks2++ ) {
            for ( int distanceIncrement2 = distanceIncrement1 + 1; distanceIncrement2 <= MAX_DISTANCE_INCREMENTS; distanceIncrement2++ ) {
                if ( numBricks1 * distanceIncrement1 == numBricks2 * distanceIncrement2 ) {
                    challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                                                       distanceIncrement1 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                       new BrickStack( numBricks2 ),
                                                                       -distanceIncrement2 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                       tiltPredictionConflictEqualChallenge ) );

                }
            }
        }
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateConflictSubordinateChallengeList( int numBricks1, int distanceIncrement1 ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        for ( int numBricks2 = 1; numBricks2 < numBricks1; numBricks2++ ) {
            for ( int distanceIncrement2 = distanceIncrement1 + 1; distanceIncrement2 <= MAX_DISTANCE_INCREMENTS; distanceIncrement2++ ) {
                if ( numBricks1 * distanceIncrement1 < numBricks2 * distanceIncrement2 ) {
                    challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks1 ),
                                                                       distanceIncrement1 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                       new BrickStack( numBricks2 ),
                                                                       -distanceIncrement2 * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                                       tiltPredictionConflictSubordinateChallenge ) );

                }
            }
        }
        return challengeList;
    }
}