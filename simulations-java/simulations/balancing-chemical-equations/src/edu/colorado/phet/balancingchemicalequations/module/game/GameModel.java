// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.view.BalancedRepresentation;
import edu.colorado.phet.balancingchemicalequations.view.game.IBalancedRepresentationStrategy;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class GameModel {

    /**
     * The set of game states.
     * For lack of better names, the state names correspond to the main action that
     * the user can take in that state.  For example. the CHECK state is where the user
     * can enter coefficients and press the "Check" button to check their answer.
     */
    public static enum GameState { START_GAME, CHECK, TRY_AGAIN, SHOW_ANSWER, NEXT, NEW_GAME };

    /*
     * Strategies for selecting the "balanced representation" that is displayed by the "Not Balanced" popup.
     * This is a map from level to strategy.
     */
    private static HashMap<Integer,IBalancedRepresentationStrategy> BALANCED_REPRESENTATION_STRATEGIES = new HashMap<Integer,IBalancedRepresentationStrategy>() {{
        put( 1, new IBalancedRepresentationStrategy.Constant( BalancedRepresentation.BALANCE_SCALES ) );
        put( 2, new IBalancedRepresentationStrategy.Random() );
        put( 3, new IBalancedRepresentationStrategy.Constant( BalancedRepresentation.BAR_CHARTS ) );
    }};

    private static final IntegerRange COEFFICENTS_RANGE = new IntegerRange( 0, 7 ); // range for equation coefficients
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

    private final GameFactory equationsFactory; // generates problem sets
    private final HashMap<Integer,Long> bestTimes; // best times, maps level to time in ms

    private ArrayList<Equation> equations; // the current set of equations to be balanced
    private int currentEquationIndex; // index of the current equation that the user is working on
    private int attempts; // how many attempts the user has made at solving the current challenge
    private boolean isNewBestTime; // is the time for this game a new best time?
    private int currentPoints; // how many points were earned for the current equation
    private BalancedRepresentation balancedRepresentation; // which representation to use in the "Not Balanced" popup

    /**
     * Constructor
     * @param globalProperties global properties, many of which are accessed via the menu bar
     */
    public GameModel( final BCEGlobalProperties globalProperties ) {
        state = new Property<GameState>( GameState.START_GAME );
        points = new Property<Integer>( 0 );
        equationsFactory = new GameFactory( globalProperties.playAllEquations );
        settings = new GameSettings( LEVELS_RANGE, true /* sound */, true /* timer */ );
        bestTimes = new HashMap<Integer,Long>();
        for ( int i = settings.level.getMin(); i <= settings.level.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }
        timer = new GameTimer( new BCEClock() );
        equations = equationsFactory.createEquations( EQUATIONS_PER_GAME, settings.level.get() ); // needs to be non-null after initialization
        currentEquationIndex = 0;
        currentEquation = new Property<Equation>( equations.get( currentEquationIndex ) );
    }

    /**
     * Called when the user presses the "Start Game" button.
     */
    public void startGame() {
        equations = equationsFactory.createEquations( EQUATIONS_PER_GAME, settings.level.get() );
        currentEquationIndex = 0;
        balancedRepresentation = BALANCED_REPRESENTATION_STRATEGIES.get( settings.level.get() ).getBalancedRepresentation();
        attempts = 0;
        isNewBestTime = false;
        timer.start();
        currentPoints = 0;
        points.set( 0 );
        currentEquation.set( equations.get( currentEquationIndex ) );
        state.set( GameState.CHECK );
    }

    /**
     * Called when the user presses the "Check" button.
     */
    public void check() {
        attempts++;
        if ( currentEquation.get().isBalancedAndSimplified() ) {

            // award points
            if ( attempts == 1 ) {
                currentPoints = POINTS_FIRST_ATTEMPT;

            }
            else if ( attempts == 2 ) {
                currentPoints = POINTS_SECOND_ATTEMPT;
            }
            else {
                currentPoints = 0;
            }
            points.set( points.get() + currentPoints );

            // end the game
            if ( currentEquationIndex == equations.size() - 1 ) {
                timer.stop();
                // check for new best time
                long previousBestTime = getBestTime( settings.level.get() );
                if ( isPerfectScore() && ( previousBestTime == 0 || timer.time.get() < previousBestTime ) ) {
                    isNewBestTime = true;
                    bestTimes.put( settings.level.get(), timer.time.get() );
                }
            }

            state.set( GameState.NEXT );
        }
        else if ( attempts < 2 ) {
            state.set( GameState.TRY_AGAIN );
        }
        else {
            state.set( GameState.SHOW_ANSWER );
        }
    }

    /**
     * Called when the user presses the "Try Again" button.
     */
    public void tryAgain() {
        state.set( GameState.CHECK );
    }

    /**
     * Called when the user presses the "Show Answer" button.
     */
    public void showAnswer() {
        state.set( GameState.NEXT );
    }

    /**
     * Called when the user presses the "Next" button.
     */
    public void next() {
        if ( currentEquationIndex < equations.size() - 1 ) {
            attempts = 0;
            currentPoints = 0;
            balancedRepresentation = BALANCED_REPRESENTATION_STRATEGIES.get( settings.level.get() ).getBalancedRepresentation();
            currentEquationIndex++;
            currentEquation.set( equations.get( currentEquationIndex ) );
            state.set( GameState.CHECK );
        }
        else {
            state.set( GameState.NEW_GAME );
        }
    }

    /**
     * Called when the user presses the "New Game" button.
     */
    public void newGame() {
        state.set( GameState.START_GAME );
    }

    public boolean isNewBestTime() {
        return isNewBestTime;
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

    /**
     * Gets the number of points earned for the current equation.
     * @return
     */
    public int getCurrentPoints() {
        return currentPoints;
    }

    /**
     * Gets the range of the user coefficients.
     * @return
     */
    public IntegerRange getCoefficientsRange() {
        return COEFFICENTS_RANGE;
    }

    /**
     * Gets the current equation, which is the equation that is being played.
     * @return
     */
    public int getCurrentEquationIndex() {
        return currentEquationIndex;
    }

    /**
     * Gets the number of equations in the current game.
     * @return
     */
    public int getNumberOfEquations() {
        return equations.size();
    }

    /**
     * Gets the number of points in a perfect score, which occurs when the user
     * balances every equation in the game correctly on the first attempt.
     * @return
     */
    public int getPerfectScore() {
        return getNumberOfEquations() * POINTS_FIRST_ATTEMPT;
    }

    /**
     * Is the current score a perfect score?
     * This can be called at any time during the game, but can't possibly
     * return true until the game has been completed.
     * @return
     */
    public boolean isPerfectScore() {
        return points.get() == getPerfectScore();
    }

    /**
     * Gets the "balanced" representation that corresponds to the current equation.
     * This representation determines what is displayed by the "Not Balanced" popup.
     * @return
     */
    public BalancedRepresentation getBalancedRepresentation() {
        return balancedRepresentation;
    }
}
