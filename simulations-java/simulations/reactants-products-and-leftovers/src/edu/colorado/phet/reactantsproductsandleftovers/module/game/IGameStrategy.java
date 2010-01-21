/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

public interface IGameStrategy {
    
    /**
     * Creates a set of challenges.
     * 
     * @param numberOfChallenges
     * @param level
     * @param quantityRange
     * @return
     */
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, IntegerRange quantityRange );
}
