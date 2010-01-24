/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;


public interface IGameStrategy {
    
    /**
     * Creates a set of challenges.
     * 
     * @param level
     * @return
     */
    public GameChallenge[] createChallenges( int level );
}
