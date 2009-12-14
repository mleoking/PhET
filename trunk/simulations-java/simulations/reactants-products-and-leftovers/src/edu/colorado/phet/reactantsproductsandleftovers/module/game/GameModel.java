package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
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
    
    private static final int CHALLENGES_PER_GAME = 10;
    private static final IntegerRange LEVEL_RANGE = new IntegerRange( 1, 3, 1 ); // difficulty level
    private static final boolean DEFAULT_TIMER_ENABLED = true;
    
    private final IClock clock;
    private final ArrayList<GameListener> listeners;
    
    private GameChallenge[] challenges;
    private int challengeNumber;
    private int level;
    private boolean timerEnabled;
    private int attempts;
    private double points;
    private long startTime; // System time in ms when the game started
    private long elapsedTime; // ms
    
    public GameModel( IClock clock ) {
        
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                elapsedTime = clockEvent.getWallTime() - startTime;
                if ( timerEnabled ) {
                    fireTimeChanged();
                }
            }
        } );
        
        listeners = new ArrayList<GameListener>();
        startGame( LEVEL_RANGE.getDefault(), DEFAULT_TIMER_ENABLED );
    }
    
    public void startGame( int level, boolean timerEnabled ) {
        setLevel( level );
        setTimerEnabled( timerEnabled );
        setPoints( 0 );
        newChallenges();
        elapsedTime = 0;
        clock.resetSimulationTime();
        clock.start();
        startTime = System.currentTimeMillis(); // don't use clock.getWallTime, it's not valid until the clock ticks
        fireGameStarted();
    }
    
    public void endGame() {
        clock.pause();
        if ( challengeNumber == CHALLENGES_PER_GAME - 1 ) {
            fireGameCompleted();
        }
        else {
            fireGameAborted();
        }
    }
    
    public void nextChallenge() {
        challengeNumber++;
        if ( challengeNumber < CHALLENGES_PER_GAME ) {
            setAttempts( 0 );
            fireChallengeChanged();
        }
        else {
            endGame();
        }
    }
    
    private void newChallenges() {
        challengeNumber = 0;
        challenges = new GameChallenge[ CHALLENGES_PER_GAME ];
        for ( int i = 0; i < challenges.length; i++ ) {
            ChemicalReaction reaction;
            ChallengeType challengeType;
            if ( level == 1 ) {
                reaction = new WaterReaction();
                challengeType = ChallengeType.HOW_MANY_REACTANTS;
            }
            else if ( level == 2 ) {
                reaction = new AmmoniaReaction();
                challengeType = ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS;
            }
            else {
                reaction = new MethaneReaction();
                challengeType = ChallengeType.HOW_MANY_PRODUCTS_AND_LEFTOVERS;
            }
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getRandomQuantity() );
            }
            challenges[i] = new GameChallenge( reaction, challengeType );
        }
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
    
    public ChemicalReaction getReaction() {
        return challenges[challengeNumber].getReaction();
    }
    
    public ChallengeType getChallengeType() {
        return challenges[challengeNumber].getChallengeType();
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
    
    private void setTimerEnabled( boolean timerEnabled ) {
        if ( timerEnabled != this.timerEnabled ) {
            this.timerEnabled = timerEnabled;
            fireTimerEnabledChanged();
        }
    }
    
    public boolean isTimerEnabled() {
        return timerEnabled;
    }
    
    public long getElapsedTime() {
        return elapsedTime;
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
    
    public interface GameListener {
        public void gameStarted(); // a new game was started
        public void gameCompleted(); // the current game was completed
        public void gameAborted(); // the current game was aborted before it was completed
        public void challengeChanged(); // the challenge changed
        public void pointsChanged(); // the number of points changed
        public void levelChanged(); // the level of difficulty changed
        public void attemptsChanged(); // the number of attempts changed
        public void timerEnabledChanged(); // the timer was enabled or disabled
        public void timeChanged(); // the time shown on the timer changed
    }
    
    public static class GameAdapter implements GameListener {
        public void gameStarted() {}
        public void gameCompleted() {}
        public void gameAborted() {}
        public void challengeChanged() {}
        public void pointsChanged() {}
        public void levelChanged() {}
        public void attemptsChanged() {}
        public void timerEnabledChanged() {}
        public void timeChanged() {}
    }
    
    public void addGameListener( GameListener listener ) {
        listeners.add( listener );
    }
    
    public void removeGameListener( GameListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireGameStarted() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.gameStarted();
        }
    }
    
    private void fireGameCompleted() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.gameCompleted();
        }
    }
    
    private void fireGameAborted() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.gameAborted();
        }
    }
    
    private void fireChallengeChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.challengeChanged();
        }
    }
    
    private void firePointsChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.pointsChanged();
        }
    }
    
    private void fireLevelChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.levelChanged();
        }
    }
    
    private void fireAttemptsChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.attemptsChanged();
        }
    }
    
    private void fireTimerEnabledChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.timerEnabledChanged();
        }
    }
    
    private void fireTimeChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.timeChanged();
        }
    }
}
