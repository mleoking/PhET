// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=3, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory3 extends ChallengeFactory {

    public ChallengeFactory3() {
        super();
    }

    public ArrayList<IChallenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<IChallenge> challenges = new ArrayList<IChallenge>();

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

        // line form for 3rd challenge of each type
        ArrayList<LineForm> lineForms = new ArrayList<LineForm>() {{
            add( LineForm.SLOPE_INTERCEPT );
            add( LineForm.POINT_SLOPE );
        }};

        // bin indices
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
        ArrayList<Integer> yInterceptBinIndices = rangeToList( new IntegerRange( 0, yInterceptBins.size() - 1 ) );

        // GTL, SI, slope & intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // first required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "1 of 2 required y-intercepts",
                                               line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // GTL, PS, point & slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // first required slope, unique
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new GTL_Challenge( "1 of 3 required slopes",
                                               line, LineForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // GTL, SI or PS (random choice)
        {
            if ( pickLineForm( lineForms ) == LineForm.SLOPE_INTERCEPT ) {
                // GTL, SI, slope & intercept
                Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // second required slope, unique
                int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new GTL_Challenge( "random choice of slope-intercept, 2 of 2 required slopes",
                                                   line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // GTL, PS, point & slope
                Fraction slope = pickFraction( slopeBins ); // unique slope
                Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new GTL_Challenge( "random choice of point-slope",
                                                   line, LineForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // MTE, SI, slope & intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // second required y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "2 of 2 required y-intercepts",
                                               line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // MTE, PS, point & slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // 3rd required slope, unique
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new MTE_Challenge( "3 of 3 required slopes",
                                               line, LineForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // MTE, SI or PS (whichever wasn't chosen above)
        {
            if ( pickLineForm( lineForms ) == LineForm.SLOPE_INTERCEPT ) {
                // MTE, SI, slope & intercept
                Fraction slope = pickFraction( slopeBins ); // unique slope
                int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new MTE_Challenge( "slope-intercept because GTL uses point-slope",
                                                   line, LineForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // MTE, PS, point & slope
                Fraction slope = pickFraction( slopeBins ); // unique slope
                Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new MTE_Challenge( "point-slope because GTL uses slope-intercept",
                                                   line, LineForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
