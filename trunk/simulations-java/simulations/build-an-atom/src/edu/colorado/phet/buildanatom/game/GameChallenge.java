/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.game;

/**
 * A challenge consists of a reaction (with specific before and after quantities)
 * and the user's "guess", and a specification of which part of the reaction
 * (before or after) the user needs to guess.  This is essentially a data structure
 * that keeps all of these associated things together.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameChallenge {
    
    public static enum ChallengeType { BEFORE, AFTER };  // whether the user must guess the Before or After quantities
    
    public static enum ChallengeVisibility { MOLECULES, NUMBERS, BOTH }; // what things are visible while the user is solving a challenge?

    private final ChallengeType challengeType;
    private final ChallengeVisibility challengeVisibility; 
    private final GameGuess guess; // the user's guess

    public GameChallenge( ChallengeType challengeType, ChallengeVisibility visibility ) {
        this.challengeType = challengeType;
        this.challengeVisibility = visibility;
        this.guess = new GameGuess( challengeType );
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }
    
    public ChallengeVisibility getChallengeVisibility() {
        return challengeVisibility;
    }
    
    public boolean isMoleculesVisible() {
        return ( challengeVisibility != ChallengeVisibility.NUMBERS );
    }
    
    public boolean isNumbersVisible() {
        return ( challengeVisibility != ChallengeVisibility.MOLECULES );
    }
    
    public GameGuess getGuess() {
        return guess;
    }
    
    public boolean isCorrect() {
        // all reactants must be equal
        return true;
    }
    
    private String getChallengeTypeString() {
        return ( challengeType == ChallengeType.BEFORE ) ? "Before" : "After";
    }
}