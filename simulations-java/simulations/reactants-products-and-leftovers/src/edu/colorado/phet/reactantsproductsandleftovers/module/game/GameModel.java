/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.*;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel extends RPALModel {
    
    private static final boolean DEBUG_OUTPUT_ENABLED = false;
    
    private static final int CHALLENGES_PER_GAME = 10;
    private static final IntegerRange LEVEL_RANGE = new IntegerRange( 1, 3, 1 ); // difficulty level
    private static final boolean DEFAULT_TIMER_ENABLED = true;
    private static final double POINTS_FIRST_ATTEMPT = 1;  // points to award for correct guess on 1st attempt
    private static final double POINTS_SECOND_ATTEMPT = 0.5; // points to award for correct guess on 2nd attempt
    
    private final ArrayList<GameListener> listeners;
    private final GameTimer timer;
    private final ChangeListener guessChangeListener;
    
    private GameChallenge[] challenges; // the challenges that make up the current game
    private int challengeNumber; // the current challenge that the user is attempting to solve
    private int level; // level of difficulty
    private boolean timerVisible; // is the timer visible?
    private int attempts; // how many attempts the user has made at solving the current challenge
    private double points; // how many points the user has earned for the current game
    
    public GameModel( IClock clock ) {
        
        listeners = new ArrayList<GameListener>();
        
        timer = new GameTimer( clock );
        timer.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                // notify when the timer changes
                if ( timerVisible ) {
                    fireTimeChanged();
                }
            }
        } );
        
        guessChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                // notify when the user's guess changes
                fireGuessChanged();
            }
        };
        
        initGame( LEVEL_RANGE.getDefault(), DEFAULT_TIMER_ENABLED );
    }
    
    /*
     * Initializes a new game.
     */
    private void initGame( int level, boolean timerVisible ) {
        setLevel( level );
        setTimerVisible( timerVisible );
        setPoints( 0 );
        setAttempts( 0 );
        newChallenges();
    }
    
    /**
     * Initiates a new game, depending on whether the current game was fully played.
     */
    public void newGame() {
        if ( !isGameCompleted() ) {
            fireGameAborted();
        }
        fireNewGame();
    }
    
    /**
     * Starts a new game.
     * @param level
     * @param timerVisible
     */
    public void startGame( int level, boolean timerVisible ) {
        initGame( level, timerVisible );
        timer.start();
        fireGameStarted();
    }
    
    /**
     * Ends the current game.
     */
    public void endGame() {
        timer.stop();
        if ( isGameCompleted() ) {
            fireGameCompleted();
        }
        else {
            fireGameAborted();
        }
    }
    
    /**
     * Advances to the next challenge.
     * If we've completed all challenges, then end the game.
     */
    public void nextChallenge() {
        if ( !isGameCompleted() ) {
            challengeNumber++;
            setAttempts( 0 );
            getChallenge().getGuess().addChangeListener( guessChangeListener );
            fireChallengeChanged();
        }
        else {
            endGame();
        }
    }
    
    /**
     * Checks the user's guess and award points accordingly.
     * @return true if the guess is correct, false if incorrect
     */
    public boolean checkGuess() {
        boolean correct = false;
        setAttempts( getAttempts() + 1 );
        if ( getChallenge().isCorrect() ) {
            correct = true;
            if ( getAttempts() == 1 ) {
                setPoints( getPoints() + POINTS_FIRST_ATTEMPT );
            }
            else if ( getAttempts() == 2 ){
                setPoints( getPoints() + POINTS_SECOND_ATTEMPT );
            }
            else {
                // subsequent attempts score zero points
            }
        }
        return correct;
    }
    
    //TODO: this needs to be rewritten, with many reactions to choose from, and more complicated selection criteria
    private void newChallenges() {
        if ( challenges != null ) {
            getChallenge().getGuess().removeChangeListener( guessChangeListener );
        }
        challengeNumber = 0;
        challenges = new GameChallenge[ CHALLENGES_PER_GAME ];
        //TODO: ensure that we don't have the same challenge twice in a row, or should they all be different?
        for ( int i = 0; i < challenges.length; i++ ) {
            ChemicalReaction reaction;
            ChallengeType challengeType;
            if ( level == 1 ) {
                reaction = new WaterReaction();
                challengeType = ChallengeType.AFTER;
            }
            else if ( level == 2 ) {
                reaction = new AmmoniaReaction();
                challengeType = ChallengeType.BEFORE;
            }
            else {
                reaction = new MethaneReaction();
                challengeType = Math.random() > 0.5 ? ChallengeType.BEFORE : ChallengeType.AFTER;
            }
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getRandomQuantity() );
            }
            challenges[i] = new GameChallenge( challengeType, reaction );
        }
        getChallenge().getGuess().addChangeListener( guessChangeListener );
        fireChallengeChanged();
    }
    
    /*
     * A game is completed if all challenges have been presented to the user.
     */
    private boolean isGameCompleted() {
        return ( challengeNumber == CHALLENGES_PER_GAME - 1 );
    }
    
    /*
     * Generates a random non-zero quantity.
     */
    private int getRandomQuantity() {
        return 1 + (int)( Math.random() * getQuantityRange().getMax() );
    }
    
    /**
     * Gets the index of the current challenge.
     */
    public int getChallengeNumber() {
        return challengeNumber;
    }
    
    /**
     * Gets the current challenge.
     */
    public GameChallenge getChallenge() {
        return challenges[ getChallengeNumber() ];
    }
    
    /**
     * Convenience method.
     * Gets the type of the current challenge.
     */
    public ChallengeType getChallengeType() {
        return getChallenge().getChallengeType();
    }
    
    /**
     * Convenience method.
     * Gets the reaction associated with the current challenge.
     */
    public ChemicalReaction getReaction() {
        return getChallenge().getReaction();
    }
    
    /**
     * Convenience method.
     * Gets the user's guess associated with the current challenge.
     */
    public GameGuess getGuess() {
        return getChallenge().getGuess();
    }
    
    /**
     * Gets the number of challenges per game.
     * @return
     */
    public static int getChallengesPerGame() {
        return CHALLENGES_PER_GAME;
    }
    
    /**
     * Gets the number of points that constitutes a perfect score in a completed game.
     * @return
     */
    public static double getPerfectScore() {
        return getChallengesPerGame() * POINTS_FIRST_ATTEMPT;
    }
    
    /**
     * Gets the range of difficulty levels.
     * @return
     */
    public static IntegerRange getLevelRange() {
        return LEVEL_RANGE;
    }
    
    /*
     * Sets the difficulty level of the current game.
     */
    private void setLevel( int level ) {
        if ( level != this.level ) {
            this.level = level;
            fireLevelChanged();
        }
    }
    
    /**
     * Gets the difficulty level of the current game.
     * @return
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Determines whether the game timer is visible.
     * @param visible
     */
    //XXX This doesn't belong in the model
    private void setTimerVisible( boolean visible ) {
        if ( visible != this.timerVisible ) {
            this.timerVisible = visible;
            fireTimerVisibleChanged();
        }
    }
    
    /**
     * Is the game timer visible?
     * @return
     */
    //XXX This doesn't belong in the model
    public boolean isTimerVisible() {
        return timerVisible;
    }
    
    /**
     * Gets the time (in ms) since the current game was started.
     * @return
     */
    public long getTime() {
        return timer.getTime();
    }
    
    /*
     * Sets the number of attempts that the user has made at solving the current challenge.
     */
    private void setAttempts( int attempts ) {
        if ( attempts != this.attempts ) {
            this.attempts = attempts;
            fireAttemptsChanged();
        }
    }
    
    /**
     * Gets the number of attempts that the user has made at solving the current challenge.
     * @return
     */
    public int getAttempts() {
        return attempts;
    }
    
    /**
     * Sets the number of points scored so far for the current game.
     * @return
     */
    private void setPoints( double score ) {
        if ( score != this.points ) {
            this.points = score;
            firePointsChanged();
        }
    }
    
    /**
     * Gets the number of points scored so far for the current game.
     * @return
     */
    public double getPoints() {
        return points;
    }
    
    //---------------------------------------------------------------------------------
    //
    //  Listener interface and related methods
    //
    //---------------------------------------------------------------------------------
    
    public interface GameListener {
        public void newGame(); // user requested to start a new game
        public void gameStarted(); // a new game was started
        public void gameCompleted(); // the current game was completed
        public void gameAborted(); // the current game was aborted before it was completed
        public void challengeChanged(); // the challenge changed
        public void guessChanged(); // user's guess changed
        public void pointsChanged(); // the number of points changed
        public void levelChanged(); // the level of difficulty changed
        public void attemptsChanged(); // the number of attempts changed
        public void timerVisibleChanged(); // the timer visibility was changed
        public void timeChanged(); // the time shown on the timer changed
    }
    
    /**
     * Default implementation of GameListener, does nothing.
     */
    public static class GameAdapter implements GameListener {
        public void newGame() {}
        public void gameStarted() {}
        public void gameCompleted() {}
        public void gameAborted() {}
        public void challengeChanged() {}
        public void guessChanged() {}
        public void pointsChanged() {}
        public void levelChanged() {}
        public void attemptsChanged() {}
        public void timerVisibleChanged() {}
        public void timeChanged() {}
    }
    
    public void addGameListener( GameListener listener ) {
        listeners.add( listener );
    }
    
    public void removeGameListener( GameListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireNewGame() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.newGame, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.newGame();
        }
    }
    
    private void fireGameStarted() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireGameStarted, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.gameStarted();
        }
    }
    
    private void fireGameCompleted() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireGameCompleted, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.gameCompleted();
        }
    }
    
    private void fireGameAborted() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireGameAborted, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.gameAborted();
        }
    }
    
    private void fireChallengeChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireChallengeChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.challengeChanged();
        }
    }
    
    private void fireGuessChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireGuessChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.guessChanged();
        }
    }
    
    private void firePointsChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.firePointsChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.pointsChanged();
        }
    }
    
    private void fireLevelChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireLevelChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.levelChanged();
        }
    }
    
    private void fireAttemptsChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireAttemptsChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.attemptsChanged();
        }
    }
    
    private void fireTimerVisibleChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireTimerVisibleChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.timerVisibleChanged();
        }
    }
    
    private void fireTimeChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.timeChanged();
        }
    }
}
