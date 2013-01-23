// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

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
public class ChallengeFactory2 extends ChallengeFactory {

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for slope manipulation challenges, 1 slope must come from each list
        ArrayList<ArrayList<Fraction>> slopeLists = createSlopeLists();
        ArrayList<Integer> slopeListIndices = rangeToList( new IntegerRange( 0, slopeLists.size() - 1 ) );

        // for y-intercept manipulation challenges, one must be positive, one negative
        final IntegerRange yInterceptRange = new IntegerRange( -10, 10 );
        ArrayList<ArrayList<Integer>> yInterceptLists = new ArrayList<ArrayList<Integer>>() {{
            assert( yInterceptRange.getMin() < 0 && yInterceptRange.getMax() > 0 );
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
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // first required slope, unique
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
            final Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices );  // second required slope, unique
            final int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
            challenges.add( new MakeTheEquation( "2 of 3 requires slopes",
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

            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // third required slope, unique
                description = "random choice of slope manipulation, 3 of 3 required slopes";
            }
            else {
                slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                description = "random choice of point manipulation";
            }
            final Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique

            // challenge
            challenges.add( new GraphTheLine( description,
                                              Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                              EquationForm.POINT_SLOPE, manipulationMode, xRange, yRange ) );
        }

        // Make-the-Equation, point-slope form, point or slope variable (whichever was not variable above)
        {
            // manipulation mode
            final ManipulationMode manipulationMode = manipulationModeChooser.choose( pointSlopeManipulationModes );

            Fraction slope;
            String description;
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                slope = fractionChooser.chooseFromLists( slopeLists, slopeListIndices ); // third required slope, unique
                description = "slope manipulation because Graph-the-Line uses point, 3 of 3 required slopes";
            }
            else {
                slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                description = "point manipulation because Graph-the-Line uses slope";
            }
            Point2D point = choosePointForSlope( slope, xRange, yRange ); // random point, not necessarily unique

            // challenge
            challenges.add( new MakeTheEquation( description,
                                                 Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                                 EquationForm.POINT_SLOPE, manipulationMode, xRange, yRange ) );
        }

        // shuffle and return
        Collections.shuffle( challenges );
        return challenges;
    }

    // Creates the set of positive fractional slopes that are identified in the design document.
    public static ArrayList<Fraction> createPositiveFractionalSlopes() {
        return new ArrayList<Fraction>() {{
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
        }};
    }

    // Creates the 3 sets of slopes that are identified in the design document.
    public static ArrayList<ArrayList<Fraction>> createSlopeLists() {
        return new ArrayList<ArrayList<Fraction>>() {{
            // positive and negative integers
            add( new ArrayList<Fraction>() {{
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
            // positive fractions
            add( createPositiveFractionalSlopes() );
            // negative fractions
            add( new ArrayList<Fraction>() {{
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
    }
}
