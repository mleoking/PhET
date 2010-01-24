/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.AmmoniaReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.WaterReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.TwoProductReactions.MethaneReaction;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;

/**
 * Game strategy used for development.
 * <p>
 * A single reaction is used for each level, as follows:
 * <ul>
 * <li>Level 1: water, after
 * <li>Level 2: ammonia, before
 * <li>Level 3: methane, random choice of after or before
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevGameStrategy implements IGameStrategy {
    
    private static final int CHALLENGES_PER_GAME = GameModel.getChallengesPerGame();
    private static final int MAX_QUANTITY = GameModel.getQuantityRange().getMax();

    public GameChallenge[] createChallenges( int level ) {
       
        GameChallenge[] challenges = new GameChallenge[ CHALLENGES_PER_GAME ];
        for ( int i = 0; i < challenges.length; i++ ) {
            ChemicalReaction reaction;
            ChallengeType challengeType;
            if ( level == 1 ) {
                reaction = new WaterReaction();
                challengeType = ChallengeType.AFTER;
            }
            else if ( level == 2 ) {
                reaction = new AmmoniaReaction();
                challengeType = ChallengeType.BEFORE;
            }
            else {
                reaction = new MethaneReaction();
                challengeType = Math.random() > 0.5 ? ChallengeType.BEFORE : ChallengeType.AFTER;
            }
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getRandomQuantity() );
            }
            challenges[i] = new GameChallenge( challengeType, reaction );
        }
        return challenges;
    }
    
    /*
     * Generates a random non-zero quantity.
     */
    private static int getRandomQuantity() {
        return 1 + (int)( Math.random() * MAX_QUANTITY );
    }

}
