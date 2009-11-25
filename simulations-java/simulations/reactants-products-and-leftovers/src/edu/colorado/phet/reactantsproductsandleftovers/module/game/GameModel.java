package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.WaterReaction;

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
    
    private final ArrayList<GameChangeListener> listeners;
    
    private int challengeNumber;
    private ChemicalReaction challengeReaction, guessReaction;
    private ChallengeType challengeType;
    private int level;
    private double points;
    private int attempts;
    private boolean timerEnabled;
    
    public GameModel() {
        listeners = new ArrayList<GameChangeListener>();
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
        fireChallengeChanged();
    }
    
    private void newReaction() {
        
        challengeReaction = new WaterReaction(); //XXX choose based on level and challengeNumber
        for ( Reactant reactant : challengeReaction.getReactants() ) {
            reactant.setQuantity( getRandomQuantity() );
        }
        
        guessReaction = new WaterReaction(); //XXX must be same type as challengeReaction
        for ( Reactant reactant : guessReaction.getReactants() ) {
            reactant.setQuantity( 0 );
        }
    }
    
    private int getRandomQuantity() {
        return 1 + (int)( Math.random() * getQuantityRange().getMax() );
    }
    
    /*
     * Chooses a challenge type based on level and which challenge we're currently on. 
     */
    private void newChallengeType() {
        challengeType = ChallengeType.GUESS_AFTER; //XXX
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
    
    public ChemicalReaction getChallengeReaction() {
        return challengeReaction;
    }
    
    public ChemicalReaction getGuessReaction() {
        return guessReaction;
    }
    
    public interface GameChangeListener {
        public void newGame();
        public void challengeChanged();
        public void pointsChanged();
        public void levelChanged();
        public void timerEnabledChanged();
        public void timeChanged();
    }
    
    public static class GameChangeAdapter implements GameChangeListener {
        public void newGame() {}
        public void challengeChanged() {}
        public void pointsChanged() {}
        public void levelChanged() {}
        public void timerEnabledChanged() {}
        public void timeChanged() {}
    }
    
    public void addGameChangeListener( GameChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeGameChangeListener( GameChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireNewGame() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.newGame();
        }
    }
    
    private void fireChallengeChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.challengeChanged();
        }
    }
    
    private void firePointsChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.pointsChanged();
        }
    }
    
    private void fireLevelChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.levelChanged();
        }
    }
    
    private void fireTimerEnabledChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.timerEnabledChanged();
        }
    }
    
    private void fireTimeChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.timeChanged();
        }
    }
}
