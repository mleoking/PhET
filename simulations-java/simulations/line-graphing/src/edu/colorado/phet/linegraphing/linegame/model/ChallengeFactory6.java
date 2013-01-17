// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=6, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory6 extends ChallengeFactory {

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        ArrayList<Integer> yIntercepts = rangeToList( new IntegerRange( -10, 10 ) );

        // Place-the-Point, slope-intercept form, slope=0 (horizontal line), slope and intercept variable
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            challenges.add( new PlaceThePoints( "slope=0",
                                                Line.createSlopeIntercept( 0, 1, yIntercept ),
                                                EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // 2 Place-the-Point challenges (1 slope-intercept form, 1 point-slope form)
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
                                                    EquationForm.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // 3 Graph-the-Line challenges with mismatched representations (eg, point-slope equation with slope-intercept manipulators)
        {
            // we'll pick 3 from here
            ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
                add( EquationForm.SLOPE_INTERCEPT );
                add( EquationForm.SLOPE_INTERCEPT );
                add( EquationForm.POINT_SLOPE );
                add( EquationForm.POINT_SLOPE );
            }};
            assert ( equationForms.size() == 4 );

            for ( int i = 0; i < 3; i++ ) {

                final EquationForm equationForm = equationFormChooser.choose( equationForms );

                // random points
                final IntegerRange range = new IntegerRange( -7, 7 );
                final ArrayList<Integer> xList = rangeToList( range );
                final ArrayList<Integer> yList = rangeToList( range );
                final int x1 = 0; // y-intercept must be an integer since we're mismatching representations
                final int y1 = integerChooser.choose( yList );
                int x2 = integerChooser.choose( xList );
                if ( x2 == x1 ) {
                    x2 = integerChooser.choose( xList ); // prevent undefined slope
                }
                final int y2 = integerChooser.choose( yList );

                // challenge, with mismatched representations
                final Line line = new Line( x1, y1, x2, y2, Color.BLACK );
                if ( equationForm == EquationForm.SLOPE_INTERCEPT ) {
                    challenges.add( new GraphTheLine( "slope-intercept, Graph-the-Line " + i, line, equationForm, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
                }
                else {
                    challenges.add( new GraphTheLine( "point-slope, Graph-the-Line " + i, line, equationForm, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
                }
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
