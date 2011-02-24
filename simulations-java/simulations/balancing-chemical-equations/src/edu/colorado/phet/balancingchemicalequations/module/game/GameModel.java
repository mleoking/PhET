// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.util.HashMap;

import edu.colorado.phet.balancingchemicalequations.model.BCEClock;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.GameEquationsFactory;
import edu.colorado.phet.balancingchemicalequations.model.OneProductEquation.WaterEquation;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel {

    /** The set of prompts that the user sees during the game. */
    public enum GamePrompt { START_GAME, CHECK, TRY_AGAIN, SHOW_ANSWER, NEXT, NEW_GAME };

    private static final IntegerRange COEFFICENTS_RANGE = new IntegerRange( 0, 7 );
    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 3, 1 );
    private static final int PROBLEMS_PER_GAME = 5;
    private static final int POINTS_FIRST_ATTEMPT = 2;  // points to award for correct guess on 1st attempt
    private static final int POINTS_SECOND_ATTEMPT = 1; // points to award for correct guess on 2nd attempt

    private final Property<Integer> pointsProperty; // how many points the user has earned for the current game
    private final Property<GamePrompt> gamePromptProperty;
    private final Property<Equation> currentEquationProperty;

    private final GameEquationsFactory problemsFactory; // generates problem sets
    private final GameSettings gameSettings;
    private final HashMap<Integer,Long> bestTimes; // best times, maps level to time in ms
    private final GameTimer timer;

    private Equation[] equations; // the current set of equations to be balanced
    private int equationIndex; // index of the equation that the user is working on
    private int attempts; // how many attempts the user has made at solving the current challenge
    private boolean isNewBestTime; // is the time for this game a new best time?
    private boolean isGameCompleted; // was the game played to completion?

    public GameModel() {
        gamePromptProperty = new Property<GamePrompt>( GamePrompt.START_GAME );
        pointsProperty = new Property<Integer>( 0 );
        currentEquationProperty = new Property<Equation>( new WaterEquation() );
        problemsFactory = new GameEquationsFactory();
        gameSettings = new GameSettings( LEVELS_RANGE, true /* sound */, true /* timer */ );
        bestTimes = new HashMap<Integer,Long>();
        for ( int i = gameSettings.level.getMin(); i <= gameSettings.level.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }
        timer = new GameTimer( new BCEClock() );
        equations = problemsFactory.createProblemSet( PROBLEMS_PER_GAME, gameSettings.level.getValue() ); // needs to be non-null after initialization
        equationIndex = 0;
    }

    public long getTime() {
        return timer.getTime();
    }

    public void addTimeObserver( SimpleObserver o ) {
        timer.addTimeObserver( o );
    }

    public void removeTimeObserver( SimpleObserver o ) {
        timer.removeTimeObserver( o );
    }

    private void setGamePrompt( GamePrompt value ) {
        gamePromptProperty.setValue( value );
    }

    public GamePrompt getGamePrompt() {
        return gamePromptProperty.getValue();
    }

    public void addGamePromptObserver( SimpleObserver o ) {
        gamePromptProperty.addObserver( o );
    }

    private void setPoints( int points ) {
        pointsProperty.setValue( points );
    }

    public int getPoints() {
        return pointsProperty.getValue();
    }

    public void addPointsObserver( SimpleObserver o ) {
        pointsProperty.addObserver( o );
    }

    public void removePointsObserver( SimpleObserver o ) {
        pointsProperty.removeObserver( o );
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Called when the user presses the "Start Game" button.
     */
    public void startGame() {
        equations = problemsFactory.createProblemSet( PROBLEMS_PER_GAME, gameSettings.level.getValue() );
        equationIndex = 0;
        attempts = 0;
        isNewBestTime = false;
        isGameCompleted = false;
        timer.start();
        setPoints( 0 );
        setCurrentEquation( equations[equationIndex] );
        setGamePrompt( GamePrompt.CHECK );
    }

    /**
     * Called when the user presses the "Check" button.
     */
    public void check() {
        attempts++;
        if ( equations[equationIndex].isBalancedWithLowestCoefficients() ) {

            // award points
            if ( attempts == 1 ) {
                pointsProperty.setValue( getPoints() + POINTS_FIRST_ATTEMPT );
            }
            else if ( attempts == 2 ) {
                pointsProperty.setValue( getPoints() + POINTS_SECOND_ATTEMPT );
            }

            // end the game
            if ( equationIndex == equations.length - 1 ) {
                timer.stop();
                isGameCompleted = true;
                // check for new best time
                long previousBestTime = getBestTime( gameSettings.level.getValue() );
                if ( isPerfectScore() && ( previousBestTime == 0 || getTime() < previousBestTime ) ) {
                    isNewBestTime = true;
                    setBestTime( gameSettings.level.getValue(), getTime() );
                }
            }

            setGamePrompt( GamePrompt.NEXT );
        }
        else if ( attempts < 2 ) {
            setGamePrompt( GamePrompt.TRY_AGAIN );
        }
        else {
            setGamePrompt( GamePrompt.SHOW_ANSWER );
        }
    }

    /**
     * Called when the user presses the "Try Again" button.
     */
    public void tryAgain() {
        setGamePrompt( GamePrompt.CHECK );
    }

    /**
     * Called when the user presses the "Show Answer" button.
     */
    public void showAnswer() {
        setGamePrompt( GamePrompt.NEXT );
    }

    /**
     * Called when the user presses the "Next" button.
     */
    public void next() {
        if ( equationIndex < equations.length - 1 ) {
            attempts = 0;
            equationIndex++;
            setCurrentEquation( equations[equationIndex] );
            setGamePrompt( GamePrompt.CHECK );
        }
        else {
            setGamePrompt( GamePrompt.NEW_GAME );
        }
    }

    /**
     * Called when the user presses the "New Game" button.
     */
    public void newGame() {
        setGamePrompt( GamePrompt.START_GAME );
    }

    private void setCurrentEquation( Equation equation ) {
        currentEquationProperty.setValue( equation );
    }

    public Equation getCurrentEquation() {
        return currentEquationProperty.getValue();
    }

    public Property<Equation> getCurrentEquationProperty() {
        return currentEquationProperty;
    }

    public boolean isNewBestTime() {
        return isNewBestTime;
    }

    public boolean isGameCompleted() {
        return isGameCompleted;
    }

    /**
     * Gets the best time for a specified level.
     * If this returns zero, then there is no best time for the level.
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

    private void setBestTime( int level, long time ) {
        bestTimes.put( level, time );
    }

    public IntegerRange getCoefficientsRange() {
        return COEFFICENTS_RANGE;
    }

    public int getEquationIndex() {
        return equationIndex;
    }

    public int getNumberOfEquations() {
        return equations.length;
    }

    public int getMaxScore() {
        return PROBLEMS_PER_GAME * POINTS_FIRST_ATTEMPT;
    }

    private boolean isPerfectScore() {
        return getPoints() == getMaxScore();
    }
}
