// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

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

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for point manipulation challenges, (x1,y1) must be in Quadrant 1 (both coordinates positive) or Quadrant 3 (both coordinates negative)
        final IntegerRange x1Range = new IntegerRange( -9, 4 );
        final IntegerRange y1Range = new IntegerRange( -9, 4 );
        ArrayList<ArrayList<Point2D>> pointLists = new ArrayList<ArrayList<Point2D>>() {{
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
        ArrayList<Integer> pointListIndices = rangeToList( new IntegerRange( 0, pointLists.size() - 1 ) );

        // for slope manipulation challenges, 1 slope must come from each list
        ArrayList<ArrayList<Fraction>> slopeLists = new ArrayList<ArrayList<Fraction>>() {{
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
        ArrayList<Integer> slopeListIndices = rangeToList( new IntegerRange( 0, slopeLists.size() - 1 ) );

        // for y-intercept manipulation challenges, one must be positive, one negative
        final IntegerRange yInterceptRange = new IntegerRange( -6, 4 );
        ArrayList<ArrayList<Integer>> yInterceptLists = new ArrayList<ArrayList<Integer>>() {{
            assert ( yInterceptRange.getMin() < 0 && yInterceptRange.getMax() > 0 );
            add( rangeToList( new IntegerRange( yInterceptRange.getMin(), -1 ) ) );
            add( rangeToList( new IntegerRange( 1, yInterceptRange.getMax() ) ) );
        }};
        ArrayList<Integer> yInterceptListIndices = rangeToList( new IntegerRange( 0, yInterceptLists.size() - 1 ) );

        // for point-slope form, one of each manipulation mode
        ArrayList<ManipulationMode> pointSlopeManipulationModes = new ArrayList<ManipulationMode>() {{
            add( ManipulationMode.POINT );
            add( ManipulationMode.SLOPE );
        }};

        // Graph-the-Line, slope-intercept form, slope variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // first required slope
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
            challenges.add( new GraphTheLine( "1 of 3 required slopes",
                                              Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                              EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // Graph-the-Line, slope-intercept form, intercept variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // first required y-intercept, unique
            challenges.add( new GraphTheLine( "1 of 2 required y-intercepts",
                                              Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                              EquationForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // Make-the-Equation, slope-intercept form, slope variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // second required slope, unique
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
            challenges.add( new MakeTheEquation( "2 of 3 required slopes",
                                                 Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                                 EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
        }

        // Make-the-Equation, slope-intercept form, intercept variable
        {
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // second required y-intercept, unique
            challenges.add( new MakeTheEquation( "2 of 2 required y-intercepts",
                                                 Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept ),
                                                 EquationForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
        }

        // Graph-the-Line, point-slope form, point or slope variable (random choice)
        {
            // manipulation mode
            final ManipulationMode manipulationMode = manipulationModeChooser.choose( pointSlopeManipulationModes );

            Point2D point;
            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                point = pointChooser.chooseFromLists( pointLists ); // unique point
                slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // third required slope, unique
                description = "random choice to manipulate slope, 3 of 3 required slopes";
            }
            else {
                point = pointChooser.chooseFromLists( pointLists, pointListIndices ); // first required point, unique
                slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                description = "random choice to manipulate point, 1 of 2 required points";
            }

            // challenge
            challenges.add( new GraphTheLine( description,
                                              Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                              EquationForm.POINT_SLOPE, manipulationMode, xRange, yRange ) );
        }

        // Make-the-Equation, point-slope form, point or slope variable (whichever was not chosen above)
        {
            // manipulation mode
            final ManipulationMode manipulationMode = manipulationModeChooser.choose( pointSlopeManipulationModes );

            Point2D point;
            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                point = pointChooser.chooseFromLists( pointLists ); // unique point
                slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // third required slope, unique
                description = "manipulate slope because Graph-the-Line uses point, 3 of 3 required slopes";
            }
            else {
                point = pointChooser.chooseFromLists( pointLists, pointListIndices ); // second required point, unique
                slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                description = "manipulate point because Graph-the-Line uses slope, 2 of 2 required points";
            }

            // challenge
            challenges.add( new MakeTheEquation( description,
                                                 Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                                 EquationForm.POINT_SLOPE, manipulationMode, xRange, yRange ) );
        }

        // shuffle and return
        Collections.shuffle( challenges );
        return challenges;
    }
}
