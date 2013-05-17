// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.balanceandtorquestudy.common.model.Plank;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.BrickStack;

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

    // Used for randomization of challenges.
    private static final Random RAND = new Random();

    // Challenges per challenge set.
    public static final int CHALLENGES_PER_SET = 8;

    // Determine the min and max distances from the center of the plank where
    // masses may be positioned.
    private static final double MAX_DISTANCE_FROM_BALANCE_CENTER_TO_MASS = ( Math.round( Plank.getLength() / Plank.INTER_SNAP_TO_MARKER_DISTANCE / 2 ) - 1 ) * Plank.INTER_SNAP_TO_MARKER_DISTANCE;

    // Lists of all possible tilt-prediction challenges of each of the potential types.
    private static final List<TiltPredictionChallenge> DOMINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> EQUAL_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> SUBORDINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();
    private static final List<TiltPredictionChallenge> CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES = new ArrayList<TiltPredictionChallenge>();

    // Initialize the possible challenges.
    static {
        for ( int numBricks = 1; numBricks <= 6; numBricks++ ) {
            for ( int distanceIncrement = 1; distanceIncrement <= 8; distanceIncrement++ ) {
                DOMINATE_TILT_PREDICTION_CHALLENGES.addAll( generateDominateChallengeList( numBricks, distanceIncrement ) );
                EQUAL_TILT_PREDICTION_CHALLENGES.addAll( generateEqualChallengeList( numBricks, distanceIncrement ) );
                SUBORDINATE_TILT_PREDICTION_CHALLENGES.addAll( generateSubordinateChallengeList( numBricks, distanceIncrement ) );
                CONFLICT_DOMINATE_TILT_PREDICTION_CHALLENGES.addAll( generateConflictDominateChallengeList( numBricks, distanceIncrement ) );
                CONFLICT_EQUAL_TILT_PREDICTION_CHALLENGES.addAll( generateConflictEqualChallengeList( numBricks, distanceIncrement ) );
                CONFLICT_SUBORDINATE_TILT_PREDICTION_CHALLENGES.addAll( generateConflictSubordinateChallengeList( numBricks, distanceIncrement ) );
            }
        }
    }

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
     * Get a set of tilt-prediction challenges.
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

        // Check that the appropriate number of challenges are in the set.
        assert balanceChallengeList.size() == CHALLENGES_PER_SET;

        return balanceChallengeList;
    }

    private static List<TiltPredictionChallenge> generateDominateChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE ) );
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateEqualChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE ) );
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateSubordinateChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE ) );
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateConflictDominateChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE ) );
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateConflictEqualChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE ) );
        return challengeList;
    }

    private static List<TiltPredictionChallenge> generateConflictSubordinateChallengeList( int numBricks, int distanceIncrement ) {
        List<TiltPredictionChallenge> challengeList = new ArrayList<TiltPredictionChallenge>();
        challengeList.add( TiltPredictionChallenge.create( new BrickStack( numBricks ),
                                                           distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE,
                                                           new BrickStack( numBricks ),
                                                           -distanceIncrement * Plank.INTER_SNAP_TO_MARKER_DISTANCE ) );
        return challengeList;
    }
}