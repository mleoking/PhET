// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges based on level, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChallengeFactory {

    private final IntegerRange xRange, yRange;
    private final Random random;

    public ChallengeFactory( IntegerRange xRange, IntegerRange yRange ) {
        this.xRange = xRange;
        this.yRange = yRange;
        this.random = new Random();
    }

    public ArrayList<IChallenge> createChallenges( int level ) {
        if ( level == 1 ) {
            return createChallenges1();
        }
        else {
            throw new IllegalArgumentException( "unsupported level:" + level );
        }
    }

    /**
     * Creates challenges for Level 1, according to these specifications:
     *
     * 3 GTL challenges
     *     SI: slope
     *     SI: intercept
     *     PS: random choice of point or slope
     *
     * 3 MTE challenges
     *     SI: slope
     *     SI: intercept
     *     PS: if GTL choice was slope, then point; else slope
     *
     * Other requirements:
     *     unique points and intercepts
     *     for intercept challenges, 1 positive, 1 negative
     *     for point challenges, point must be in Q1 or Q3
     *
     * @return collection of challenges
     */
    private ArrayList<IChallenge> createChallenges1() {

        //TODO for intercept challenges, 1 positive, 1 negative
        //TODO for point challenges, point must be in Q1 or Q3

        ArrayList<IChallenge> challenges = new ArrayList<IChallenge>();

        // Things that can be randomly chosen
        ArrayList<ArrayList<Fraction>> slopeBins = new ArrayList<ArrayList<Fraction>>() {{
            add( new ArrayList<Fraction>() {{
                add( new Fraction( 3, 2 ) );
                add( new Fraction( 4, 3 ) );
                add( new Fraction( 5, 2 ) );
                add(  new Fraction( 5, 3 ) );
            }} );
            add( new ArrayList<Fraction>() {{
                add( new Fraction( 1, 2 ) );
                add( new Fraction( 1, 3 ) );
                add( new Fraction( 1, 4 ) );
                add( new Fraction( 1, 5 ) );
            }} );
            add( new ArrayList<Fraction>() {{
                add( new Fraction( 2, 3 ) );
                add( new Fraction( 3, 4 ) );
                add( new Fraction( 3, 5 ) );
                add( new Fraction( 2, 5 ) );
            }} );
        }};
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ), false );
        ArrayList<Integer> x1Values = rangeToList( new IntegerRange( -9, 4 ), true );
        ArrayList<Integer> y1Values = rangeToList( new IntegerRange( -9, 4 ), true );
        ArrayList<Integer> yIntercepts = rangeToList( new IntegerRange( -9, 4 ), true );
        ArrayList<ManipulationMode> pointSlopeManipulationMode = new ArrayList<ManipulationMode>() {{
            add( ManipulationMode.POINT );
            add( ManipulationMode.SLOPE );
        }};

        int index; // random index

        // GTL, SI, slope
        {
            // slope: choose from remaining bins
            index = randomIndex( slopeBinIndices );
            ArrayList<Fraction> slopes = new ArrayList<Fraction>( slopeBins.get( index ) );
            slopeBinIndices.remove( index );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept
            index = randomIndex( yIntercepts );
            final int yIntercept = yIntercepts.get( index );
            yIntercepts.remove( index );

            // challenge
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            IChallenge challenge = new GTL_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange );
            challenges.add( challenge );
        }

        // GTL, SI, intercept
        {
            // slope: choose from any bin
            ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ), false );
            index = randomIndex( indices );
            ArrayList<Fraction> slopes = new ArrayList<Fraction>( slopeBins.get( index ) );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );

            // y-intercept
            index = randomIndex( yIntercepts );
            final int yIntercept = yIntercepts.get( index );
            yIntercepts.remove( index );

            // challenge
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            IChallenge challenge = new GTL_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange );
            challenges.add( challenge );
        }

        // GTL, PS, point or slope
        {
             // manipulation mode
            index = randomIndex( pointSlopeManipulationMode );
            final ManipulationMode manipulationMode = pointSlopeManipulationMode.get( index );
            pointSlopeManipulationMode.remove( index );

            // x1
            index = randomIndex( x1Values );
            final int x1 = x1Values.get( index );
            x1Values.remove( index );

            // y1
            index = randomIndex( y1Values );
            final int y1 = y1Values.get( index );
            y1Values.remove( index );

            // slope
            int slopeBinIndex;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                // choose from remaining bins
                slopeBinIndex = randomIndex( slopeBinIndices );
                slopeBinIndices.remove( slopeBinIndex );
            }
            else {
                // choose from any bin
                ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ), false );
                slopeBinIndex = randomIndex( indices );
            }
            ArrayList<Fraction> slopes = new ArrayList<Fraction>( slopeBins.get( slopeBinIndex ) );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // challenge
            Line line = Line.createPointSlope( x1, y1, slope.numerator, slope.denominator );
            IChallenge challenge = new GTL_Challenge( line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // MTE, SI, slope
        {
            // slope: choose from remaining bins
            index = randomIndex( slopeBinIndices );
            ArrayList<Fraction> slopes = new ArrayList<Fraction>( slopeBins.get( index ) );
            slopeBinIndices.remove( index );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept
            index = randomIndex( yIntercepts );
            final int yIntercept = yIntercepts.get( index );
            yIntercepts.remove( index );

            // challenge
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            IChallenge challenge = new MTE_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange );
            challenges.add( challenge );
        }

        // GTL, SI, intercept
        {
            // slope: choose from any bin
            ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ), false );
            index = randomIndex( indices );
            ArrayList<Fraction> slopes = new ArrayList<Fraction>( slopeBins.get( index ) );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept
            index = randomIndex( yIntercepts );
            final int yIntercept = yIntercepts.get( index );
            yIntercepts.remove( index );

            // challenge
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            IChallenge challenge = new MTE_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange );
            challenges.add( challenge );
        }

        // MTE, PS, point or slope
        {
            // manipulation mode
            index = randomIndex( pointSlopeManipulationMode );
            final ManipulationMode manipulationMode = pointSlopeManipulationMode.get( index );
            pointSlopeManipulationMode.remove( index );

            // x1
            index = randomIndex( x1Values );
            final int x1 = x1Values.get( index );
            x1Values.remove( index );

            // y1
            index = randomIndex( y1Values );
            final int y1 = y1Values.get( index );
            y1Values.remove( index );

            // slope
            int slopeBinIndex;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                // choose from remaining bins
                slopeBinIndex = randomIndex( slopeBinIndices );
                slopeBinIndices.remove( slopeBinIndex );
            }
            else {
                // choose from any bin
                ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ), false );
                slopeBinIndex = randomIndex( indices );
            }
            ArrayList<Fraction> slopes = new ArrayList<Fraction>( slopeBins.get( slopeBinIndex ) );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // challenge
            Line line = Line.createPointSlope( x1, y1, slope.numerator, slope.denominator );
            IChallenge challenge = new MTE_Challenge( line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // randomize the order

        return challenges;
    }

    // Coverts an integer range to a list of values that are in that range.
    private static ArrayList<Integer> rangeToList( IntegerRange range, boolean skipZero ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int i = range.getMin(); i <= range.getMax(); i++ ) {
            if ( !( skipZero && i == 0 ) ) {
                list.add( i );
            }
        }
        assert( list.size() > 0 );
        return list;
    }

    // Gets a random index for a specified list.
    private int randomIndex( List list ) {
        return random.nextInt( list.size() );
    }
}
