// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.ArrayList;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.games.GameTimer;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Model for the "Line Game" module. Responsibilities include:
 * <ul>
 * <li>creation of challenges</li>
 * <li>management of game state</li>
 * <li>management of game results</li>
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    private static final int MAX_POINTS_PER_CHALLENGE = 2;
    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 6 );

    public final GameSettings settings;
    public final GameTimer timer;
    public final GameResults results;
    public final Property<GamePhase> phase;
    public final Property<PlayState> state;

    public final Property<Challenge> challenge; // the current challenge
    private ArrayList<Challenge> challenges = new ArrayList<Challenge>();
    private int challengeIndex;

    // Default is a graph with uniform quadrants.
    public LineGameModel() {

        settings = new GameSettings( LEVELS_RANGE, true /* soundEnabled */, true /* timerEnabled */ );
        timer = new GameTimer();
        results = new GameResults( LEVELS_RANGE );

        // initial value is meaningless, but must be non-null
        challenge = new Property<Challenge>( new GTL_Challenge( "", Line.createSlopeIntercept( 1, 1, 1 ), LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE,
                                                                 LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE ) );

        phase = new Property<GamePhase>( GamePhase.SETTINGS ) {

            // Update fields so that they are accurate before property observers are notified.
            @Override public void set( GamePhase phase ) {
                LOGGER.info( "game phase = " + phase );
                if ( phase == GamePhase.SETTINGS ) {
                    state.set( PlayState.NONE );
                    timer.stop();
                }
                else if ( phase == GamePhase.PLAY ) {
                    initChallenges();
                    state.set( PlayState.FIRST_CHECK );
                    results.score.set( 0 );
                    timer.start();
                }
                else if ( phase == GamePhase.RESULTS ) {
                    state.set( PlayState.NONE );
                    timer.stop();
                    updateBestTime();
                }
                else {
                    throw new UnsupportedOperationException( "unsupported game phase = " + phase );
                }
                super.set( phase );
            }
        };

        initChallenges();

        state = new Property<PlayState>( PlayState.NONE ) {{
            addObserver( new VoidFunction1<PlayState>() {
                public void apply( PlayState state ) {
                    LOGGER.info( "play state = " + state );
                    if ( state == PlayState.FIRST_CHECK ) {
                        if ( challengeIndex == challenges.size() ) {
                            // game has been completed
                            phase.set( GamePhase.RESULTS );
                        }
                        else {
                            // next challenge
                            challenge.set( challenges.get( challengeIndex ) );
                            challengeIndex++;
                        }
                    }
                    else if ( state == PlayState.NEXT ) {
                        challenge.get().setAnswerVisible( true );
                    }
                }
            } );
        }};
    }

    private void initChallenges() {
        challenges = ChallengeFactory.createChallenges( settings.level.get(), LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
        challengeIndex = 0;
    }

    public boolean isPerfectScore() {
        return results.score.get() == getPerfectScore();
    }

    // Gets the number of points in a perfect score (ie, correct answers for all challenges on the first try)
    public int getPerfectScore() {
        return challenges.size() * computePoints( 1 );
    }

    // Updates the best time for the current level, at the end of a timed game with a perfect score.
    private void updateBestTime() {
        assert ( !timer.isRunning() );
        if ( settings.timerEnabled.get() && results.score.get() == getPerfectScore() ) {
            results.updateBestTime( settings.level.get(), timer.time.get() );
        }
    }

    // Compute points to be awarded for a correct answer.
    public int computePoints( int attempts ) {
        return Math.max( 0, MAX_POINTS_PER_CHALLENGE - attempts + 1 );
    }

    /**
     * Skips the current challenge.
     * This is a developer feature.
     * Score and best times are meaningless after using this.
     */
    public void skipCurrentChallenge() {
        assert ( PhetApplication.getInstance().isDeveloperControlsEnabled() );
        state.set( PlayState.NEXT );
        state.set( PlayState.FIRST_CHECK );
    }

    /**
     * Replays the current challenge.
     * This is a developer feature.
     * Score and best times are meaningless after using this.
     */
    public void replayCurrentChallenge() {
        assert ( PhetApplication.getInstance().isDeveloperControlsEnabled() );
        challenge.get().reset();
        challengeIndex--;
        state.set( PlayState.FIRST_CHECK );
    }
}