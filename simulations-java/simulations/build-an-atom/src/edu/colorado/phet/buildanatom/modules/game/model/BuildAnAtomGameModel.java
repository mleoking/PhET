package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.GameOverStateView;
import edu.colorado.phet.buildanatom.modules.game.view.GameSettingsStateView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The primary model for the Build an Atom game.  This class sequences the
 * game and sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class BuildAnAtomGameModel {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static final int MAX_LEVELS = 3;
    private static final int PROBLEMS_PER_SET = 5;
    private static final int MAX_POINTS_PER_PROBLEM = 2;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    private final State gameSettingsState = new State( this ){
        @Override
        public StateView createView( GameCanvas gameCanvas ) {
            return new GameSettingsStateView(gameCanvas, BuildAnAtomGameModel.this );
        }
    };
    private final State gameOverState = new State( this ){
        @Override
        public StateView createView( GameCanvas gameCanvas ) {
            return new GameOverStateView(gameCanvas, BuildAnAtomGameModel.this );
        }

        @Override
        public void init() {
            // Stop the game clock.
            stopGame();
        }
    };

    /**
     * Null state, useful for avoiding use of null values.
     */
    private final State nullState = new State( this ){
        @Override
        public StateView createView( GameCanvas gameCanvas ) {
            throw new RuntimeException("Not implemented.");
        }
    };

    private State currentState = nullState;

    private final Property<Integer> scoreProperty = new Property<Integer>( 0 );
    private final Property<Boolean> timerEnabledProperty = new Property<Boolean>( true );
    private final Property<Boolean> soundEnabledProperty = new Property<Boolean>( true );
    private final Property<Integer> levelProperty = new Property<Integer>( 1 );

    // Level pools from the design doc.  These define the pools of problems for a given level.
    private final HashMap<Integer, ArrayList<AtomValue>> levelPools = new HashMap<Integer, ArrayList<AtomValue>>() {{
        put( 1, new ArrayList<AtomValue>() {{
            add( new AtomValue( 1, 0, 0 ) );
            add( new AtomValue( 1, 0, 1 ) );
            add( new AtomValue( 1, 0, 2 ) );
            add( new AtomValue( 2, 2, 2 ) );
            add( new AtomValue( 3, 4, 2 ) );
            add( new AtomValue( 3, 4, 3 ) );
            add( new AtomValue( 4, 5, 4 ) );
            add( new AtomValue( 5, 5, 5 ) );
            add( new AtomValue( 6, 6, 6 ) );
            add( new AtomValue( 7, 7, 7 ) );
            add( new AtomValue( 7, 7, 10 ) );
            add( new AtomValue( 8, 8, 8 ) );
            add( new AtomValue( 8, 8, 10 ) );
            add( new AtomValue( 9, 9, 9 ) );
            add( new AtomValue( 9, 9, 10 ) );
            add( new AtomValue( 10, 10, 10 ) );
        }} );
        put( 2, new ArrayList<AtomValue>() {{
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
            add( new AtomValue( 11, 12, 11 ) );
            add( new AtomValue( 12, 12, 12 ) );
            add( new AtomValue( 13, 14, 13 ) );
            add( new AtomValue( 14, 14, 14 ) );
            add( new AtomValue( 15, 16, 15 ) );
            add( new AtomValue( 16, 16, 16 ) );
            add( new AtomValue( 17, 18, 17 ) );
            add( new AtomValue( 18, 22, 18 ) );
        }} );
        //before these can work, sim will need to support another shell for e-
        put( 3, new ArrayList<AtomValue>() {{
            add( new AtomValue( 1, 0, 0 ) );
            add( new AtomValue( 1, 0, 2 ) );
            add( new AtomValue( 3, 4, 2 ) );
            add( new AtomValue( 7, 7, 10 ) );
            add( new AtomValue( 8, 8, 10 ) );
            add( new AtomValue( 9, 9, 10 ) );
            add( new AtomValue( 11, 12, 10 ) );
            add( new AtomValue( 13, 14, 10 ) );
            add( new AtomValue( 12, 12, 10 ) );
            add( new AtomValue( 16, 16, 18 ) );
            add( new AtomValue( 17, 18, 18 ) );
        }} );
    }};
    private ProblemSet problemSet;
    private final ConstantDtClock clock=new ConstantDtClock( 1000,1000);//simulation time is in milliseconds

    private double bestTime = Double.POSITIVE_INFINITY;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    public BuildAnAtomGameModel() {
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
            oldState.teardown();
            currentState = newState;
            currentState.init();
            for ( GameModelListener listener : listeners ) {
                listener.stateChanged( oldState, currentState );
            }
        }
    }

    public State getState() {
        return currentState;
    }

    public void startGame( int level, boolean timerOn, boolean soundOn ) {
        levelProperty.setValue( level );
        timerEnabledProperty.setValue( timerOn );
        soundEnabledProperty.setValue( soundOn );

        problemSet = new ProblemSet( this, PROBLEMS_PER_SET );
        setState( problemSet.getCurrentProblem() );

        scoreProperty.reset();
        getGameClock().resetSimulationTime();//Start time at zero in case it had time from previous runs
        getGameClock().start();//time starts when the game starts
    }

    public void stopGame() {
        getGameClock().stop();

        // Update the best time value if appropriate.
        if ( timerEnabledProperty.getValue() ){
            if ( getGameClock().getSimulationTime() < bestTime ){
                bestTime = getGameClock().getSimulationTime();
            }
        }
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

    /**
     * Check the user's guess and update the state of the model accordingly.
     * @param guess
     */
    public void processGuess( AtomValue guess ) {
        problemSet.getCurrentProblem().processGuess( guess );
        scoreProperty.setValue( scoreProperty.getValue() + problemSet.getCurrentProblem().getScore() );
    }

    public Property<Integer> getScoreProperty() {
        return scoreProperty;
    }

    public Property<Boolean> getTimerEnabledProperty() {
        return timerEnabledProperty;
    }

    public boolean isTimerEnabled(){
        return timerEnabledProperty.getValue();
    }

    public Property<Integer> getLevelProperty() {
        return levelProperty;
    }

    public Property<Boolean> getSoundEnabledProperty() {
        return soundEnabledProperty;
    }

    public ArrayList<AtomValue> getLevelPool( ) {
        return levelPools.get( levelProperty.getValue() );
    }

    public int getProblemIndex( Problem problem ) {
        return problemSet.getProblemIndex( problem );
    }

    public int getNumberProblems() {
        return problemSet.getTotalNumProblems();
    }

    /**
     * Moves to the next problem or gameover state if no more problems
     */
    public void next() {
        if ( problemSet.isLastProblem() ) {
            setState( getGameOverState() );
        }
        else {
            setState( problemSet.nextProblem() );
        }
    }

    public int getScore() {
        return scoreProperty.getValue();
    }

    public int getMaximumPossibleScore() {
        return MAX_POINTS_PER_PROBLEM * PROBLEMS_PER_SET;
    }

    public ConstantDtClock getGameClock() {
        return clock;
    }

    public long getTime() {
        return (long) clock.getSimulationTime();
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );
    }

    public long getBestTime() {
        return (long) bestTime;
    }

    public boolean isNewBestTime(){
        return getTime() == getBestTime() && timerEnabledProperty.getValue();
    }

    /**
     * Returns true if at least one best time has been recorded, false otherwise.
     * @return
     */
    public boolean isBestTimeRecorded(){
        return bestTime != Double.POSITIVE_INFINITY;
    }
}
