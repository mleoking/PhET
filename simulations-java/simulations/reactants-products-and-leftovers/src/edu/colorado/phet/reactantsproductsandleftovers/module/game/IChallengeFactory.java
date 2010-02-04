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
     * @param level
     * @param imagesVisible
     * @return
     */
    public GameChallenge[] createChallenges( int level, boolean imagesVisible );
}
