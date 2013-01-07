// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=1, as specified in the design document.
 * Slope, intercept, and point (x1,y1) are all uniquely chosen.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory1 extends ChallengeFactory {

    public ChallengeFactory1() {
        super();
    }

    public ArrayList<IChallenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<IChallenge> challenges = new ArrayList<IChallenge>();

        // for point manipulation challenges, (x1,y1) must be in Quadrant 1 (both coordinates positive) or Quadrant 3 (both coordinates negative)
        final IntegerRange x1Range = new IntegerRange( -9, 4 );
        final IntegerRange y1Range = new IntegerRange( -9, 4 );
        ArrayList<ArrayList<Point2D>> pointBins = new ArrayList<ArrayList<Point2D>>() {{
            add( new ArrayList<Point2D>() {{
                // Quadrant 1
                assert ( x1Range.getMax() > 0 && y1Range.getMax() > 0 );
                for ( int x = 1; x < x1Range.getMax(); x++ ) {
                    for ( int y = 1; y < y1Range.getMax(); y++ ) {
                        add( new Point2D.Double( x, y ) );
                    }
                }
            }} );
            add( new ArrayList<Point2D>() {{
                // Quadrant 3
                assert ( x1Range.getMin() < 0 && y1Range.getMin() < 0 );
                for ( int x = x1Range.getMin(); x < 0; x++ ) {
                    for ( int y = y1Range.getMin(); y < 0; y++ ) {
                        add( new Point2D.Double( x, y ) );
                    }
                }
            }} );
        }};

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

        // for y-intercept manipulation challenges, one must be positive, one negative
        final IntegerRange yInterceptRange = new IntegerRange( -9, 4 );
        ArrayList<ArrayList<Integer>> yInterceptBins = new ArrayList<ArrayList<Integer>>() {{
            assert ( yInterceptRange.getMin() < 0 && yInterceptRange.getMax() > 0 );
            add( rangeToList( new IntegerRange( yInterceptRange.getMin(), -1 ) ) );
            add( rangeToList( new IntegerRange( 1, yInterceptRange.getMax() ) ) );
        }};

        // for point-slope form, one of each manipulation mode
        ArrayList<ManipulationMode> pointSlopeManipulationModes = new ArrayList<ManipulationMode>() {{
            add( ManipulationMode.POINT );
            add( ManipulationMode.SLOPE );
        }};

        // bin indices
        ArrayList<Integer> pointBinIndices = rangeToList( new IntegerRange( 0, pointBins.size() - 1 ) );
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
        ArrayList<Integer> yInterceptBinIndices = rangeToList( new IntegerRange( 0, yInterceptBins.size() - 1 ) );

        // GTL, SI, slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // first required slope
            int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "1 of 3 required slopes",
                                               line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // GTL, SI, intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // first required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "1 of 2 required y-intercepts",
                                               line, LineForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // MTE, SI, slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // second required slope, unique
            int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "2 of 3 required slopes",
                                               line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // MTE, SI, intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // second required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "2 of 2 required y-intercepts",
                                               line, LineForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // GTL, PS, point or slope (random choice)
        {
            // manipulation mode
            final ManipulationMode manipulationMode = pickManipulationMode( pointSlopeManipulationModes );

            Point2D point;
            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                point = pickPoint( pointBins ); // unique point
                slope = pickFraction( slopeBins, slopeBinIndices ); // third required slope, unique
                description = "random choice to manipulate slope, 3 of 3 required slopes";
            }
            else {
                point = pickPoint( pointBins, pointBinIndices ); // first required point, unique
                slope = pickFraction( slopeBins ); // unique slope
                description = "random choice to manipulate point, 1 of 2 required points";
            }

            // challenge
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            IChallenge challenge = new GTL_Challenge( description, line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // MTE, PS, point or slope (whichever was not chosen above)
        {
            // manipulation mode
            final ManipulationMode manipulationMode = pickManipulationMode( pointSlopeManipulationModes );

            Point2D point;
            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                point = pickPoint( pointBins ); // unique point
                slope = pickFraction( slopeBins, slopeBinIndices ); // third required slope, unique
                description = "manipulate slope because GTL uses point, 3 of 3 required slopes";
            }
            else {
                point = pickPoint( pointBins, pointBinIndices ); // second required point, unique
                slope = pickFraction( slopeBins ); // unique slope
                description = "manipulate point because GTL uses slope, 2 of 2 required points";
            }

            // challenge
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            IChallenge challenge = new MTE_Challenge( description, line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
