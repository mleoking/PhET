/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

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
     * @param imagesVisible
     * @param numbersVisible
     * @return
     */
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, int maxQuantity, boolean imagesVisible, boolean numbersVisible );
}
