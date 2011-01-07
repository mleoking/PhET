// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.AmmoniaReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.WaterReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.TwoProductReactions.MethaneReaction;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeVisibility;

/**
 * A very simple, predictable factory for generating game challenges,
 * used during development to get the game working.
 * <p>
 * A single reaction is used for each level, as follows:
 * <ul>
 * <li>Level 1: water, After
 * <li>Level 2: ammonia, Before
 * <li>Level 3: methane, random choice of Before or After
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevChallengeFactory extends AbstractChallengeFactory {
    
    /**
     * Creates challenges.
     *
     * @param numberOfChallenges
     * @param level 1-N
     * @param maxQuantity
     * @param imagesVisible
     * @param numbersVisible
     */
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, int maxQuantity, ChallengeVisibility challengeVisibility ) {
       
        GameChallenge[] challenges = new GameChallenge[ numberOfChallenges ];
        for ( int i = 0; i < challenges.length; i++ ) {
            
            // reaction and challenge type
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
            
            // quantities
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getRandomQuantity( maxQuantity ) );
            }
            fixQuantityRangeViolation( reaction, maxQuantity ); // do this before creating the challenge, see #2156
            
            challenges[i] = new GameChallenge( reaction, challengeType, challengeVisibility );
        }
        return challenges;
    }
}
