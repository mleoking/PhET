/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.AmmoniaReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.WaterReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.TwoProductReactions.MethaneReaction;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;

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
    
    /*
     * Abstract "hook" in the base class.
     * This handles creation of the challenges, which the base class then verifies.
     */
    protected GameChallenge[] createChallengesAux( int level, boolean imagesVisible ) {
       
        GameChallenge[] challenges = new GameChallenge[ getChallengesPerGame() ];
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
                reactant.setQuantity( getRandomQuantity() );
            }
            
            challenges[i] = new GameChallenge( reaction, challengeType, imagesVisible );
        }
        return challenges;
    }
}
