package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.*;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel extends RPALModel {
    
    public static enum ChallengeType { GUESS_AFTER, GUESS_BEFORE };
    
    private static final int CHALLENGES_PER_GAME = 10;
    private static final IntegerRange LEVEL_RANGE = new IntegerRange( 1, 3, 1 );
    private static final boolean DEFAULT_IS_TIMED = true;
    
    private final ArrayList<GameListener> listeners;
    
    private int challengeNumber;
    private ChemicalReaction reaction;
    private ChallengeType challengeType;
    private int level;
    private double points;
    private int attempts;
    private boolean timerEnabled;
    
    public GameModel() {
        listeners = new ArrayList<GameListener>();
        newGame( LEVEL_RANGE.getDefault(), DEFAULT_IS_TIMED );
    }
    
    public void reset() {
        //XXX do we need this in the Game?
    }
    
    public void newGame( int level, boolean timerEnabled ) {
        setLevel( level );
        setTimerEnabled( timerEnabled );
        setPoints( 0 );
        challengeNumber = 0;
        newChallenge(); //XXX should probably generate all 10 challenges at once, since our algorithm isn't random, and in case we need history
        fireNewGame();
    }
    
    private void newChallenge() {
        challengeNumber++;
        setAttempts( 0 );
        newReaction();
        newChallengeType();
        fireReactionChanged();
    }
    
    private void newReaction() {
        
        //XXX this needs to be chosen based on level and challengeNumber
        if ( level == 1 ) {
            reaction = new WaterReaction();
        }
        else if ( level == 2 ) {
            reaction = new AmmoniaReaction();
        }
        else {
            reaction = new MethaneReaction();
        }
        
        for ( Reactant reactant : reaction.getReactants() ) {
            reactant.setQuantity( getRandomQuantity() );
        }
    }
    
    private int getRandomQuantity() {
        return 1 + (int)( Math.random() * getQuantityRange().getMax() ); //XXX should this be zero sometimes?
    }
    
    /*
     * Chooses a challenge type based on level and which challenge we're currently on. 
     */
    private void newChallengeType() {
        challengeType = ChallengeType.GUESS_AFTER; //XXX
    }
    
    public int getChallengeNumber() {
        return challengeNumber;
    }
    
    public ChallengeType getChallengeType() {
        return challengeType;
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
    
    public int getTime() {
        return 0; //XXX calculate time in seconds from RPALClock
    }
    
    private void setAttempts( int attempts ) {
        if ( attempts != this.attempts ) {
            this.attempts = attempts;
            //XXX notify?
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
    
    public ChemicalReaction getReaction() {
        return reaction;
    }
    
    public interface GameListener {
        public void newGame();
        public void reactionChanged();
        public void pointsChanged();
        public void levelChanged();
        public void timerEnabledChanged();
        public void timeChanged();
    }
    
    public static class GameAdapter implements GameListener {
        public void newGame() {}
        public void reactionChanged() {}
        public void pointsChanged() {}
        public void levelChanged() {}
        public void timerEnabledChanged() {}
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
        for ( GameListener listener : listenersCopy ) {
            listener.newGame();
        }
    }
    
    private void fireReactionChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.reactionChanged();
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
    
    private void fireTimerEnabledChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.timerEnabledChanged();
        }
    }
    
    //XXX model needs a clock
    private void fireTimeChanged() {
        ArrayList<GameListener> listenersCopy = new ArrayList<GameListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameListener listener : listenersCopy ) {
            listener.timeChanged();
        }
    }
}
