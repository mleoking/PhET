// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
     * <p/>
     * 3 GTL challenges
     * SI: slope
     * SI: intercept
     * PS: random choice of point or slope
     * <p/>
     * 3 MTE challenges
     * SI: slope
     * SI: intercept
     * PS: if GTL choice was slope, then point; else slope
     * <p/>
     * Other requirements:
     * unique points and intercepts
     * for intercept challenges, 1 positive, 1 negative
     * for point challenges, point must be in Q1 or Q3
     *
     * @return collection of challenges
     */
    private ArrayList<IChallenge> createChallenges1() {

        ArrayList<IChallenge> challenges = new ArrayList<IChallenge>();

        // for slope manipulation challenges, 1 slope must come from each bin
        ArrayList<ArrayList<Fraction>> slopeBins = new ArrayList<ArrayList<Fraction>>() {{
            add( new ArrayList<Fraction>() {{
                add( new Fraction( 3, 2 ) );
                add( new Fraction( 4, 3 ) );
                add( new Fraction( 5, 2 ) );
                add( new Fraction( 5, 3 ) );
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
        // for point manipulation challenges, (x1,y1) must be in Q1 or Q3
        ArrayList<ArrayList<Integer>> x1Bins = new ArrayList<ArrayList<Integer>>() {{
            add( rangeToList( new IntegerRange( -9, -1 ) ) );
            add( rangeToList( new IntegerRange( 1, 4 ) ) );
        }};
        ArrayList<ArrayList<Integer>> y1Bins = new ArrayList<ArrayList<Integer>>() {{
            add( rangeToList( new IntegerRange( -9, -1 ) ) );
            add( rangeToList( new IntegerRange( 1, 4 ) ) );
        }};
        // for y-intercept manipulation challenges, one must be positive, one negative
        ArrayList<ArrayList<Integer>> yInterceptBins = new ArrayList<ArrayList<Integer>>() {{
            add( rangeToList( new IntegerRange( -9, -1 ) ) );
            add( rangeToList( new IntegerRange( 1, 4 ) ) );
        }};
        // for point-slope form, one of each manipulation mode
        ArrayList<ManipulationMode> pointSlopeManipulationMode = new ArrayList<ManipulationMode>() {{
            add( ManipulationMode.POINT );
            add( ManipulationMode.SLOPE );
        }};

        // bin indices
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
        assert ( x1Bins.size() == y1Bins.size() );
        ArrayList<Integer> pointBinIndices = rangeToList( new IntegerRange( 0, x1Bins.size() - 1 ) );
        ArrayList<Integer> yInterceptBinIndices = rangeToList( new IntegerRange( 0, yInterceptBins.size() - 1 ) );

        int index; // random index

        // GTL, SI, slope
        {
            // slope: choose from remaining bins
            index = randomIndex( slopeBinIndices );
            ArrayList<Fraction> slopes = slopeBins.get( slopeBinIndices.get( index ) );
            slopeBinIndices.remove( index );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept: choose from any bin
            index = randomIndex( yInterceptBins );
            ArrayList<Integer> yIntercepts = yInterceptBins.get( index );
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
            ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
            index = randomIndex( indices );
            ArrayList<Fraction> slopes = slopeBins.get( index );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept: choose from remaining bins
            index = randomIndex( yInterceptBinIndices );
            ArrayList<Integer> yIntercepts = yInterceptBins.get( yInterceptBinIndices.get( index ) );
            yInterceptBinIndices.remove( index );
            index = randomIndex( yInterceptBins );
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

            int x1, y1;
            Fraction slope;
            if ( manipulationMode == ManipulationMode.SLOPE ) {

                // point: choose from any bin
                ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, x1Bins.size() - 1 ) );
                index = randomIndex( indices );
                ArrayList<Integer> x1Values = x1Bins.get( index );
                ArrayList<Integer> y1Values = x1Bins.get( index );
                index = randomIndex( x1Values );
                x1 = x1Values.get( index );
                x1Values.remove( index );
                index = randomIndex( y1Values );
                y1 = y1Values.get( index );
                y1Values.remove( index );

                // slope: choose from remaining bins
                index = randomIndex( slopeBinIndices );
                ArrayList<Fraction> slopes = slopeBins.get( slopeBinIndices.get( index ) );
                slopeBinIndices.remove( index );
                index = randomIndex( slopes );
                slope = slopes.get( index );
                slopes.remove( index );
            }
            else {
                // point: choose from remaining bins
                index = randomIndex( pointBinIndices );
                ArrayList<Integer> x1Values = x1Bins.get( pointBinIndices.get( index ) );
                ArrayList<Integer> y1Values = y1Bins.get( pointBinIndices.get( index ) );
                pointBinIndices.remove( index );
                index = randomIndex( x1Values );
                x1 = x1Values.get( index );
                x1Values.remove( index );
                index = randomIndex( y1Values );
                y1 = y1Values.get( index );
                y1Values.remove( index );

                // slope: choose from any bin
                ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
                index = randomIndex( indices );
                ArrayList<Fraction> slopes = slopeBins.get( index );
                index = randomIndex( slopes );
                slope = slopes.get( index );
                slopes.remove( index );
            }

            // challenge
            Line line = Line.createPointSlope( x1, y1, slope.numerator, slope.denominator );
            IChallenge challenge = new GTL_Challenge( line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // MTE, SI, slope
        {
            // slope: choose from remaining bins
            index = randomIndex( slopeBinIndices );
            ArrayList<Fraction> slopes = slopeBins.get( slopeBinIndices.get( index ) );
            slopeBinIndices.remove( index );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept: choose from any bin
            index = randomIndex( yInterceptBins );
            ArrayList<Integer> yIntercepts = yInterceptBins.get( index );
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
            ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
            index = randomIndex( indices );
            ArrayList<Fraction> slopes = slopeBins.get( index );
            index = randomIndex( slopes );
            final Fraction slope = slopes.get( index );
            slopes.remove( index );

            // y-intercept: choose from remaining bins
            index = randomIndex( yInterceptBinIndices );
            ArrayList<Integer> yIntercepts = yInterceptBins.get( yInterceptBinIndices.get( index ) );
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

            int x1, y1;
            Fraction slope;
            if ( manipulationMode == ManipulationMode.SLOPE ) {

                // point: choose from any bin
                ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, x1Bins.size() - 1 ) );
                index = randomIndex( indices );
                ArrayList<Integer> x1Values = x1Bins.get( index );
                ArrayList<Integer> y1Values = x1Bins.get( index );
                index = randomIndex( x1Values );
                x1 = x1Values.get( index );
                x1Values.remove( index );
                index = randomIndex( y1Values );
                y1 = y1Values.get( index );
                y1Values.remove( index );

                // slope: choose from remaining bins
                index = randomIndex( slopeBinIndices );
                ArrayList<Fraction> slopes = slopeBins.get( slopeBinIndices.get( index ) );
                slopeBinIndices.remove( index );
                index = randomIndex( slopes );
                slope = slopes.get( index );
                slopes.remove( index );
            }
            else {
                // point: choose from remaining bins
                index = randomIndex( pointBinIndices );
                ArrayList<Integer> x1Values = x1Bins.get( pointBinIndices.get( index ) );
                ArrayList<Integer> y1Values = y1Bins.get( pointBinIndices.get( index ) );
                pointBinIndices.remove( index );
                index = randomIndex( x1Values );
                x1 = x1Values.get( index );
                x1Values.remove( index );
                index = randomIndex( y1Values );
                y1 = y1Values.get( index );
                y1Values.remove( index );

                // slope: choose from any bin
                ArrayList<Integer> indices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
                index = randomIndex( indices );
                ArrayList<Fraction> slopes = slopeBins.get( index );
                index = randomIndex( slopes );
                slope = slopes.get( index );
                slopes.remove( index );
            }

            // challenge
            Line line = Line.createPointSlope( x1, y1, slope.numerator, slope.denominator );
            IChallenge challenge = new MTE_Challenge( line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // shuffle and return
        return shuffle( challenges );
    }

    // Coverts an integer range to a list of values that are in that range.
    private static ArrayList<Integer> rangeToList( IntegerRange range ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int i = range.getMin(); i <= range.getMax(); i++ ) {
            list.add( i );
        }
        assert ( list.size() > 0 );
        return list;
    }

    // Gets a random index for a specified list.
    private int randomIndex( List list ) {
        return random.nextInt( list.size() );
    }

    // Shuffles a list of challenges.
    private ArrayList<IChallenge> shuffle( ArrayList<IChallenge> list ) {
        ArrayList<IChallenge> shuffledList = new ArrayList<IChallenge>();
        while ( list.size() != 0 ) {
            int index = randomIndex( list );
            shuffledList.add( list.get( index ) );
            list.remove( index );
        }
        return shuffledList;
    }
}
