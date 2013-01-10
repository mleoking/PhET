// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=2, as specified in the design document.
 * Slope and intercept are uniquely chosen.
 * Point (x1,y1) is not unique, but is chosen such that slope indicator is on the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory2 extends ChallengeFactory {

    public ChallengeFactory2() {
        super();
    }

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for slope manipulation challenges, 1 slope must come from each bin
        ArrayList<ArrayList<Fraction>> slopeBins = new ArrayList<ArrayList<Fraction>>() {{
            add( new ArrayList<Fraction>() {{
                // positive and negative integers
                add( new Fraction( 1, 1 ) );
                add( new Fraction( 2, 1 ) );
                add( new Fraction( 3, 1 ) );
                add( new Fraction( 4, 1 ) );
                add( new Fraction( 5, 1 ) );
                add( new Fraction( -1, 1 ) );
                add( new Fraction( -2, 1 ) );
                add( new Fraction( -3, 1 ) );
                add( new Fraction( -4, 1 ) );
                add( new Fraction( -5, 1 ) );
            }} );
            add( new ArrayList<Fraction>() {{
                // positive fractions
                add( new Fraction( 1, 4 ) );
                add( new Fraction( 1, 5 ) );
                add( new Fraction( 1, 6 ) );
                add( new Fraction( 1, 7 ) );
                add( new Fraction( 2, 5 ) );
                add( new Fraction( 3, 5 ) );
                add( new Fraction( 2, 7 ) );
                add( new Fraction( 3, 7 ) );
                add( new Fraction( 4, 7 ) );
                add( new Fraction( 5, 2 ) );
                add( new Fraction( 3, 2 ) );
                add( new Fraction( 7, 2 ) );
                add( new Fraction( 7, 3 ) );
                add( new Fraction( 7, 4 ) );
            }} );
            add( new ArrayList<Fraction>() {{
                // negative fractions
                add( new Fraction( -1, 2 ) );
                add( new Fraction( -1, 3 ) );
                add( new Fraction( -1, 4 ) );
                add( new Fraction( -1, 5 ) );
                add( new Fraction( -2, 3 ) );
                add( new Fraction( -3, 4 ) );
                add( new Fraction( -2, 5 ) );
                add( new Fraction( -3, 5 ) );
                add( new Fraction( -4, 5 ) );
                add( new Fraction( -3, 2 ) );
                add( new Fraction( -4, 3 ) );
                add( new Fraction( -5, 2 ) );
                add( new Fraction( -5, 3 ) );
                add( new Fraction( -5, 4 ) );
            }} );
        }};

        // for y-intercept manipulation challenges, one must be positive, one negative
        final IntegerRange yInterceptRange = new IntegerRange( -10, 10 );
        ArrayList<ArrayList<Integer>> yInterceptBins = new ArrayList<ArrayList<Integer>>() {{
            assert( yInterceptRange.getMin() < 0 && yInterceptRange.getMax() > 0 );
            add( rangeToList( new IntegerRange( yInterceptRange.getMin(), -1 ) ) );
            add( rangeToList( new IntegerRange( 1, yInterceptRange.getMax() ) ) );
        }};

        // for point-slope form, one of each manipulation mode
        ArrayList<ManipulationMode> pointSlopeManipulationModes = new ArrayList<ManipulationMode>() {{
            add( ManipulationMode.POINT );
            add( ManipulationMode.SLOPE );
        }};

        // bin indices
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
        ArrayList<Integer> yInterceptBinIndices = rangeToList( new IntegerRange( 0, yInterceptBins.size() - 1 ) );

        // GTL, SI, slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // first required slope, unique
            int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "1 of 3 required slopes",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // GTL, SI, intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // first required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "1 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // MTE, SI, slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices );  // second required slope, unique
            int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "2 of 3 requires slopes",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // MTE, SI, intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // second required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "2 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // GTL, PS, point or slope
        {
            // manipulation mode
            final ManipulationMode manipulationMode = pickManipulationMode( pointSlopeManipulationModes );

            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                slope = pickFraction( slopeBins, slopeBinIndices ); // third required slope, unique
                description = "random choice of slope manipulation, 3 of 3 required slopes";
            }
            else {
                slope = pickFraction( slopeBins ); // unique slope
                description = "random choice of point manipulation";
            }
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique

            // challenge
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            Challenge challenge = new GTL_Challenge( description, line, EquationForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // MTE, PS, point or slope
        {
            // manipulation mode
            final ManipulationMode manipulationMode = pickManipulationMode( pointSlopeManipulationModes );

            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                slope = pickFraction( slopeBins, slopeBinIndices ); // third required slope, unique
                description = "slope manipulation because GTL uses point, 3 of 3 required slopes";
            }
            else {
                slope = pickFraction( slopeBins ); // unique slope
                description = "point manipulation because GTL uses slope";
            }
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique

            // challenge
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            Challenge challenge = new MTE_Challenge( description, line, EquationForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
