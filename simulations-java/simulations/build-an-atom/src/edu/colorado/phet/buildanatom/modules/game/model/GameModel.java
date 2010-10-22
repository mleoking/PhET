package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * The primary model for the Build an Atom game.  This class sequences the
 * game and sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameModel {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static final int MAX_LEVELS = 3;
    public static final int MAX_SCORE = 5;
    private static final int PROBLEMS_PER_GAME = 3;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    private final State gameSettingsState = new State( this );
    private final State gameOverState = new State( this );

    // Level pools from the design doc
    private final HashMap<Integer, ArrayList<AtomValue>> levels = new HashMap<Integer, ArrayList<AtomValue>>() {{
        put( 1, new ArrayList<AtomValue>() {{
            add( new AtomValue( 1, 0, 1 ) );
            add( new AtomValue( 2, 2, 2 ) );
            add( new AtomValue( 3, 4, 3 ) );
            add( new AtomValue( 4, 5, 4 ) );
            add( new AtomValue( 5, 5, 5 ) );
            add( new AtomValue( 6, 6, 6 ) );
            add( new AtomValue( 7, 7, 7 ) );
            add( new AtomValue( 8, 8, 8 ) );
            add( new AtomValue( 9, 9, 9 ) );
            add( new AtomValue( 10, 10, 10 ) );
        }} );
        put( 2, new ArrayList<AtomValue>() {{
            add( new AtomValue( 1, 0, 0 ) );
            add( new AtomValue( 1, 0, 2 ) );
            add( new AtomValue( 3, 4, 2 ) );
            add( new AtomValue( 7, 7, 10 ) );
            add( new AtomValue( 8, 8, 10 ) );
            add( new AtomValue( 9, 9, 10 ) );
        }} );
        //before these can work, sim will need to support another shell for e-
//        put( 3, new ArrayList<AtomValue>() {{
//            add( new AtomValue( 11, 12, 11 ) );
//            add( new AtomValue( 11, 12, 10 ) );
//            add( new AtomValue( 12, 12, 12 ) );
//            add( new AtomValue( 12, 12, 10 ) );
//            add( new AtomValue( 14, 14, 14 ) );
//            add( new AtomValue( 15, 16, 15 ) );
//            add( new AtomValue( 16, 16, 16 ) );
//            add( new AtomValue( 16, 16, 18 ) );
//            add( new AtomValue( 17, 18, 17 ) );
//            add( new AtomValue( 17, 18, 18 ) );
//            add( new AtomValue( 18, 22, 18 ) );
//        }} );

    }};
    private final Random random = new Random();


    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    public GameModel() {
        setState( gameSettingsState );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

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
        System.out.println( "level = " + level );
        ProblemSet problemSet = new ProblemSet( this, level, timerOn, soundOn );
        // TODO: We need to make sure that the same problem is not generated
        // twice.
        ArrayList<AtomValue> pool = levels.get( level );
        for ( int i = 0; i < PROBLEMS_PER_GAME; i++ ) {
            AtomValue atomValue = pool.get( random.nextInt( pool.size() ) );
//            final int problemType = random.nextInt( 3 );
            final int problemType = 2; // TODO: Temporary to get one type of problem working.
            if ( problemType == 0 ) {
                problemSet.addProblem( new CompleteTheModelProblem( this, problemSet, atomValue ) );
            }
            else if ( problemType == 1 ) {
                problemSet.addProblem( new CompleteTheSymbolProblem( this, problemSet, atomValue ) );
            }
            else if ( problemType == 2 ) {
                problemSet.addProblem( new HowManyParticlesProblem( this, problemSet, atomValue ) );
            }
        }
        for ( GameModelListener listener : listeners ) {
            listener.problemSetCreated( problemSet );
        }
        setState( problemSet.getProblem( 0 ) );
    }

    public void addListener( GameModelListener listener ) {
        listeners.add( listener );
    }

    public void newGame() {
        setState( gameSettingsState );
    }

    public State getGameOverState() {
        return gameOverState;
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );

        void problemSetCreated( ProblemSet problemSet );
    }

}
