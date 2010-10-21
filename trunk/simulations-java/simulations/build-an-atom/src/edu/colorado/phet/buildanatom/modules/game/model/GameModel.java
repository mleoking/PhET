package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;

/**
 * The primary model for the Build an Atom game.  This sequences the game and
 * sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameModel {
    public static final int MAX_LEVELS = 3;
    public static final int MAX_SCORE = 5;

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    private final State gameSettingsState = new State( this );
    private final State gameOverState = new State( this );

    public GameModel() {
        setState( gameSettingsState );
    }

    public State getGameSettingsState() {
        return gameSettingsState;
    }

    public void setState( State newState ) {
        if ( currentState != newState ) {
            State oldState = currentState;
            currentState = newState;
            for ( GameModelListener listener : listeners ) {
                listener.stateChanged( oldState, currentState );
            }
        }
    }

    public State getState() {
        return currentState;
    }

    public void startGame( int level, boolean timerOn, boolean soundOn ) {
        ProblemSet problemSet = new ProblemSet( this, level, timerOn, soundOn );
        for ( GameModelListener listener : listeners ) {
            listener.problemSetCreated( problemSet );
        }
        setState( problemSet.getProblem( 0 ) );
    }

    public void addListener( GameModelListener listener ) {
        listeners.add( listener );
    }

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );

        void problemSetCreated( ProblemSet problemSet );
    }

    public void newGame() {
        setState( gameSettingsState );
    }

    public State getGameOverState() {
        return gameOverState;
    }

}
