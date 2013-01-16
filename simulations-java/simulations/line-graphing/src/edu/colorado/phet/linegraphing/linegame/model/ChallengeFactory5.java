// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=5, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory5 extends ChallengeFactory {

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for y-intercept manipulation challenges
        ArrayList<Integer> yIntercepts = rangeToList( new IntegerRange( -10, 10 ) );

        // Make-the-Equation, slope-intercept form, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            challenges.add( new MakeTheEquation( "slope=0",
                                                 Line.createSlopeIntercept( 0, 1, yIntercept ),
                                                 EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // Graph-the-Line, slope-intercept form, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            challenges.add( new GraphTheLine( "slope=0",
                                              Line.createSlopeIntercept( 0, 1, yIntercept ),
                                              EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // Graph-the-Line, slope-intercept or point-slope form (random choice), 2 variables
        {
            // randomly choose equation form
            ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
                add( EquationForm.SLOPE_INTERCEPT );
                add( EquationForm.POINT_SLOPE );
            }};
            final EquationForm equationForm = equationFormChooser.choose( equationForms );

            // random points
            IntegerRange range = new IntegerRange( -5, 5 );
            final ArrayList<Integer> xList = rangeToList( range );
            final ArrayList<Integer> yList = rangeToList( range );
            final int x1 = ( equationForm == EquationForm.SLOPE_INTERCEPT ) ? 0 : integerChooser.choose( xList );
            final int y1 = integerChooser.choose( yList );
            int x2 = integerChooser.choose( xList );
            if ( x2 == x1 ) {
                x2 = integerChooser.choose( xList ); // prevent undefined slope
            }
            final int y2 = integerChooser.choose( yList );

            // challenge
            final Line line = new Line( x1, y1, x2, y2, Color.BLACK );
            if ( equationForm == EquationForm.SLOPE_INTERCEPT ) {
                challenges.add( new GraphTheLine( "random choice of slope-intercept, points in [-5,5]",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                challenges.add( new GraphTheLine( "random choice of point-slope, points in [-5,5]",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // Make-the-Equation, slope-intercept or point-slope form (random choice), 2 variables, random slope with exclusions
        {
            // randomly choose equation form
            final ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
                add( EquationForm.SLOPE_INTERCEPT );
                add( EquationForm.POINT_SLOPE );
            }};
            final EquationForm equationForm = equationFormChooser.choose( equationForms );

            // exclude slopes whose simplified absolute value matches these
            ArrayList<Fraction> excludedSlopes = new ArrayList<Fraction>() {{
                add( new Fraction( 1, 1 ) );
                add( new Fraction( 2, 1 ) );
                add( new Fraction( 1, 2 ) );
                add( new Fraction( 1, 3 ) );
                add( new Fraction( 1, 4 ) );
                add( new Fraction( 2, 3 ) );
            }};

            // choose rise and run such that they don't make an undefined or excluded slope
            int rise, run;
            {
                final ArrayList<Integer> riseList = rangeToList( yRange );
                final ArrayList<Integer> runList = rangeToList( xRange );
                rise = integerChooser.choose( riseList );
                run = integerChooser.choose( runList );
                boolean excluded = true;
                while ( excluded && runList.size() > 0 ) {
                    excluded = false;
                    // is this an excluded or undefined slope?
                    for ( Fraction slope : excludedSlopes ) {
                        if ( run == 0 || slope.toDecimal() == new Fraction( rise, run ).toDecimal() ) {
                            excluded = true;
                            run = integerChooser.choose( runList ); // choose a new run, and remove it from runList
                            break;
                        }
                    }
                }
                if ( excluded ) {
                    run = 5; // a run that's not in excludedSlopes
                }
            }
            assert( run != 0 );

            // points
            final Point2D p = choosePointForSlope( new Fraction( rise, run ), xRange, yRange );
            final int x1 = ( equationForm == EquationForm.SLOPE_INTERCEPT ) ? 0 : (int) p.getX();
            final int y1 = (int) p.getY();
            final int x2 = x1 + run;
            final int y2 = y1 + rise;

            // challenge
            final Line line = new Line( x1, y1, x2, y2, Color.BLACK );
            if ( equationForm == EquationForm.SLOPE_INTERCEPT ) {
                challenges.add( new GraphTheLine( "random choice of slope-intercept, some excluded slopes",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                challenges.add( new GraphTheLine( "random choice of point-slope, some excluded slopes",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // 2 Place-the-Point challenges
        {
            // ranges of x1,y1,rise,run limited to [-5,5]
            IntegerRange range = new IntegerRange( -5, 5 );
            ArrayList<Integer> xList = rangeToList( range );
            ArrayList<Integer> yList = rangeToList( range );
            ArrayList<Integer> riseList = rangeToList( range );
            ArrayList<Integer> runList = rangeToList( range );

            // slope-intercept form, slope and intercept variable
            {
                final int x1 = integerChooser.choose( xList );
                final int y1 = integerChooser.choose( yList );
                final int rise = integerChooser.choose( riseList );
                int run = integerChooser.choose( runList );
                if ( run == 0 ) {
                    // prevent undefined slope
                    run = integerChooser.choose( runList );
                }
                challenges.add( new PlaceThePoints( "slope-intercept, random points",
                                                    new Line( x1, y1, x1 + run, y1 + rise, Color.BLACK ),
                                                    EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
            }

            // point-slope form, point and slope variable
            {
                final int x1 = integerChooser.choose( xList );
                final int y1 = integerChooser.choose( yList );
                final int rise = integerChooser.choose( riseList );
                int run = integerChooser.choose( runList );
                if ( run == 0 ) {
                    // prevent undefined slope
                    run = integerChooser.choose( runList );
                }
                challenges.add( new PlaceThePoints( "point-slope, random points",
                                                    new Line( x1, y1, x1 + run, y1 + rise, Color.BLACK ),
                                                    EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
