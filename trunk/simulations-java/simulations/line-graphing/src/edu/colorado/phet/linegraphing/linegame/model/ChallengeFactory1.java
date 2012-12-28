// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;

/**
 * Creates challenges for game level 1, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChallengeFactory1 {

    // One slope will be chosen from each of these 3 bins.
    private static final Fraction[][] SLOPES_BINS = new Fraction[][] {
            { new Fraction( 3, 2 ), new Fraction( 4, 3 ), new Fraction( 5, 2 ), new Fraction( 5, 3 ) },
            { new Fraction( 1, 2 ), new Fraction( 1, 3 ), new Fraction( 1, 4 ), new Fraction( 1, 5 ) },
            { new Fraction( 2, 3 ), new Fraction( 3, 4 ), new Fraction( 3, 5 ), new Fraction( 2, 5 ) }
    };

    private static final IntegerRange Y_INTERCEPT_RANGE = new IntegerRange( -9, 4 );
    private static final IntegerRange X1_RANGE = new IntegerRange( -9, 4 );
    private static final IntegerRange Y1_RANGE = new IntegerRange( -9, 4 );

    private ChallengeFactory1() {}

    public static IChallenge[] createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        IChallenge[] challenges = new IChallenge[6];

        // 3 GTL challenges
        // SI: slope
        // SI: intercept
        // PS: random choice of point or slope

        // 3 MTE challenges
        // SI: slope
        // SI: intercept
        // PS: if GTL choice was slope, then point; else slope

        // Other requirements:
        // - for intercept challenges, 1 positive, 1 negative
        // - for point challenges, point must be in Q1 or Q3

        // randomize the order

        return challenges;
    }
}
