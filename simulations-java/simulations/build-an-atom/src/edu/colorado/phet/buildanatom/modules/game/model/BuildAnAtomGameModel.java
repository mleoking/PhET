// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.GameOverStateView;
import edu.colorado.phet.buildanatom.modules.game.view.GameSettingsStateView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

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

    public static final int MAX_LEVELS = 4;
    private static final int PROBLEMS_PER_SET = 5;
    private static final int MAX_POINTS_PER_PROBLEM = 2;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    private final State gameSettingsState = new State( this ){
        @Override
        public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
            return new GameSettingsStateView(gameCanvas, BuildAnAtomGameModel.this );
        }
    };
    private final State gameOverState = new State( this ){
        @Override
        public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
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
        public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
            throw new RuntimeException("Not implemented.");
        }
    };

    private State currentState = nullState;

    private final Property<Integer> scoreProperty = new Property<Integer>( 0 );
    private final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, BuildAnAtomGameModel.MAX_LEVELS, 1 ), true, true );

    // Level pools from the design doc.  These define the pools of problems for a given level.
    // A pool is the set of atoms that can be selected for creating problem sets at a given game level.
    private final HashMap<Integer, ArrayList<ImmutableAtom>> levelPools = new HashMap<Integer, ArrayList<ImmutableAtom>>() {{
        put( 1, new ArrayList<ImmutableAtom>() {{
            add( new ImmutableAtom( 1, 0, 0 ) );
            add( new ImmutableAtom( 1, 0, 1 ) );
            add( new ImmutableAtom( 1, 0, 2 ) );
            add( new ImmutableAtom( 1, 1, 0 ) );
            add( new ImmutableAtom( 1, 1, 1 ) );
            add( new ImmutableAtom( 1, 1, 2 ) );
            add( new ImmutableAtom( 2, 1, 2 ) );
            add( new ImmutableAtom( 2, 2, 2 ) );
            add( new ImmutableAtom( 3, 3, 2 ) );
            add( new ImmutableAtom( 3, 3, 3 ) );
            add( new ImmutableAtom( 3, 4, 2 ) );
            add( new ImmutableAtom( 3, 4, 3 ) );
            add( new ImmutableAtom( 4, 5, 4 ) );
            add( new ImmutableAtom( 5, 5, 5 ) );
            add( new ImmutableAtom( 5, 6, 5 ) );
            add( new ImmutableAtom( 6, 6, 6 ) );
            add( new ImmutableAtom( 6, 7, 6 ) );
            add( new ImmutableAtom( 7, 7, 7 ) );
            add( new ImmutableAtom( 7, 7, 10 ) );
            add( new ImmutableAtom( 7, 8, 7 ) );
            add( new ImmutableAtom( 7, 8, 10 ) );
            add( new ImmutableAtom( 8, 8, 8 ) );
            add( new ImmutableAtom( 8, 8, 10 ) );
            add( new ImmutableAtom( 8, 9, 8 ) );
            add( new ImmutableAtom( 8, 9, 10 ) );
            add( new ImmutableAtom( 8, 10, 8 ) );
            add( new ImmutableAtom( 8, 10, 10 ) );
            add( new ImmutableAtom( 9, 10, 9 ) );
            add( new ImmutableAtom( 9, 10, 10 ) );
            add( new ImmutableAtom( 10, 10, 10 ) );
            add( new ImmutableAtom( 10, 11, 10 ) );
            add( new ImmutableAtom( 10, 12, 10 ) );
        }} );
        put( 2, new ArrayList<ImmutableAtom>() {{
            add( new ImmutableAtom( 1, 0, 0 ) );
            add( new ImmutableAtom( 1, 0, 1 ) );
            add( new ImmutableAtom( 1, 0, 2 ) );
            add( new ImmutableAtom( 1, 1, 0 ) );
            add( new ImmutableAtom( 1, 1, 1 ) );
            add( new ImmutableAtom( 1, 1, 2 ) );
            add( new ImmutableAtom( 2, 1, 2 ) );
            add( new ImmutableAtom( 2, 2, 2 ) );
            add( new ImmutableAtom( 3, 3, 2 ) );
            add( new ImmutableAtom( 3, 3, 3 ) );
            add( new ImmutableAtom( 3, 4, 2 ) );
            add( new ImmutableAtom( 3, 4, 3 ) );
            add( new ImmutableAtom( 4, 5, 4 ) );
            add( new ImmutableAtom( 5, 5, 5 ) );
            add( new ImmutableAtom( 5, 6, 5 ) );
            add( new ImmutableAtom( 6, 6, 6 ) );
            add( new ImmutableAtom( 6, 7, 6 ) );
            add( new ImmutableAtom( 7, 7, 7 ) );
            add( new ImmutableAtom( 7, 7, 10 ) );
            add( new ImmutableAtom( 7, 8, 7 ) );
            add( new ImmutableAtom( 7, 8, 10 ) );
            add( new ImmutableAtom( 8, 8, 8 ) );
            add( new ImmutableAtom( 8, 8, 10 ) );
            add( new ImmutableAtom( 8, 9, 8 ) );
            add( new ImmutableAtom( 8, 9, 10 ) );
            add( new ImmutableAtom( 8, 10, 8 ) );
            add( new ImmutableAtom( 8, 10, 10 ) );
            add( new ImmutableAtom( 9, 10, 9 ) );
            add( new ImmutableAtom( 9, 10, 10 ) );
            add( new ImmutableAtom( 10, 10, 10 ) );
            add( new ImmutableAtom( 10, 11, 10 ) );
            add( new ImmutableAtom( 10, 12, 10 ) );
        }} );
        put( 3, new ArrayList<ImmutableAtom>() {{
            add( new ImmutableAtom( 1, 0, 0 ) );
            add( new ImmutableAtom( 1, 0, 1 ) );
            add( new ImmutableAtom( 1, 0, 2 ) );
            add( new ImmutableAtom( 1, 1, 0 ) );
            add( new ImmutableAtom( 1, 1, 1 ) );
            add( new ImmutableAtom( 1, 1, 2 ) );
            add( new ImmutableAtom( 2, 1, 2 ) );
            add( new ImmutableAtom( 2, 2, 2 ) );
            add( new ImmutableAtom( 3, 3, 2 ) );
            add( new ImmutableAtom( 3, 3, 3 ) );
            add( new ImmutableAtom( 3, 4, 2 ) );
            add( new ImmutableAtom( 3, 4, 3 ) );
            add( new ImmutableAtom( 4, 5, 4 ) );
            add( new ImmutableAtom( 5, 5, 5 ) );
            add( new ImmutableAtom( 5, 6, 5 ) );
            add( new ImmutableAtom( 6, 6, 6 ) );
            add( new ImmutableAtom( 6, 7, 6 ) );
            add( new ImmutableAtom( 7, 7, 7 ) );
            add( new ImmutableAtom( 7, 7, 10 ) );
            add( new ImmutableAtom( 7, 8, 7 ) );
            add( new ImmutableAtom( 7, 8, 10 ) );
            add( new ImmutableAtom( 8, 8, 8 ) );
            add( new ImmutableAtom( 8, 8, 10 ) );
            add( new ImmutableAtom( 8, 9, 8 ) );
            add( new ImmutableAtom( 8, 9, 10 ) );
            add( new ImmutableAtom( 8, 10, 8 ) );
            add( new ImmutableAtom( 8, 10, 10 ) );
            add( new ImmutableAtom( 9, 10, 9 ) );
            add( new ImmutableAtom( 9, 10, 10 ) );
            add( new ImmutableAtom( 10, 10, 10 ) );
            add( new ImmutableAtom( 10, 11, 10 ) );
            add( new ImmutableAtom( 10, 12, 10 ) );
            add( new ImmutableAtom( 11, 12, 10 ) );
            add( new ImmutableAtom( 11, 12, 11 ) );
            add( new ImmutableAtom( 12, 12, 10 ) );
            add( new ImmutableAtom( 12, 12, 12 ) );
            add( new ImmutableAtom( 12, 13, 10 ) );
            add( new ImmutableAtom( 12, 13, 12 ) );
            add( new ImmutableAtom( 12, 14, 10 ) );
            add( new ImmutableAtom( 12, 14, 12 ) );
            add( new ImmutableAtom( 13, 14, 10 ) );
            add( new ImmutableAtom( 13, 14, 13 ) );
            add( new ImmutableAtom( 14, 14, 14 ) );
            add( new ImmutableAtom( 14, 15, 14 ) );
            add( new ImmutableAtom( 14, 16, 14 ) );
            add( new ImmutableAtom( 15, 16, 15 ) );
            add( new ImmutableAtom( 16, 16, 16 ) );
            add( new ImmutableAtom( 16, 16, 18 ) );
            add( new ImmutableAtom( 16, 17, 16 ) );
            add( new ImmutableAtom( 16, 17, 18 ) );
            add( new ImmutableAtom( 16, 18, 16 ) );
            add( new ImmutableAtom( 16, 18, 18 ) );
            add( new ImmutableAtom( 16, 19, 16 ) );
            add( new ImmutableAtom( 16, 19, 18 ) );
            add( new ImmutableAtom( 17, 18, 17 ) );
            add( new ImmutableAtom( 17, 18, 18 ) );
            add( new ImmutableAtom( 17, 20, 17 ) );
            add( new ImmutableAtom( 17, 20, 18 ) );
            add( new ImmutableAtom( 18, 20, 18 ) );
            add( new ImmutableAtom( 18, 22, 18 ) );
        }} );
        put( 4, new ArrayList<ImmutableAtom>() {{
            add( new ImmutableAtom( 1, 0, 0 ) );
            add( new ImmutableAtom( 1, 0, 1 ) );
            add( new ImmutableAtom( 1, 0, 2 ) );
            add( new ImmutableAtom( 1, 1, 0 ) );
            add( new ImmutableAtom( 1, 1, 1 ) );
            add( new ImmutableAtom( 1, 1, 2 ) );
            add( new ImmutableAtom( 2, 1, 2 ) );
            add( new ImmutableAtom( 2, 2, 2 ) );
            add( new ImmutableAtom( 3, 3, 2 ) );
            add( new ImmutableAtom( 3, 3, 3 ) );
            add( new ImmutableAtom( 3, 4, 2 ) );
            add( new ImmutableAtom( 3, 4, 3 ) );
            add( new ImmutableAtom( 4, 5, 4 ) );
            add( new ImmutableAtom( 5, 5, 5 ) );
            add( new ImmutableAtom( 5, 6, 5 ) );
            add( new ImmutableAtom( 6, 6, 6 ) );
            add( new ImmutableAtom( 6, 7, 6 ) );
            add( new ImmutableAtom( 7, 7, 7 ) );
            add( new ImmutableAtom( 7, 7, 10 ) );
            add( new ImmutableAtom( 7, 8, 7 ) );
            add( new ImmutableAtom( 7, 8, 10 ) );
            add( new ImmutableAtom( 8, 8, 8 ) );
            add( new ImmutableAtom( 8, 8, 10 ) );
            add( new ImmutableAtom( 8, 9, 8 ) );
            add( new ImmutableAtom( 8, 9, 10 ) );
            add( new ImmutableAtom( 8, 10, 8 ) );
            add( new ImmutableAtom( 8, 10, 10 ) );
            add( new ImmutableAtom( 9, 10, 9 ) );
            add( new ImmutableAtom( 9, 10, 10 ) );
            add( new ImmutableAtom( 10, 10, 10 ) );
            add( new ImmutableAtom( 10, 11, 10 ) );
            add( new ImmutableAtom( 10, 12, 10 ) );
            add( new ImmutableAtom( 11, 12, 10 ) );
            add( new ImmutableAtom( 11, 12, 11 ) );
            add( new ImmutableAtom( 12, 12, 10 ) );
            add( new ImmutableAtom( 12, 12, 12 ) );
            add( new ImmutableAtom( 12, 13, 10 ) );
            add( new ImmutableAtom( 12, 13, 12 ) );
            add( new ImmutableAtom( 12, 14, 10 ) );
            add( new ImmutableAtom( 12, 14, 12 ) );
            add( new ImmutableAtom( 13, 14, 10 ) );
            add( new ImmutableAtom( 13, 14, 13 ) );
            add( new ImmutableAtom( 14, 14, 14 ) );
            add( new ImmutableAtom( 14, 15, 14 ) );
            add( new ImmutableAtom( 14, 16, 14 ) );
            add( new ImmutableAtom( 15, 16, 15 ) );
            add( new ImmutableAtom( 16, 16, 16 ) );
            add( new ImmutableAtom( 16, 16, 18 ) );
            add( new ImmutableAtom( 16, 17, 16 ) );
            add( new ImmutableAtom( 16, 17, 18 ) );
            add( new ImmutableAtom( 16, 18, 16 ) );
            add( new ImmutableAtom( 16, 18, 18 ) );
            add( new ImmutableAtom( 16, 19, 16 ) );
            add( new ImmutableAtom( 16, 19, 18 ) );
            add( new ImmutableAtom( 17, 18, 17 ) );
            add( new ImmutableAtom( 17, 18, 18 ) );
            add( new ImmutableAtom( 17, 20, 17 ) );
            add( new ImmutableAtom( 17, 20, 18 ) );
            add( new ImmutableAtom( 18, 20, 18 ) );
            add( new ImmutableAtom( 18, 22, 18 ) );
        }} );
    }};
    private ProblemSet problemSet;
    private final ConstantDtClock clock = new ConstantDtClock( 1000, 1000 );//simulation time is in milliseconds

    // Track the best times on a per-level basis.
    private final HashMap<Integer, Double> mapLevelToBestTime = new HashMap<Integer, Double>();

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

    public void startGame() {

        problemSet = new ProblemSet( this, PROBLEMS_PER_SET );
        if ( problemSet.getTotalNumProblems() > 0 ){
            setState( problemSet.getCurrentProblem() );
        }
        else{
            // No problems generated, go directly to Game Over state.
            setState( gameOverState );
        }

        scoreProperty.reset();
        getGameClock().resetSimulationTime();//Start time at zero in case it had time from previous runs
        getGameClock().start();//time starts when the game starts
    }

    public void stopGame() {
        getGameClock().stop();

        // Update the best time value if appropriate.
        if ( isTimerEnabled() ){
            if ( !mapLevelToBestTime.containsKey( getLevel() ) || getGameClock().getSimulationTime() < mapLevelToBestTime.get( getLevel() ) ){
                mapLevelToBestTime.put( getLevel(), getGameClock().getSimulationTime() );
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
    public void processGuess( ImmutableAtom guess ) {
        problemSet.getCurrentProblem().processGuess( guess );
        scoreProperty.set( scoreProperty.get() + problemSet.getCurrentProblem().getScore() );
    }

    public Property<Integer> getScoreProperty() {
        return scoreProperty;
    }

    /**
     * Get the pool of possible problems for the current level setting.
     */
    public ArrayList<ImmutableAtom> getLevelPool( ) {
        return levelPools.get( getLevel() );
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
        return scoreProperty.get();
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

    /**
     * Get the best time for the specified level.  Note that levels start at 1
     * and not 0, so there is some offsetting here.
     *
     * @param level
     * @return
     */
    public long getBestTime( int level ) {
        assert level > 0 && level <= MAX_LEVELS;
        return (long) (mapLevelToBestTime.containsKey( level ) ? mapLevelToBestTime.get( level ).doubleValue() : Long.MAX_VALUE );
    }

    public boolean isNewBestTime(){
        return getTime() == getBestTime( getLevel() ) && isTimerEnabled();
    }

    /**
     * Returns true if at least one best time has been recorded for the
     * specified level, false otherwise.  Note that levels start at 1 and not
     * 0.
     *
     * @return
     */
    public boolean isBestTimeRecorded( int level ){
        return mapLevelToBestTime.containsKey( level );
    }
}
