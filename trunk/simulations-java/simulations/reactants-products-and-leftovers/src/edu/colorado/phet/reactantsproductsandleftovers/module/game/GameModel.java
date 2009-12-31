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
    
    private static final boolean DEBUG_OUTPUT_ENABLED = true;
    
    private static final int CHALLENGES_PER_GAME = 10;
    private static final IntegerRange LEVEL_RANGE = new IntegerRange( 1, 3, 1 ); // difficulty level
    private static final boolean DEFAULT_TIMER_ENABLED = true;
    private static final double POINTS_FIRST_ATTEMPT = 1;  // points to award for correct answer on 1st attempt
    private static final double POINTS_SECOND_ATTEMPT = 0.5; // points to award for correct answer on 2nd attempt
    
    private final ArrayList<GameListener> listeners;
    private final GameTimer timer;
    private final ChangeListener answerChangeListener;
    
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
                if ( timerVisible ) {
                    fireTimeChanged();
                }
            }
        } );
        
        answerChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireAnswerChanged();
            }
        };
        
        startGame( LEVEL_RANGE.getDefault(), DEFAULT_TIMER_ENABLED );
    }
    
    public void newGame() {
        if ( challengeNumber < CHALLENGES_PER_GAME -1 ) {
            fireGameAborted();
        }
        fireNewGame();
    }
    
    public void startGame( int level, boolean timerVisible ) {
        setLevel( level );
        setTimerVisible( timerVisible );
        setPoints( 0 );
        newChallenges();
        timer.start();
        fireGameStarted();
    }
    
    public void endGame() {
        timer.stop();
        if ( challengeNumber == CHALLENGES_PER_GAME - 1 ) {
            fireGameCompleted();
        }
        else {
            fireGameAborted();
        }
    }
    
    public void nextChallenge() {
        if ( challengeNumber < CHALLENGES_PER_GAME - 1 ) {
            challengeNumber++;
            setAttempts( 0 );
            getChallenge().getAnswer().addChangeListener( answerChangeListener );
            fireChallengeChanged();
        }
        else {
            endGame();
        }
    }
    
    public boolean checkAnswer() {
        boolean correct = false;
        setAttempts( getAttempts() + 1 );
        if ( getAnswer().isCorrect() ) {
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
    
    private void newChallenges() {
        if ( challenges != null ) {
            getChallenge().getAnswer().removeChangeListener( answerChangeListener );
        }
        challengeNumber = 0;
        challenges = new GameChallenge[ CHALLENGES_PER_GAME ];
        //TODO: ensure that we don't have the same challenge twice in a row, or should they all be different?
        for ( int i = 0; i < challenges.length; i++ ) {
            ChemicalReaction reaction;
            ChallengeType challengeType;
            if ( level == 1 ) {
                reaction = new WaterReaction();
                challengeType = ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS;
            }
            else if ( level == 2 ) {
                reaction = new AmmoniaReaction();
                challengeType = ChallengeType.HOW_MANY_REACTANTS;
            }
            else {
                reaction = new MethaneReaction();
                challengeType = ChallengeType.HOW_MANY_REACTANTS;
            }
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getRandomQuantity() );
            }
            challenges[i] = new GameChallenge( challengeType, reaction );
        }
        getChallenge().getAnswer().addChangeListener( answerChangeListener );
        fireChallengeChanged();
    }
    
    /*
     * Generates a random non-zero quantity.
     */
    private int getRandomQuantity() {
        return 1 + (int)( Math.random() * getQuantityRange().getMax() );
    }
    
    public int getChallengeNumber() {
        return challengeNumber;
    }
    
    public GameChallenge getChallenge() {
        return challenges[ getChallengeNumber() ];
    }
    
    public ChallengeType getChallengeType() {
        return getChallenge().getChallengeType();
    }
    
    public ChemicalReaction getReaction() {
        return getChallenge().getReaction();
    }
    
    public GameAnswer getAnswer() {
        return getChallenge().getAnswer();
    }
    
    public static int getChallengesPerGame() {
        return CHALLENGES_PER_GAME;
    }
    
    public static IntegerRange getLevelRange() {
        return LEVEL_RANGE;
    }
    
    private void setLevel( int level ) {
        if ( level != this.level ) {
            this.level = level;
            fireLevelChanged();
        }
    }
    
    public int getLevel() {
        return level;
    }
    
    private void setTimerVisible( boolean visible ) {
        if ( visible != this.timerVisible ) {
            this.timerVisible = visible;
            fireTimerVisibleChanged();
        }
    }
    
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
    
    private void setAttempts( int attempts ) {
        if ( attempts != this.attempts ) {
            this.attempts = attempts;
            fireAttemptsChanged();
        }
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    private void setPoints( double score ) {
        if ( score != this.points ) {
            this.points = score;
            firePointsChanged();
        }
    }
    
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
        public void answerChanged(); // user's answer changed
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
        public void answerChanged() {}
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
    
    private void fireAnswerChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "GameModel.fireAnswerChanged, notifying " + listenersCopy.size() + " listeners" );
        }
        for ( GameListener listener : listenersCopy ) {
            listener.answerChanged();
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
