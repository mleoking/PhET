/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeVisibility;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel extends RPALModel {
    
    private static final boolean DEBUG_NOTIFICATION = false;
    private static final boolean DEBUG_WRONG_GUESS = false;
    
    private static final int CHALLENGES_PER_GAME = 5;
    private static final IntegerRange LEVEL_RANGE = new IntegerRange( 1, 3, 1 ); // difficulty level
    private static final double POINTS_FIRST_ATTEMPT = 1;  // points to award for correct guess on 1st attempt
    private static final double POINTS_SECOND_ATTEMPT = 0.5; // points to award for correct guess on 2nd attempt
    
    private static final boolean DEFAULT_TIMER_VISIBLE = true;
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final ChallengeVisibility DEFAULT_CHALLENGE_VISIBILITY = ChallengeVisibility.BOTH;
    
    private final EventListenerList listeners;
    private final GameTimer timer;
    private final ChangeListener guessChangeListener;
    private final IChallengeFactory challengeFactory;
    private final long[] bestTimes; // best times for each level, in ms
    
    private GameChallenge[] challenges; // the challenges that make up the current game
    private int challengeNumber; // the current challenge that the user is attempting to solve
    private int level; // level of difficulty
    private boolean timerVisible; // is the timer visible?
    private int attempts; // how many attempts the user has made at solving the current challenge
    private double points; // how many points the user has earned for the current game
    private boolean soundEnabled; // is sound enabled?
    private boolean isNewBestTime;
    private ChallengeVisibility challengeVisibility;
    
    public GameModel( IClock clock ) {
        
        listeners = new EventListenerList();
        
        bestTimes = new long[ getLevelRange().getLength() + 1 ]; // all zero by default
        isNewBestTime = false;
        
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
        
        challengeFactory = new NumberOfVariablesChallengeFactory();
        
        initGame( LEVEL_RANGE.getDefault(), DEFAULT_TIMER_VISIBLE, DEFAULT_SOUND_ENABLED, DEFAULT_CHALLENGE_VISIBILITY );
    }
    
    /*
     * Initializes a new game.
     */
    private void initGame( int level, boolean timerVisible, boolean soundEnabled, ChallengeVisibility challengeVisibility ) {
        isNewBestTime = false;
        setLevel( level );
        setTimerVisible( timerVisible );
        setSoundEnabled( soundEnabled );
        setChallengeVisibility( challengeVisibility );
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
     * @param soundEnabled
     * @param challengeVisibility
     */
    public void startGame( int level, boolean timerVisible, boolean soundEnabled, ChallengeVisibility challengeVisibility ) {
        initGame( level, timerVisible, soundEnabled, challengeVisibility );
        timer.start();
        fireGameStarted();
    }
    
    /**
     * Ends the current game.
     */
    public void endGame() {
        timer.stop();
        if ( isGameCompleted() ) {
            rememberBestTime();
            fireGameCompleted();
        }
        else {
            fireGameAborted();
        }
    }
    
    /*
     * Remembers the best time for the current game level.
     * Times are only remembered if the timer was visible during the game, and the score was perfect.
     */
    private void rememberBestTime() {
        if ( isTimerVisible() && isPerfectScore() ) {
            if ( bestTimes[level - 1] == 0 ) {
                // this is our first game
                bestTimes[level - 1] = getTime();
                isNewBestTime = true;
            }
            else {
                // compare with previous best time
                final long time = getTime();
                if ( time < bestTimes[level - 1] ) {
                    bestTimes[level - 1] = time;
                    isNewBestTime = true;
                }
            }
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
            
            // stop the timer immediately when the last challenge is correctly guessed
            if ( getChallengeNumber() == getChallengesPerGame() - 1 ) {
                timer.stop();
            }
        }
        else if ( DEBUG_WRONG_GUESS ) { /* see #2156 */
            String reactionString = getChallenge().getReaction().getEquationPlainText();
            String challengeString = getChallenge().getReaction().getQuantitiesString();
            String guessString = getChallenge().getGuess().toString();
            System.out.println( "GameModel.checkGuess correct=" + correct + " reaction=" + reactionString + " challenge=[" + challengeString + "] guess=[" + guessString + "]" );
        }
        return correct;
    }
    
    /*
     * Creates a new set of challenges.
     */
    private void newChallenges() {
        if ( challenges != null ) {
            getChallenge().getGuess().removeChangeListener( guessChangeListener );
        }
        challengeNumber = 0;
        challenges = challengeFactory.createChallenges( CHALLENGES_PER_GAME, getLevel(), getQuantityRange().getMax(), challengeVisibility );
        getChallenge().getGuess().addChangeListener( guessChangeListener );
        fireChallengeChanged();
    }
    
    /*
     * A game is completed if all challenges have been presented to the user.
     */
    private boolean isGameCompleted() {
        return ( challengeNumber == CHALLENGES_PER_GAME - 1 );
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
    public boolean isTimerVisible() {
        return timerVisible;
    }
    
    /**
     * Determines whether sound is enabled.
     * @param soundEnabled
     */
    private void setSoundEnabled( boolean soundEnabled ) {
        if ( soundEnabled != this.soundEnabled ) {
            this.soundEnabled = soundEnabled;
            fireSoundEnabledChanged();
        }
    }
    
    /**
     * Is sound enabled?
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /*
     * Determines what's visible while the user is solving a challenge.
     * @param challengeVisibility
     */
    private void setChallengeVisibility( ChallengeVisibility challengeVisibility) {
        if ( challengeVisibility != this.challengeVisibility ) {
            this.challengeVisibility = challengeVisibility;
            fireChallengeVisibilityChanged();
        }
    }
    
    /**
     * What is visible while the user is solving a challenge?
     * @return
     */
    public ChallengeVisibility getChallengeVisibility() {
        return challengeVisibility;
    }
    
    /**
     * Gets the time since the current game was started.
     * @return time, in ms
     */
    public long getTime() {
        return timer.getTime();
    }
    
    /**
     * Gets the best time for a specific level.
     * @return time, in ms
     */
    public long getBestTime() {
        return bestTimes[ level - 1 ];
    }
    
    /**
     * Is the current best time a new best time?
     */
    public boolean isNewBestTime() {
        return isNewBestTime;
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
    
    /**
     * Is the current score a perfect score?
     * @return
     */
    public boolean isPerfectScore() {
        return points == getPerfectScore();
    }

    //---------------------------------------------------------------------------------
    //
    //  Listener interface and related methods
    //
    //---------------------------------------------------------------------------------
    
    public interface GameListener extends EventListener {
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
        public void soundEnabledChanged(); // sound was toggled on or off
        public void challengeVisibilityChanged(); // what's visible while the user is solving a challenge has changed
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
        public void soundEnabledChanged() {}
        public void challengeVisibilityChanged() {}
        public void timeChanged() {}
    }
    
    public void addGameListener( GameListener listener ) {
        listeners.add( GameListener.class, listener );
    }
    
    public void removeGameListener( GameListener listener ) {
        listeners.remove( GameListener.class, listener );
    }
    
    private void fireNewGame() {
        firePrintDebug( "fireNewGame" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.newGame();
        }
    }
    
    private void fireGameStarted() {
        firePrintDebug( "fireGameStarted" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.gameStarted();
        }
    }
    
    private void fireGameCompleted() {
        firePrintDebug( "fireGameCompleted" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.gameCompleted();
        }
    }
    
    private void fireGameAborted() {
        firePrintDebug( "fireGameAborted" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.gameAborted();
        }
    }
    
    private void fireChallengeChanged() {
        firePrintDebug( "fireChallengeChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.challengeChanged();
        }
    }
    
    private void fireGuessChanged() {
        firePrintDebug( "fireGuessChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.guessChanged();
        }
    }
    
    private void firePointsChanged() {
        firePrintDebug( "firePointsChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.pointsChanged();
        }
    }
    
    private void fireLevelChanged() {
        firePrintDebug( "fireLevelChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.levelChanged();
        }
    }
    
    private void fireAttemptsChanged() {
        firePrintDebug( "fireAttemptsChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.attemptsChanged();
        }
    }
    
    private void fireTimerVisibleChanged() {
        firePrintDebug( "fireTimerVisibleChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.timerVisibleChanged();
        }
    }
    
    private void fireSoundEnabledChanged() {
        firePrintDebug( "fireSoundEnabledChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.soundEnabledChanged();
        }
    }
    
    private void fireChallengeVisibilityChanged() {
        firePrintDebug( "fireChallengeVisibilityChanged" );
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.challengeVisibilityChanged();
        }
    }
    
    private void fireTimeChanged() {
        // no print debug for this, it happens too frequently
        for ( GameListener listener : listeners.getListeners( GameListener.class ) ) {
            listener.timeChanged();
        }
    }
    
    private void firePrintDebug( String methodName ) {
        if ( DEBUG_NOTIFICATION ) {
            System.out.println( "GameModel." + methodName + ", notifying " + listeners.getListeners( GameListener.class ).length + " listeners" );
        }
    }
}
