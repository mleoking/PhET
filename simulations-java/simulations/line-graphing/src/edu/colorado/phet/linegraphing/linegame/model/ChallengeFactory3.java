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

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for slope manipulation challenges, 1 slope must come from each list
        ArrayList<ArrayList<Fraction>> slopeLists = ChallengeFactory2.createSlopeLists(); // same slopes as level 2
        ArrayList<Integer> slopeListIndices = rangeToList( new IntegerRange( 0, slopeLists.size() - 1 ) );

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

        // Graph-the-Line, slope-intercept form, slope and intercept variable
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // first required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GraphTheLine( "1 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // Graph-the-Line, point-slope form, point and slope variable
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // first required slope, unique
            Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new GraphTheLine( "1 of 3 required slopes",
                                               line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // Graph-the-Line, slope-intercept or point-slope form (random choice), 2 variables
        {
            if ( equationFormChooser.choose( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // Graph-the-Line, slope-intercept form, slope and intercept variable
                Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // second required slope, unique
                int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new GraphTheLine( "random choice of slope-intercept, 2 of 2 required slopes",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // Graph-the-Line, point-slope form, point and slope variable
                Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new GraphTheLine( "random choice of point-slope",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // Make-the-Equation, slope-intercept form, slope and intercept variable
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // second required y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MakeTheEquation( "2 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // Make-the-Equation, point-slope form, point and slope variable
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // 3rd required slope, unique
            Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new MakeTheEquation( "3 of 3 required slopes",
                                               line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // Make-the-Equation, slope-intercept or point-slope form (whichever wasn't chosen above), 2 variables
        {
            if ( equationFormChooser.choose( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // Make-the-Equation, slope-intercept, slope and intercept variable
                Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new MakeTheEquation( "slope-intercept because Graph-the-Line uses point-slope",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // Make-the-Equation, point-slope form, point and slope variable
                Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new MakeTheEquation( "point-slope because Graph-the-Line uses slope-intercept",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
