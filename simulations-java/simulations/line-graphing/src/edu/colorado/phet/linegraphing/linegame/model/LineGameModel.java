// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.games.GameTimer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.LineFactory.SlopeInterceptLineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.common.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.linegame.model.MatchingChallenge.SlopeInterceptChallenge;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;

/**
 * Model for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    private static final int GRID_VIEW_UNITS = 400; // max dimension (width or height) of the grid in the view
    private static final int CHALLENGES_PER_GAME = 5;
    private static final int MAX_POINTS_PER_CHALLENGE = 2;
    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 3 );

    // phases of a game, mutually exclusive
    public enum GamePhase {
        SETTINGS,  // user is choosing game settings
        PLAY, // user is playing the game
        RESULTS // user is receiving results of playing the game
    }

    /*
     * States during the "play" phase of a game, mutually exclusive.
     * For lack of better names, the state names correspond to the main action that
     * the user can take in that state.  For example. the FIRST_CHECK state is where the user
     * has their first opportunity to press the "Check" button to check their answer.
     */
    public static enum PlayState {
        FIRST_CHECK,
        TRY_AGAIN,
        SECOND_CHECK,
        SHOW_ANSWER,
        NEXT,
        NONE // use this value when game is not in the "play" phase
    }

    private final ModelViewTransform mvtGraphTheLine, mvtMakeTheEquation;

    public final GameSettings settings;
    public final GameTimer timer;
    public final GameResults results;
    public final Property<GamePhase> phase;
    public final Property<PlayState> state;

    public final Graph graph; // the graph that plots the lines
    public final Property<SlopeInterceptChallenge> challenge; // the current challenge
    private SlopeInterceptChallenge[] challenges = new SlopeInterceptChallenge[CHALLENGES_PER_GAME];
    private int challengeIndex;
    public final PointTool pointTool1, pointTool2;
    private final ObservableList<PointSlopeLine> allLines;

    // Default is a graph with uniform quadrants.
    public LineGameModel() {
        this( LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
    }

    private LineGameModel( IntegerRange xRange, IntegerRange yRange ) {

        final double mvtScale = GRID_VIEW_UNITS / Math.max( xRange.getLength(), yRange.getLength() ); // view units / model units
        mvtGraphTheLine = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 700, 300 ), mvtScale, -mvtScale ); // graph on right, y inverted
        mvtMakeTheEquation = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 200, 300 ), mvtScale, -mvtScale ); // graph on left, y inverted

        settings = new GameSettings( LEVELS_RANGE, true /* soundEnabled */, true /* timerEnabled */ );
        timer = new GameTimer();
        results = new GameResults( LEVELS_RANGE );

        graph = new Graph( xRange, yRange );

        challenge = new Property<SlopeInterceptChallenge>( new SlopeInterceptChallenge( new SlopeInterceptLine( 1, 1, 1, Color.BLACK ), mvtGraphTheLine ) ); // initial value is meaningless

        allLines = new ObservableList<PointSlopeLine>();
        this.pointTool1 = new PointTool( new Vector2D( xRange.getMin() + ( 0.65 * xRange.getLength() ), yRange.getMin() - 1 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( xRange.getMin() + ( 0.95 * xRange.getLength() ), yRange.getMin() - 4 ), Orientation.DOWN, allLines );

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
                        if ( challengeIndex == challenges.length ) {
                            // game has been completed
                            phase.set( GamePhase.RESULTS );
                        }
                        else {
                            // next challenge
                            challenge.set( challenges[challengeIndex] );
                            challengeIndex++;
                            // reset location of point tools
                            pointTool1.location.reset();
                            pointTool2.location.reset();
                        }
                    }
                    else if ( state == PlayState.NEXT ) {
                        updateListOfLines();
                    }
                }
            } );
        }};

        // When the user's guess changes, update the list of lines.
        challenge.addObserver( new VoidFunction1<SlopeInterceptChallenge>() {
            public void apply( SlopeInterceptChallenge challenge ) {
                challenge.guess.addObserver( new SimpleObserver() {
                    public void update() {
                        updateListOfLines();
                    }
                } );
            }
        } );
    }

    private void updateListOfLines() {
        allLines.clear();
        allLines.add( challenge.get().guess.get() );
        if ( state.get() == PlayState.NEXT && !challenge.get().isCorrect() ) {
            // user got it wrong and we're showing the correct answer
            allLines.add( challenge.get().lineFactory.withColor( challenge.get().answer, GameConstants.CORRECT_ANSWER_COLOR ) );
        }
    }

    private void initChallenges() {
        //TODO create different types of challenges, randomized for level
        challengeIndex = 0;
        challenges[0] = new SlopeInterceptChallenge( new SlopeInterceptLine( 4, 2, 3, GameConstants.GIVEN_COLOR ), mvtGraphTheLine );
        challenges[1] = new SlopeInterceptChallenge( new SlopeInterceptLine( 5, 1, 1, GameConstants.GIVEN_COLOR ), mvtGraphTheLine );
        challenges[2] = new SlopeInterceptChallenge( new SlopeInterceptLine( -3, 3, -2, GameConstants.GIVEN_COLOR ), mvtGraphTheLine );
        challenges[3] = new SlopeInterceptChallenge( new SlopeInterceptLine( 10, 2, -6, GameConstants.GIVEN_COLOR ), mvtGraphTheLine );
        challenges[4] = new SlopeInterceptChallenge( new SlopeInterceptLine( 0, 3, 2, GameConstants.GIVEN_COLOR ), mvtGraphTheLine );
    }

    public boolean isPerfectScore() {
        return results.score.get() == getPerfectScore();
    }

    // Gets the number of points in a perfect score (ie, correct answers for all challenges on the first try)
    public int getPerfectScore() {
        return CHALLENGES_PER_GAME * computePoints( 1 );
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
}