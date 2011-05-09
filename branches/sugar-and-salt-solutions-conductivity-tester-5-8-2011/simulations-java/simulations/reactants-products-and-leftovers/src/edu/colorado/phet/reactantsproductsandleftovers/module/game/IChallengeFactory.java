// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeVisibility;

/**
 * Interface for creating game challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IChallengeFactory {
    
    /**
     * Creates a set of challenges.
     * 
     * @param level 1-N, as in the model
     * @param maxQuantity
     * @param challengeVisibility
     * @return
     */
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, int maxQuantity, ChallengeVisibility challengeVisibility );
}
