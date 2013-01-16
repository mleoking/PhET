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
 * <li>creation of challenges (delegated to factory)</li>
 * <li>management of game state</li>
 * <li>management of game results</li>
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    private static final boolean USE_HARD_CODED_CHALLENGES = false;

    private static final int MAX_POINTS_PER_CHALLENGE = 2;
    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 6 );
    private static final Challenge DUMMY_CHALLENGE = new GTL_Challenge( "", Line.createSlopeIntercept( 1, 1, 1 ),
                                                                        EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE,
                                                                        LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );

    // a challenge factory for each level
    private final ChallengeFactory factory1 = new ChallengeFactory1();
    private final ChallengeFactory factory2 = new ChallengeFactory2();
    private final ChallengeFactory factory3 = new ChallengeFactory3();
    private final ChallengeFactory factory4 = new ChallengeFactory4();
    private final ChallengeFactory factory5 = new ChallengeFactory5();
    private final ChallengeFactory factory6 = new ChallengeFactory6();

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
        challenge = new Property<Challenge>( DUMMY_CHALLENGE );

        phase = new Property<GamePhase>( GamePhase.SETTINGS ) {

            // Update fields so that they are accurate before property observers are notified.
            @Override public void set( GamePhase phase ) {
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

    // initializes a new set of challenges for the current level
    private void initChallenges() {

        challengeIndex = 0;

        if ( USE_HARD_CODED_CHALLENGES ) {
            LOGGER.info( "hard-coded challenges are enabled" );
            challenges = ChallengeFactoryHardCoded.createChallenges( settings.level.get(), LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
        }
        else {
            switch( settings.level.get() ) {
                case 1:
                    challenges = factory1.createChallenges( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
                    break;
                case 2:
                    challenges = factory2.createChallenges( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
                    break;
                case 3:
                    challenges = factory3.createChallenges( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
                    break;
                case 4:
                    challenges = factory4.createChallenges( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
                    break;
                case 5:
                    challenges = factory5.createChallenges( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
                    break;
                case 6:
                    challenges = factory6.createChallenges( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
                    break;
                default:
                    throw new IllegalArgumentException( "unsupported level: " + settings.level.get() );
            }
        }
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
        challenge.set( DUMMY_CHALLENGE ); // force an update
        state.set( PlayState.FIRST_CHECK );
    }
}