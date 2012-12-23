// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Creates challenges for game level 1, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChallengeFactory1 {

    // One slope will be chosen from each of these 3 bins.
    private static final double[][] SLOPES_BINS = new double[][] {
            { 3d / 2d, 4d / 3d, 5d / 2d, 5d / 3d },
            { 1d / 2d, 1d / 3d, 1d / 4d, 1d / 5d },
            { 2d / 3d, 3d / 4d, 3d / 5d, 2d / 5d }
    };

    private static final DoubleRange SI_INTERCEPT_RANGE = new DoubleRange( -9, 4 );
    private static final DoubleRange PS_X1_RANGE = new DoubleRange( -9, 4 );
    private static final DoubleRange PS_Y1_RANGE = new DoubleRange( -9, 4 );

    private ChallengeFactory1() {}

    public static IChallenge[] createChallenges() {

        IChallenge[] challenges = new IChallenge[6];

        // 3 GTL challenges
        // SI: slope
        // SI: intercept
        // PS: random choice of point or slope

        // 3 MTE challenges
        // SI: point
        // SI: slope
        // PS: if GTL choice was slope, then point; else slope

        // randomize the order

        return challenges;
    }
}
