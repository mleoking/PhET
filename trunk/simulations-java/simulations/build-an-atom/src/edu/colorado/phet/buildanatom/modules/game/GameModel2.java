package edu.colorado.phet.buildanatom.modules.game;

import java.util.ArrayList;

/**
 * The primary model for the Build an Atom game.  This sequences the game and
 * sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameModel2 {
    public static final int MAX_LEVELS = 3;
    public static final int MAX_SCORE = 5;

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    final GameSettingsState gameSettingsState = new GameSettingsState( this );
    private PlayingGame playingGameState = new PlayingGame( this );
    private PlayingGame level2 = new PlayingGame( this );
    private GameOver gameOver= new GameOver( this );

    public GameModel2() {
        setState( gameSettingsState );
    }

    public PlayingGame getLevel2() {
        return level2;
    }

    public GameSettingsState getGameSettingsState() {
        return gameSettingsState;
    }

    public PlayingGame getPlayingGameState() {
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

    public void checkGuess() {
        currentState.checkGuess();
    }

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );
    }

    public abstract static class State {
        protected final GameModel2 model;

        public State( GameModel2 model ) {
            this.model = model;
        }

        public abstract void checkGuess();
    }

    public static class GameSettingsState extends State {
        public GameSettingsState( GameModel2 model ) {
            super( model );
        }

        @Override
        public void checkGuess() {
        }
    }

    public static class PlayingGame extends State {
        public PlayingGame( GameModel2 model ) {
            super( model );
        }

        @Override
        public void checkGuess() {
        }
    }

    public static class GameOver extends State {
        public GameOver( GameModel2 model ) {
            super( model );
        }

        @Override
        public void checkGuess() {
        }
    }

    public void newGame() {
        setState( gameSettingsState );
    }
}
