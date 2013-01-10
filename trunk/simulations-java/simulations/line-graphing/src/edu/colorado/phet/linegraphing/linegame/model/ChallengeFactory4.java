// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=4, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory4 extends ChallengeFactory {

    public ChallengeFactory4() {
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

        // equation form for 3rd challenge of each type
        ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
            add( EquationForm.SLOPE_INTERCEPT );
            add( EquationForm.POINT_SLOPE );
        }};

        // bin indices
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeBins.size() - 1 ) );
        ArrayList<Integer> yInterceptBinIndices = rangeToList( new IntegerRange( 0, yInterceptBins.size() - 1 ) );

        // MTE, SI, slope & intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // first required y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "1 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // MTE, PS, point & slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // first required slope
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new MTE_Challenge( "1 of 3 required slopes",
                                               line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // MTE, SI or PS (random choice)
        {
            if ( pickEquationForm( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // MTE, SI, slope & intercept
                Fraction slope = pickFraction( slopeBins ); // unique slope
                int yIntercept = pickInteger( yInterceptBins ); // unique y-intercept
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new MTE_Challenge( "random choice of slope-intercept",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // MTE, PS, point & slope
                Fraction slope = pickFraction( slopeBins ); // unique slope
                Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new MTE_Challenge( "random choice of point-slope",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // GTL, SI, slope & intercept
        {
            Fraction slope = pickFraction( slopeBins ); // unique slope
            int yIntercept = pickInteger( yInterceptBins, yInterceptBinIndices ); // second required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "2 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // GTL, PS, point & slope
        {
            Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // second required slope, unique
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new GTL_Challenge( "2 of 3 required slopes",
                                               line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        /*
         * GTL, SI or PS (random choice), 2 points.
         * Choose y-intercept or point such that (x2,y2) is off the graph, so that user is forced to invert the slope.
         */
        {
            if ( pickEquationForm( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // GTL, SI, 2 points
                Fraction slope = pickFraction( slopeBins, slopeBinIndices ); // third required slope, unique
                int yIntercept = (int) pickPointForInvertedSlope( slope, xRange, yRange ).getY(); // random y-intercept, not necessarily unique
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new GTL_Challenge( "slope-intercept because MTE uses point-slope, 3 of 3 required slopes, force slope inversion",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.TWO_POINTS, xRange, yRange ) );
            }
            else {
                // GTL, PS, 2 points
                Fraction slope = pickFraction( slopeBins ); // third required slope, unique
                Point2D point = pickPointForInvertedSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new GTL_Challenge( "point-slope because MTE uses slope-intercept, 3 of 3 required slopes, force slope inversion",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.TWO_POINTS, xRange, yRange ) );
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
