/* Copyright 2009, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;

public class GameChallenge {
    
    public static enum ChallengeType { HOW_MANY_REACTANTS, HOW_MANY_PRODUCTS_AND_LEFTOVERS };

    private final ChemicalReaction reaction;
    private final ChallengeType challengeType;

    public GameChallenge( ChemicalReaction reaction, ChallengeType challengeType ) {
        this.reaction = reaction;
        this.challengeType = challengeType;
    }

    public ChemicalReaction getReaction() {
        return reaction;
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }
}