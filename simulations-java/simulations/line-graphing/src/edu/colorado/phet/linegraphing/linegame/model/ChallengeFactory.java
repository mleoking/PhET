// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Wrapper for challenge generation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChallengeFactory {

    private final IntegerRange xRange, yRange;

    public ChallengeFactory( IntegerRange xRange, IntegerRange yRange ) {
        this.xRange = xRange;
        this.yRange = yRange;
    }

    public ArrayList<IChallenge> createChallenges( int level ) {
        if ( level == 1 ) {
            return new ChallengeFactory1().createChallenges( xRange, yRange );
        }
        else {
            throw new IllegalArgumentException( "unsupported level: " + level );
        }
    }
}
