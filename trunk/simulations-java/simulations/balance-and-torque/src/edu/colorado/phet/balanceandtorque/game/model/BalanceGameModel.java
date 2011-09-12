// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.BalanceAndTorqueSharedConstants;
import edu.colorado.phet.balanceandtorque.teetertotter.model.AttachmentBar;
import edu.colorado.phet.balanceandtorque.teetertotter.model.FulcrumAbovePlank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.balanceandtorque.teetertotter.model.SupportColumn;
import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

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

    // Count of challenges encountered so far at the current level.
    private int challengeCount = 0;

    private int incorrectGuessesOnCurrentChallenge = 0;

    // A list of all the masses in the model
    public final ObservableList<Mass> massList = new ObservableList<Mass>();

    // Fulcrum on which the plank pivots
    private final FulcrumAbovePlank fulcrum = new FulcrumAbovePlank( 1, FULCRUM_HEIGHT );

    // Support columns
    private final List<SupportColumn> supportColumns = new ArrayList<SupportColumn>() {{
        // Note: These are positioned so that the closing window that is
        // placed on them (the red x) is between two snap-to points on the
        // plank that the they don't get blocked by force vectors.
        add( new SupportColumn( PLANK_HEIGHT, -1.625 ) );
        add( new SupportColumn( PLANK_HEIGHT, 1.625 ) );
    }};

    // Property that controls whether the columns are supporting the plank.
    public final BooleanProperty supportColumnsActive = new BooleanProperty( true );

    // Plank upon which the various masses can be placed.
    private final Plank plank = new Plank( clock,
                                           new Point2D.Double( 0, PLANK_HEIGHT ),
                                           new Point2D.Double( 0, FULCRUM_HEIGHT ),
                                           supportColumnsActive );

    // Bar that attaches the fulcrum to the pivot point.
    private final AttachmentBar attachmentBar = new AttachmentBar( plank );

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public BalanceGameModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clock.getDt() );
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    private void stepInTime( double dt ) {
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            mass.stepInTime( dt );
        }
    }

    // Adds a mass to the model.
    public UserMovableModelElement addMass( final Mass mass ) {
        mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( !userControlled ) {
                    // The user has dropped this mass.
                    if ( !plank.addMassToSurface( mass ) ) {
                        // The attempt to add mass to surface of plank failed,
                        // probably because the area below the mass is full,
                        // or because the mass wasn't over the plank.
                        removeMassAnimated( mass );
                    }
                }
            }
        } );
        massList.add( mass );
        return mass;
    }

    /**
     * Remove the mass from the model and animate its removal.
     *
     * @param mass
     */
    private void removeMassAnimated( final Mass mass ) {
        // Register a listener for the completion of the removal animation sequence.
        mass.addAnimationStateObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isAnimating, Boolean wasAnimating ) {
                if ( wasAnimating && !isAnimating ) {
                    // Animation sequence has completed.
                    mass.removeAnimationStateObserver( this );
                    massList.remove( mass );
                }
            }
        } );
        // Kick off the animation back to the tool box.
        mass.initiateAnimation();
    }

    /**
     * Removes a mass right away, with no animation.
     *
     * @param mass
     */
    private void removeMass( final Mass mass ) {
        massList.remove( mass );
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

    public List<SupportColumn> getSupportColumns() {
        return supportColumns;
    }

    public void reset() {
        getClock().resetSimulationTime();

        // Remove masses from the plank.
        plank.removeAllMasses();

        // Remove this model's references to the masses.
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            removeMass( mass );
        }

        // Set the support columns to their initial state.
        supportColumnsActive.reset();
    }

    public int getMaximumPossibleScore() {
        System.out.println( getClass().getName() + " - Warning: Max score is faked!" );
        // TODO
        return 100;
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

    public long getTime() {
        return (long) clock.getSimulationTime();
    }

    /**
     * Get the best time for the specified level.  Note that levels start at 1
     * and not 0, so there is some offsetting here.
     *
     * @param level
     * @return
     */
    public long getBestTime( int level ) {
        assert level > 0 && level <= MAX_LEVELS;
        return (long) ( mapLevelToBestTime.containsKey( level ) ? mapLevelToBestTime.get( level ).doubleValue() : Long.MAX_VALUE );
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

    public void newGame() {
        gameStateProperty.set( GameState.OBTAINING_GAME_SETUP );
    }

    public void startGame() {
        gameStateProperty.set( GameState.PRESENTING_INTERACTIVE_CHALLENGE );
    }

    public void checkGuess() {
        // TODO: Always assumed correct for now.
        gameStateProperty.set( GameState.SHOWING_CORRECT_ANSWER_FEEDBACK );
        scoreProperty.set( scoreProperty.get() + 2 );
    }

    // TODO: This is for prototype purposes only, should be removed later.
    public void checkIncorrectGuess() {
        incorrectGuessesOnCurrentChallenge++;
        if ( incorrectGuessesOnCurrentChallenge < 2 ) {
            gameStateProperty.set( GameState.SHOWING_INCORRECT_ANSWER_FEEDBACK_TRY_AGAIN );
        }
        else {
            // Exceeded max guesses.
            gameStateProperty.set( GameState.SHOWING_INCORRECT_ANSWER_FEEDBACK_MOVE_ON );
        }
    }

    public void nextChallenge() {
        challengeCount++;
        incorrectGuessesOnCurrentChallenge = 0;
        if ( challengeCount < PROBLEMS_PER_SET ) {
            gameStateProperty.set( GameState.PRESENTING_INTERACTIVE_CHALLENGE );
        }
        else {
            gameStateProperty.set( GameState.SHOWING_GAME_RESULTS );
        }
    }

    public void tryAgain() {
        gameStateProperty.set( GameState.PRESENTING_INTERACTIVE_CHALLENGE );
    }

    public void displayCorrectAnswer() {
        gameStateProperty.set( GameState.DISPLAYING_CORRECT_ANSWER );
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
