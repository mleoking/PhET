// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.util.HashMap;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * Model for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    private static final int CHALLENGES_PER_GAME = 5;
    private static final int MAX_POINTS_PER_CHALLENGE = 2;

    // phases of a game, mutually exclusive
    public enum GamePhase {
        SETTINGS,  // user is choosing game settings
        PLAY, // user is playing the game
        RESULTS // user is receiving results of playing the game
    }

    /*
     * States during the "play" phase of a game, mutually exclusive.
     * For lack of better names, the state names correspond to the main action that
     * the user can take in that state.  For example. the CHECK state is where the user
     * can enter coefficients and press the "Check" button to check their answer.
     */
    public static enum PlayState {
        CHECK,
        TRY_AGAIN,
        SHOW_ANSWER,
        NEXT,
        NEW_GAME
    }

    public final GameSettings settings;
    public final GameTimer timer;
    private final HashMap<Integer, Long> bestTimes; // best times, maps level to time in ms
    private boolean isNewBestTime; // is the time for this game a new best time?
    public final Property<Integer> score; // how many points the user has earned for the current game
    public final Property<GamePhase> phase;
    public final Property<PlayState> state;

    public final Property<MatchingChallenge> challenge; // the current challenge

    public LineGameModel() {

        settings = new GameSettings( new IntegerRange( 1, 3 ), true /* soundEnabled */, true /* timerEnabled */ );

        score = new Property<Integer>( 0 );

        challenge = new Property<MatchingChallenge>( new MatchingChallenge( new StraightLine( 10, 5, 3, Color.RED ) ) );

        // time
        timer = new GameTimer( new ConstantDtClock( 1000 / 5, 1 ) );
        bestTimes = new HashMap<Integer, Long>();
        for ( int i = settings.level.getMin(); i <= settings.level.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }

        phase = new Property<GamePhase>( GamePhase.SETTINGS ) {

            // Update fields so that they are accurate before property observers are notified.
            @Override public void set( GamePhase phase ) {
                LOGGER.info( "game phase = " + phase );
                if ( phase == GamePhase.SETTINGS ) {
                    //TODO
                }
                else if ( phase == GamePhase.PLAY ) {
                    //TODO set up challenges
                    timer.start();
                }
                else if ( phase == GamePhase.RESULTS ) {
                    timer.stop();
                    updateBestTime();
                }
                else {
                    throw new UnsupportedOperationException( "unsupported game phase = " + phase );
                }
                super.set( phase );
            }
        };

        state = new Property<PlayState>( PlayState.CHECK ) {{
            addObserver( new VoidFunction1<PlayState>() {
                public void apply( PlayState state ) {
                    LOGGER.info( "play state = " + state );
                    //TODO
                }
            } );
        }};
    }

    public boolean isPerfectScore() {
        return score.get() == getPerfectScore();
    }

    // Gets the number of points in a perfect score (ie, correct answers for all challenges on the first try)
    public int getPerfectScore() {
       return CHALLENGES_PER_GAME * computePoints( 1 );
    }

    /**
     * Gets the best time for a specified level.
     * If this returns zero, then there is no best time for the level.
     *
     * @param level
     * @return
     */
    public long getBestTime( int level ) {
        Long bestTime = bestTimes.get( level );
        if ( bestTime == null ) {
            bestTime = 0L;
        }
        return bestTime;
    }

    public boolean isNewBestTime() {
        return isNewBestTime;
    }

    // Updates the best time for the current level, at the end of a game with a perfect score.
    private void updateBestTime() {
        assert( !timer.isRunning() );
        if ( settings.timerEnabled.get() && score.get() == getPerfectScore() ) {
            isNewBestTime = false;
            if ( bestTimes.get( settings.level.get() ) == 0 ) {
                // there was no previous time for this level
                bestTimes.put( settings.level.get(), timer.time.get() );
            }
            else if ( timer.time.get() < bestTimes.get( settings.level.get() ) ) {
                // we have a new best time for this level
                bestTimes.put( settings.level.get(), timer.time.get() );
                isNewBestTime = true;
            }
        }
    }

    // Compute points to be awarded for a correct answer.
    private static int computePoints( int attempts ) {
        return Math.max( 0, MAX_POINTS_PER_CHALLENGE - attempts + 1 ) ;
    }
}