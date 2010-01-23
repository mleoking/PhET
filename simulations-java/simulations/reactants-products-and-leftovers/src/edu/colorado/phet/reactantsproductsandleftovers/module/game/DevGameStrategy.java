/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
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

    public GameChallenge[] createChallenges( int numberOfChallenges, int level, IntegerRange quantityRange ) {
       
        GameChallenge[] challenges = new GameChallenge[ numberOfChallenges ];
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
                reactant.setQuantity( getRandomQuantity( quantityRange ) );
            }
            challenges[i] = new GameChallenge( challengeType, reaction );
        }
        return challenges;
    }
    
    /*
     * Generates a random non-zero quantity.
     */
    private static int getRandomQuantity( IntegerRange quantityRange ) {
        return 1 + (int)( Math.random() * quantityRange.getMax() );
    }

}
