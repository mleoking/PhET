/* Copyright 2010, University of Colorado */

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

    private final ChallengeType challengeType;
    private final ChemicalReaction reaction; // the actual reaction, which won't be modified
    private final GameGuess guess; // the user's guess

    public GameChallenge( ChallengeType challengeType, ChemicalReaction reaction ) {
        this.challengeType = challengeType;
        this.reaction = reaction;
        this.guess = new GameGuess( reaction, challengeType );
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
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
}