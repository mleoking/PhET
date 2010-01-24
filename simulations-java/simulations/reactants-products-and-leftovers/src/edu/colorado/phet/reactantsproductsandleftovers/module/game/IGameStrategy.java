/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

/**
 * Interface for generating game challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IGameStrategy {
    
    /**
     * Creates a set of challenges.
     * 
     * @param level
     * @return
     */
    public GameChallenge[] createChallenges( int level );
}
