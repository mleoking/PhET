package edu.colorado.phet.buildanatom.modules.game;

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
    public static final int PROBLEMS_PER_GAME = 3;

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    final GameSettingsState gameSettingsState = new GameSettingsState( this );
    private final Problem playingGameState = new Problem( this );
    private final Problem level2 = new Problem( this );
    private final GameOver gameOverState = new GameOver( this );
    private int problemIndex = 0;

    public GameModel() {
        setState( gameSettingsState );
    }

    public Problem getLevel2() {
        return level2;
    }

    public GameSettingsState getGameSettingsState() {
        return gameSettingsState;
    }

    public Problem getPlayingGameState() {
        return playingGameState;
    }

    void setState( State newState ) {
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

    public void startGame() {
        setState( playingGameState );
    }

    public void addListener( GameModelListener listener ) {
        listeners.add( listener );
    }

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );
    }

    public abstract static class State {
        protected final GameModel model;

        public State( GameModel model ) {
            this.model = model;
        }
    }

    public static class GameSettingsState extends State {
        public GameSettingsState( GameModel model ) {
            super( model );
        }
    }

    public static class Problem extends State {
        public Problem( GameModel model ) {
            super( model );
        }

        public void checkGuess() {
            model.problemIndex++;
            if ( model.problemIndex < PROBLEMS_PER_GAME ) {
                nextProblem();
            }
            else {
                model.setState( model.gameOverState );
                model.problemIndex = 0;
            }
        }

        private void nextProblem() {
            // TODO Auto-generated method stub
            System.err.println( getClass().getName() + "Would move to next challenge now." );
        }

    }

    public static class GameOver extends State {
        public GameOver( GameModel model ) {
            super( model );
        }
    }

    public void newGame() {
        setState( gameSettingsState );
    }

    public State getGameOverState() {
        return gameOverState;
    }
}
