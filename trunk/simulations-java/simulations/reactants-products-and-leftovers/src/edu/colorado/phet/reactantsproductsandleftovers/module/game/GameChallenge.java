/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;

public class GameChallenge {
    
    public static enum ChallengeType { HOW_MANY_REACTANTS, HOW_MANY_PRODUCTS_AND_LEFTOVERS };

    private final ChallengeType challengeType;
    private final ChemicalReaction reaction;
    private final GameAnswer answer;

    public GameChallenge( ChallengeType challengeType, ChemicalReaction reaction ) {
        this.challengeType = challengeType;
        this.reaction = reaction;
        this.answer = new GameAnswer( reaction, challengeType );
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
    }

    public GameAnswer getAnswer() {
        return answer;
    }
}