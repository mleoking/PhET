// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=1, as specified in the design document.
 * Slope, points and intercept are all uniquely chosen.
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
                assert( x1Range.getMax() > 0 && y1Range.getMax() > 0 );
                for ( int x = 1; x < x1Range.getMax(); x++ ) {
                    for ( int y = 1; y < y1Range.getMax(); y++ ) {
                        add( new Point2D.Double( x, y ) );
                    }
                }
            }} );
            add( new ArrayList<Point2D>() {{
                // Quadrant 3
                assert( x1Range.getMin() < 0 && y1Range.getMin() < 0 );
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
        ArrayList<Integer> pointBinIndices = rangeToList( new IntegerRange( 0, pointBins.size() - 1 ) );
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
        ArrayList<Integer> yInterceptBinIndices = rangeToList( new IntegerRange( 0, yInterceptBins.size() - 1 ) );

        // GTL, SI, slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices );
            int yIntercept = pickInteger( yInterceptBins );
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // GTL, SI, intercept
        {
            Fraction slope = pickFraction( slopeBins );
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices );
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // GTL, PS, point or slope
        {
            // manipulation mode
            final ManipulationMode manipulationMode = pickManipulationMode( pointSlopeManipulationModes );

            Point2D point;
            Fraction slope;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                point = pickPoint( pointBins );
                slope = pickFraction( slopeBins, slopeBinIndices );
            }
            else {
                point = pickPoint( pointBins, pointBinIndices );
                slope = pickFraction( slopeBins );
            }

            // challenge
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            IChallenge challenge = new GTL_Challenge( line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // MTE, SI, slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices );
            int yIntercept = pickInteger( yInterceptBins );
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // GTL, SI, intercept
        {
            Fraction slope = pickFraction( slopeBins );
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices );
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( line, LineForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // MTE, PS, point or slope
        {
            // manipulation mode
            final ManipulationMode manipulationMode = pickManipulationMode( pointSlopeManipulationModes );

            Point2D point;
            Fraction slope;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                point = pickPoint( pointBins );
                slope = pickFraction( slopeBins, slopeBinIndices );
            }
            else {
                point = pickPoint( pointBins, pointBinIndices );
                slope = pickFraction( slopeBins );
            }

            // challenge
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            IChallenge challenge = new MTE_Challenge( line, LineForm.POINT_SLOPE, manipulationMode, xRange, yRange );
            challenges.add( challenge );
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
