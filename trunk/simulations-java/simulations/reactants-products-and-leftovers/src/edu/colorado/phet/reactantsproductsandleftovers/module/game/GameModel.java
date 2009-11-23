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
    
    private final ArrayList<GameChangeListener> listeners;
    
    private int challengeNumber;
    private ChemicalReaction challengeReaction, guessReaction;
    private ChallengeType challengeType;
    private int level;
    private double score;
    private int attempts;
    
    public GameModel() {
        listeners = new ArrayList<GameChangeListener>();
        newGame();
    }
    
    public void reset() {
        //XXX do we need this in the Game?
    }
    
    public void newGame() {
        challengeNumber = 0;
        score = 0;
        newChallenge();
    }
    
    private void newChallenge() {
        challengeNumber++;
        attempts = 0;
        newReaction();
        newChallengeType();
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
    
    public void setLevel( int level ) {
        if ( level != this.level)  {
            this.level = level;
            fireLevelChanged();
        }
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    private void setScore( double score ) {
        if ( score != this.score ) {
            this.score = score;
            fireScoreChanged();
        }
    }
    
    public double getScore() {
        return score;
    }
    
    public ChemicalReaction getChallengeReaction() {
        return challengeReaction;
    }
    
    public ChemicalReaction getGuessReaction() {
        return guessReaction;
    }
    
    public interface GameChangeListener {
        public void challengeChanged();
        public void levelChanged();
        public void scoreChanged();
    }
    
    public static class GameChangeAdapter implements GameChangeListener {
        public void challengeChanged() {}
        public void levelChanged() {}
        public void scoreChanged() {}
    }
    
    public void addChangeListeners( GameChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListeners( GameChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireChallengeChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.challengeChanged();
        }
    }
    
    private void fireLevelChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.levelChanged();
        }
    }
    
    private void fireScoreChanged() {
        ArrayList<GameChangeListener> listenersCopy = new ArrayList<GameChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( GameChangeListener listener : listenersCopy ) {
            listener.scoreChanged();
        }
    }
}
