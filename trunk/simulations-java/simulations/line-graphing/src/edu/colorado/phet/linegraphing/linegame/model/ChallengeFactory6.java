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

    public ChallengeFactory6() {
        super();
    }

    /**
     * Creates challenges for this game level.
     *
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        ArrayList<Integer> yIntercepts = rangeToList( new IntegerRange( -10, 10 ) );

        // PTP, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            Line line = Line.createSlopeIntercept( 0, 1, yIntercept );
            challenges.add( new PTP_Challenge( "slope=0", line, EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // 2 PTP challenges (1 SI, 1 PS)
        {
            // ranges of x1,y1,rise,run limited to [-5,5]
            IntegerRange range = new IntegerRange( -5, 5 );
            ArrayList<Integer> xList = rangeToList( range );
            ArrayList<Integer> yList = rangeToList( range );
            ArrayList<Integer> riseList = rangeToList( range );
            ArrayList<Integer> runList = rangeToList( range );

            // SI
            {
                final int x1 = integerChooser.choose( xList );
                final int y1 = integerChooser.choose( yList );
                final int rise = integerChooser.choose( riseList );
                int run = integerChooser.choose( runList );
                if ( run == 0 ) {
                    // prevent undefined slope
                    run = integerChooser.choose( runList );
                }
                final Line line = new Line( x1, y1, x1 + run, y1 + rise, Color.BLACK );
                challenges.add( new PTP_Challenge( "slope-intercept, random points", line, EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
            }

            // PS
            {
                final int x1 = integerChooser.choose( xList );
                final int y1 = integerChooser.choose( yList );
                final int rise = integerChooser.choose( riseList );
                int run = integerChooser.choose( runList );
                if ( run == 0 ) {
                    // prevent undefined slope
                    run = integerChooser.choose( runList );
                }
                final Line line = new Line( x1, y1, x1 + run, y1 + rise, Color.BLACK );
                challenges.add( new PTP_Challenge( "point-slope, random points", line, EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
            }
        }

        // 3 GTL challenges where line forms are different (eg, PS equation with SI manipulators)
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
                    challenges.add( new GTL_Challenge( "slope-intercept, GTL " + i, line, equationForm, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
                }
                else {
                    challenges.add( new GTL_Challenge( "point-slope, GTL " + i, line, equationForm, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
                }
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
