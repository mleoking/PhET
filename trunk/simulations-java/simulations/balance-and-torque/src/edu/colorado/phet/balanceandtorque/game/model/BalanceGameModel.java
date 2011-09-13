// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.BalanceAndTorqueSharedConstants;
import edu.colorado.phet.balanceandtorque.teetertotter.model.AttachmentBar;
import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.FulcrumAbovePlank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.ShapeModelElement;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Main model class for the balance game.
 *
 * @author John Blanco
 */
public class BalanceGameModel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants that define some of the attributes of the game.
    public static final int MAX_LEVELS = 4;
    private static final int PROBLEMS_PER_SET = 5;
    private static final int MAX_POINTS_PER_PROBLEM = 2;
    private static final int MAX_SCORE_PER_GAME = PROBLEMS_PER_SET * MAX_POINTS_PER_PROBLEM;
    private static final int MAX_ATTEMPTS_TO_ANSWER = 2;

    // Information about the relationship between the plank and fulcrum.
    private static final double FULCRUM_HEIGHT = 0.85; // In meters.
    private static final double PLANK_HEIGHT = 0.75; // In meters.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( BalanceAndTorqueSharedConstants.FRAME_RATE );

    // Game settings information.
    public final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, MAX_LEVELS, 1 ), true, true );

    // Track the best times on a per-level basis.
    private final HashMap<Integer, Double> mapLevelToBestTime = new HashMap<Integer, Double>();

    // Property that tracks the current score.
    private final Property<Integer> scoreProperty = new Property<Integer>( 0 );

    // Property that tracks the current game state.
    public final Property<GameState> gameStateProperty = new Property<GameState>( GameState.OBTAINING_GAME_SETUP );

    // Counters used to track progress on the game.
    private int challengeCount = 0;
    private int incorrectGuessesOnCurrentChallenge = 0;

    // Current set of challenges, which collectively comprise a single game, on
    // which the user is currently working.
    private List<BalanceChallenge> currentChallengeList;

    // Fixed masses that sit on the plank and that the user must attempt to balance.
    public ObservableList<BalanceChallenge.MassDistancePair> massesToBeBalanced = new ObservableList<BalanceChallenge.MassDistancePair>();

    // Masses that the user moves on to the plank to counterbalance the fixed masses.
    public ObservableList<Mass> movableMasses = new ObservableList<Mass>();

    // Support column.  In this model, there is only one.
    private final ShapeModelElement supportColumn;

    // Property that controls whether two, one or zero columns are supporting the plank.
    public final Property<ColumnState> supportColumnState = new Property<ColumnState>( ColumnState.SINGLE_COLUMN );

    // Plank upon which the various masses can be placed.
    private final Plank plank = new Plank( clock,
                                           new Point2D.Double( 0, PLANK_HEIGHT ),
                                           new Point2D.Double( 0, FULCRUM_HEIGHT ),
                                           supportColumnState );

    // Bar that attaches the fulcrum to the pivot point.
    private final AttachmentBar attachmentBar = new AttachmentBar( plank );

    // Fulcrum on which the plank pivots
    private final FulcrumAbovePlank fulcrum = new FulcrumAbovePlank( 1, FULCRUM_HEIGHT );

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public BalanceGameModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clock.getDt() );
            }
        } );

        // Note: These are positioned so that the closing window that is
        // placed on them (the red x) is between two snap-to points on the
        // plank that the they don't get blocked by force vectors.
        final double plankX = 1.8;

        final double WIDTH = 0.35;
        final DoubleGeneralPath path = new DoubleGeneralPath( plankX - WIDTH / 2, 0 ) {{
            lineTo( plankX + WIDTH / 2, 0 );
            lineTo( plankX + WIDTH / 2, plank.getSurfaceYValue( plankX + WIDTH / 2 ) );
            lineTo( plankX - WIDTH / 2, plank.getSurfaceYValue( plankX - WIDTH / 2 ) );
            closePath();
        }};
        supportColumn = new ShapeModelElement( path.getGeneralPath() );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    private void stepInTime( double dt ) {
        for ( Mass mass : new ArrayList<Mass>( movableMasses ) ) {
            mass.stepInTime( dt );
        }
        for ( BalanceChallenge.MassDistancePair massDistancePair : massesToBeBalanced ) {
            massDistancePair.mass.stepInTime( dt );
        }
    }

    public FulcrumAbovePlank getFulcrum() {
        return fulcrum;
    }

    public Plank getPlank() {
        return plank;
    }

    public AttachmentBar getAttachmentBar() {
        return attachmentBar;
    }

    public int getMaximumPossibleScore() {
        return MAX_SCORE_PER_GAME;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public int getLevel() {
        return gameSettings.level.get();
    }

    public boolean isTimerEnabled() {
        return gameSettings.timerEnabled.get();
    }

    public boolean isSoundEnabled() {
        return gameSettings.soundEnabled.get();
    }

    public double getTime() {
        return clock.getSimulationTime();
    }

    /**
     * Get the best time for the specified level.  Note that levels start at 1
     * and not 0, so there is some offsetting here.
     *
     * @param level
     * @return time in seconds
     */
    public double getBestTime( int level ) {
        assert level > 0 && level <= MAX_LEVELS;
        return ( mapLevelToBestTime.containsKey( level ) ? mapLevelToBestTime.get( level ).doubleValue() : Double.POSITIVE_INFINITY );
    }

    public boolean isNewBestTime() {
        return getTime() == getBestTime( getLevel() ) && isTimerEnabled();
    }

    /**
     * Returns true if at least one best time has been recorded for the
     * specified level, false otherwise.  Note that levels start at 1 and not
     * 0.
     *
     * @return
     */
    public boolean isBestTimeRecorded( int level ) {
        return mapLevelToBestTime.containsKey( level );
    }

    public Property<Integer> getScoreProperty() {
        return scoreProperty;
    }

    public void showGameInitDialog() {
        gameStateProperty.set( GameState.OBTAINING_GAME_SETUP );
    }

    public void startGame() {
        // Initialize the game timers, counters, etc.
        scoreProperty.set( 0 );
        challengeCount = 0;
        clock.resetSimulationTime();
        clock.start();

        // Set up the challenges.
        currentChallengeList = BalanceChallengeSetFactory.getChallengeSet( getLevel(), PROBLEMS_PER_SET );

        //Set up the model for the next challenge
        setChallenge( currentChallengeList.get( 0 ), true );

        //Switch to the new state, will create graphics for the challenge
        gameStateProperty.set( GameState.PRESENTING_INTERACTIVE_CHALLENGE );
    }

    public void checkAnswer() {
        supportColumnState.set( ColumnState.NONE );
        if ( plank.isBalanced() ) {
            // The user answered the challenge correctly.
            gameStateProperty.set( GameState.SHOWING_CORRECT_ANSWER_FEEDBACK );
            if ( incorrectGuessesOnCurrentChallenge == 0 ) {
                // User got it right the first time.
                scoreProperty.set( scoreProperty.get() + MAX_POINTS_PER_PROBLEM );
            }
            else {
                // User got it wrong at first, but got it right now.
                scoreProperty.set( scoreProperty.get() + MAX_POINTS_PER_PROBLEM - incorrectGuessesOnCurrentChallenge );
            }
        }
        else {
            // The user got it wrong.
            incorrectGuessesOnCurrentChallenge++;
            if ( incorrectGuessesOnCurrentChallenge < MAX_ATTEMPTS_TO_ANSWER ) {
                gameStateProperty.set( GameState.SHOWING_INCORRECT_ANSWER_FEEDBACK_TRY_AGAIN );
            }
            else {
                gameStateProperty.set( GameState.SHOWING_INCORRECT_ANSWER_FEEDBACK_MOVE_ON );
            }
        }
    }

    public void nextChallenge() {
        challengeCount++;
        incorrectGuessesOnCurrentChallenge = 0;
        if ( challengeCount < PROBLEMS_PER_SET ) {
            setChallenge( currentChallengeList.get( challengeCount ), true );
            gameStateProperty.set( GameState.PRESENTING_INTERACTIVE_CHALLENGE );
        }
        else {
            // See if this is a new best time and, if so, record it.
            if ( scoreProperty.get() == MAX_SCORE_PER_GAME ) {
                // Perfect game.  See if new best time.
                double timeForChallenge = clock.getSimulationTime();
                if ( timeForChallenge < getBestTime( getLevel() ) ) {
                    // New best.
                    mapLevelToBestTime.put( getLevel(), timeForChallenge );
                }
            }
            // Done with this game, show the results.
            gameStateProperty.set( GameState.SHOWING_GAME_RESULTS );
        }
    }

    private void setChallenge( BalanceChallenge balanceChallenge, boolean addColumn ) {

        // Clear out the previous challenge (if there was one).
        plank.removeAllMasses();
        massesToBeBalanced.clear();
        for ( Mass mass : movableMasses ) {
            mass.userControlled.removeAllObservers();
        }
        movableMasses.clear();

        // Set up the new challenge.
        for ( BalanceChallenge.MassDistancePair massDistancePair : balanceChallenge.fixedMasses ) {
            massesToBeBalanced.add( massDistancePair );
            plank.addMassToSurface( massDistancePair.mass, massDistancePair.distance );
        }
        for ( final Mass mass : balanceChallenge.movableMasses ) {
            // TODO: Put movable masses on the right side until tool box is in place.
            final Point2D initialPosition = new Point2D.Double( 3, 0 );
            mass.setPosition( initialPosition );
            mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean userControlled ) {
                    if ( !userControlled ) {
                        // The user has dropped this mass.
                        if ( !plank.addMassToSurface( mass ) ) {
                            // The attempt to add mass to surface of plank failed,
                            // probably because the area below the mass is full,
                            // or because the mass wasn't over the plank.
                            mass.setPosition( initialPosition );
                        }
                    }
                }
            } );

            movableMasses.add( mass );
        }

        // Add the column if desired.
        //
        if ( addColumn ) {
            supportColumnState.set( ColumnState.SINGLE_COLUMN );
        }
    }

    public void tryAgain() {
        setChallenge( currentChallengeList.get( challengeCount ), true );
        gameStateProperty.set( GameState.PRESENTING_INTERACTIVE_CHALLENGE );
    }

    public void displayCorrectAnswer() {
        BalanceChallenge currentChallenge = currentChallengeList.get( challengeCount );

        // Put the challenge in its initial state, with none of the movable
        // masses on the plank.
        setChallenge( currentChallenge, false );

        // Display the solution.
        for ( BalanceChallenge.MassDistancePair solutionMassDistancePair : currentChallenge.solutionToPresent ) {
            plank.addMassToSurface( solutionMassDistancePair.mass, solutionMassDistancePair.distance );
        }

        supportColumnState.set( ColumnState.NONE );
        gameStateProperty.set( GameState.DISPLAYING_CORRECT_ANSWER );
    }

    public ShapeModelElement getSupportColumn() {
        return supportColumn;
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * The set of game states.
     */
    public static enum GameState {
        OBTAINING_GAME_SETUP,                        // Getting the game setup information from the user, i.e. level, sound on/off, etc.
        PRESENTING_INTERACTIVE_CHALLENGE,            // Presenting the challenge that the user must interact with and then test their answer.
        SHOWING_CORRECT_ANSWER_FEEDBACK,             // Showing the feedback that indicates a correct answer (e.g. a simley face).
        SHOWING_INCORRECT_ANSWER_FEEDBACK_TRY_AGAIN, // Showing the feedback that indicates an incorrect answer (e.g. a frowny face) w opportunity to try again.
        SHOWING_INCORRECT_ANSWER_FEEDBACK_MOVE_ON,   // Showing the feedback that indicates an incorrect answer (e.g. a frowny face), w opportunity to see correct answer.
        DISPLAYING_CORRECT_ANSWER,                   // Displaying the correct answer.
        SHOWING_GAME_RESULTS                         // Showing the overall results, i.e. score, best time, etc.
    }
}
