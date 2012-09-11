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
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;

/**
 * Model for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    private static final int GRID_VIEW_UNITS = 400; // max dimension (width or height) of the grid in the view
    private static final int CHALLENGES_PER_GAME = 4;
    private static final int MAX_POINTS_PER_CHALLENGE = 2;
    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 4 );

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
    public final Property<Challenge> challenge; // the current challenge
    private Challenge[] challenges = new Challenge[CHALLENGES_PER_GAME];
    private int challengeIndex;
    public final PointTool pointTool1, pointTool2;
    private final ObservableList<Line> allLines;

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

        challenge = new Property<Challenge>( new SI_EG_SlopeIntercept_Challenge( Line.createSlopeIntercept( 1, 1, 1, Color.BLACK ), mvtGraphTheLine ) ); // initial value is meaningless

        allLines = new ObservableList<Line>();
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
        challenge.addObserver( new VoidFunction1<Challenge>() {
            public void apply( Challenge challenge ) {
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
            allLines.add( challenge.get().answer.withColor( GameConstants.CORRECT_ANSWER_COLOR ) );
        }
    }

    private void initChallenges() {
        //TODO create different types of challenges, randomized for level
        int index = 0;
        if ( settings.level.get() == 1 ) {
            challenges[index++] = new SI_EG_Intercept_Challenge( Line.createSlopeIntercept( 1, 1, -2, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
            challenges[index++] = new SI_EG_Slope_Challenge( Line.createSlopeIntercept( 5, 1, 1, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
            challenges[index++] = new SI_EG_SlopeIntercept_Challenge( Line.createSlopeIntercept( 4, 2, 3, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
            challenges[index++] = new SI_EG_Points_Challenge( Line.createSlopeIntercept( 3, 3, -3, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
        }
        else if ( settings.level.get() == 2 ) {
            challenges[index++] = new PS_EG_Slope_Challenge( Line.createPointSlope( -1, -3, 1, 2, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
            challenges[index++] = new PS_EG_Slope_Challenge( Line.createPointSlope( -1, -3, 1, 2, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
            challenges[index++] = new PS_EG_Slope_Challenge( Line.createPointSlope( -1, -3, 1, 2, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
            challenges[index++] = new PS_EG_Slope_Challenge( Line.createPointSlope( -1, -3, 1, 2, GameConstants.ANSWER_COLOR ), mvtGraphTheLine );
        }
        assert ( challenges.length == CHALLENGES_PER_GAME );
        challengeIndex = 0;
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