// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.util.HashMap;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.GameEquationsFactory;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel {

    /**
     * The set of game states.
     * For lack of better names, the state names correspond to the main action that
     * the user can take in that state.  For example. the CHECK state is where the user
     * can enter coefficients and press the "Check" button to check their answer.
     */
    public enum GameState { START_GAME, CHECK, TRY_AGAIN, SHOW_ANSWER, NEXT, NEW_GAME };

    private static final IntegerRange COEFFICENTS_RANGE = new IntegerRange( 0, 7 );
    private static final IntegerRange LEVELS_RANGE = new IntegerRange( 1, 3, 1 );
    private static final int EQUATIONS_PER_GAME = 5;
    private static final int POINTS_FIRST_ATTEMPT = 2;  // points to award for correct guess on 1st attempt
    private static final int POINTS_SECOND_ATTEMPT = 1; // points to award for correct guess on 2nd attempt

    // properties directly accessible by clients
    public final Property<Integer> points; // how many points the user has earned for the current game
    public final Property<GameState> state;
    public final Property<Equation> currentEquation;
    public final GameSettings settings;
    public final GameTimer timer;

    private final GameEquationsFactory equationsFactory; // generates problem sets
    private final HashMap<Integer,Long> bestTimes; // best times, maps level to time in ms

    private Equation[] equations; // the current set of equations to be balanced
    private int equationIndex; // index of the equation that the user is working on
    private int attempts; // how many attempts the user has made at solving the current challenge
    private boolean isNewBestTime; // is the time for this game a new best time?
    private boolean isGameCompleted; // was the game played to completion?

    public GameModel( final BCEGlobalProperties globalProperties ) {
        state = new Property<GameState>( GameState.START_GAME );
        points = new Property<Integer>( 0 );
        equationsFactory = new GameEquationsFactory( globalProperties.playAllEquations );
        settings = new GameSettings( LEVELS_RANGE, true /* sound */, true /* timer */ );
        bestTimes = new HashMap<Integer,Long>();
        for ( int i = settings.level.getMin(); i <= settings.level.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }
        timer = new GameTimer( new BCEClock() );
        equations = equationsFactory.createEquations( EQUATIONS_PER_GAME, settings.level.getValue() ); // needs to be non-null after initialization
        equationIndex = 0;
        currentEquation = new Property<Equation>( equations[equationIndex] );
    }

    /**
     * Called when the user presses the "Start Game" button.
     */
    public void startGame() {
        equations = equationsFactory.createEquations( EQUATIONS_PER_GAME, settings.level.getValue() );
        equationIndex = 0;
        attempts = 0;
        isNewBestTime = false;
        isGameCompleted = false;
        timer.start();
        points.setValue( 0 );
        setCurrentEquation( equations[equationIndex] );
        state.setValue( GameState.CHECK );
    }

    /**
     * Called when the user presses the "Check" button.
     */
    public void check() {
        attempts++;
        if ( equations[equationIndex].isBalancedAndSimplified() ) {

            // award points
            if ( attempts == 1 ) {
                points.setValue( points.getValue() + POINTS_FIRST_ATTEMPT );
            }
            else if ( attempts == 2 ) {
                points.setValue( points.getValue() + POINTS_SECOND_ATTEMPT );
            }

            // end the game
            if ( equationIndex == equations.length - 1 ) {
                timer.stop();
                isGameCompleted = true;
                // check for new best time
                long previousBestTime = getBestTime( settings.level.getValue() );
                if ( isPerfectScore() && ( previousBestTime == 0 || timer.time.getValue() < previousBestTime ) ) {
                    isNewBestTime = true;
                    setBestTime( settings.level.getValue(), timer.time.getValue() );
                }
            }

            state.setValue( GameState.NEXT );
        }
        else if ( attempts < 2 ) {
            state.setValue( GameState.TRY_AGAIN );
        }
        else {
            state.setValue( GameState.SHOW_ANSWER );
        }
    }

    /**
     * Called when the user presses the "Try Again" button.
     */
    public void tryAgain() {
        state.setValue( GameState.CHECK );
    }

    /**
     * Called when the user presses the "Show Answer" button.
     */
    public void showAnswer() {
        state.setValue( GameState.NEXT );
    }

    /**
     * Called when the user presses the "Next" button.
     */
    public void next() {
        if ( equationIndex < equations.length - 1 ) {
            attempts = 0;
            equationIndex++;
            setCurrentEquation( equations[equationIndex] );
            state.setValue( GameState.CHECK );
        }
        else {
            state.setValue( GameState.NEW_GAME );
        }
    }

    /**
     * Called when the user presses the "New Game" button.
     */
    public void newGame() {
        state.setValue( GameState.START_GAME );
    }

    private void setCurrentEquation( Equation equation ) {
        currentEquation.setValue( equation );
    }

    public Equation getCurrentEquation() {
        return currentEquation.getValue();
    }

    public Property<Equation> getCurrentEquationProperty() {
        return currentEquation;
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
        return getNumberOfEquations() * POINTS_FIRST_ATTEMPT;
    }

    public boolean isPerfectScore() {
        return points.getValue() == getMaxScore();
    }
}
