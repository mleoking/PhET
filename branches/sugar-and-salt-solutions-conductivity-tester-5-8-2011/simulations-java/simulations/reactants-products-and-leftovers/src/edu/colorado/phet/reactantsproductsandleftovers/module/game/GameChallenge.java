// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;

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

    private final ChemicalReaction reaction; // the actual reaction, which won't be modified
    private final ChallengeType challengeType;
    private final ChallengeVisibility challengeVisibility; 
    private final GameGuess guess; // the user's guess

    public GameChallenge( ChemicalReaction reaction, ChallengeType challengeType, ChallengeVisibility visibility ) {
        this.reaction = reaction;
        this.challengeType = challengeType;
        this.challengeVisibility = visibility;
        this.guess = new GameGuess( reaction, challengeType );
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
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
        for ( int i = 0; i < reaction.getNumberOfReactants(); i++ ) {
            if ( !guess.getReactant( i ).equals( reaction.getReactant( i ) ) ) { 
                return false;
            }
        }
        // all products must be equal
        for ( int i = 0; i < reaction.getNumberOfProducts(); i++ ) {
            if ( !guess.getProduct( i ).equals( reaction.getProduct( i ) ) ) { 
                return false;
            }
        }
        return true;
    }
    
    /**
     * Example: 2F<sub>2</sub>+1H<sub>2</sub>O->1OF<sub>2</sub>+2HF : 4,1 -> 1,2,2,0 : After
     */
    public String toString() {
        return reaction.getEquationHTML() + " : " + reaction.getQuantitiesString() + " : " + getChallengeTypeString();
    }
    
    private String getChallengeTypeString() {
        return ( challengeType == ChallengeType.BEFORE ) ? "Before" : "After";
    }
}