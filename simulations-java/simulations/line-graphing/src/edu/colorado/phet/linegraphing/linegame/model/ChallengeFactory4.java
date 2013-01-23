// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=4, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory4 extends ChallengeFactory {

    /**
     * Creates challenges for this game level.
     *
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for slope manipulation challenges, 1 slope must come from each list
        ArrayList<ArrayList<Fraction>> slopeLists = ChallengeFactory2.createSlopeLists();
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeLists.size() - 1 ) );

        // for y-intercept manipulation challenges, one must be positive, one negative
        final IntegerRange yInterceptRange = new IntegerRange( -10, 10 );
        ArrayList<ArrayList<Integer>> yInterceptLists = new ArrayList<ArrayList<Integer>>() {{
            assert ( yInterceptRange.getMin() < 0 && yInterceptRange.getMax() > 0 );
            add( rangeToList( new IntegerRange( yInterceptRange.getMin(), -1 ) ) );
            add( rangeToList( new IntegerRange( 1, yInterceptRange.getMax() ) ) );
        }};
        ArrayList<Integer> yInterceptListIndices = rangeToList( new IntegerRange( 0, yInterceptLists.size() - 1 ) );

        // equation form for 3rd challenge of each type
        ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
            add( EquationForm.SLOPE_INTERCEPT );
            add( EquationForm.POINT_SLOPE );
        }};

        // Make-the-Equation, slope-intercept form, slope and intercept variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // first required y-intercept
            challenges.add( new MakeTheEquation( "1 of 2 required y-intercepts",
                                                 Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                                 EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // Make-the-Equation, point-slope form, point and slope variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeBinIndices ); // first required slope
            final Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            challenges.add( new MakeTheEquation( "1 of 3 required slopes",
                                                 Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                                 EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // Make-the-Equation, slope-intercept or point-slope form (random choice)
        {
            if ( equationFormChooser.choose( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // Make-the-Equation, slope-intercept form, slope and intercept variable
                final Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                final int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
                challenges.add( new MakeTheEquation( "random choice of slope-intercept",
                                                     Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                                     EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // Make-the-Equation, point-slope form, point and slope variable
                final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeBinIndices ); // second required slope, unique
                final Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                challenges.add( new MakeTheEquation( "2 of 2 required slopes, random choice of point-slope",
                                                     Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                                     EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // Graph-the-Line, slope-intercept form, slope and intercept variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // second required y-intercept, unique
            challenges.add( new GraphTheLine( "2 of 2 required y-intercepts",
                                              Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                              EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // Graph-the-Line, point-slope form, point and slope variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeBinIndices ); // third required slope, unique
            final Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            challenges.add( new GraphTheLine( "3 of 3 required slopes",
                                              Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                              EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        /*
         * Graph-the-Line, slope-intercept or point-slope form (random choice), 2 points.
         * Choose y-intercept or point such that (x2,y2) is off the graph, so that user is forced to invert the slope.
         */
        {
            // choose a positive fractional slope
            final ArrayList<Fraction> positiveSlopes = ChallengeFactory2.createPositiveFractionalSlopes();
            positiveSlopes.add( new Fraction( 2, 1 ) );
            positiveSlopes.add( new Fraction( 3, 1 ) );
            positiveSlopes.add( new Fraction( 4, 1 ) );
            positiveSlopes.add( new Fraction( 5, 1 ) );
            Fraction slope = fractionChooser.choose( positiveSlopes );

            Point2D point = choosePointForSlopeInversion( slope, xRange, yRange ); // random point, not necessarily unique

            if ( equationFormChooser.choose( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // Graph-the-Line, slope-intercept, 2 points variable
                challenges.add( new GraphTheLine( "slope-intercept because Make-the-Equation uses point-slope, force slope inversion",
                                                  Line.createSlopeIntercept( slope.numerator, slope.denominator, point.getY() ),
                                                  EquationForm.SLOPE_INTERCEPT, ManipulationMode.TWO_POINTS, xRange, yRange ) );
            }
            else {
                // Graph-the-Line, point-slope, 2 points variable
                challenges.add( new GraphTheLine( "point-slope because Make-the-Equation uses slope-intercept, force slope inversion",
                                                  Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                                  EquationForm.POINT_SLOPE, ManipulationMode.TWO_POINTS, xRange, yRange ) );
            }
        }

        // shuffle and return
        Collections.shuffle( challenges );
        return challenges;
    }
}
