// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

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

    public ChallengeFactory5() {
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

        // for y-intercept manipulation challenges, one must be positive, one negative
        ArrayList<Integer> yIntercepts = rangeToList( new IntegerRange( -10, 10 ) );

        // equation form
        ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
            add( EquationForm.SLOPE_INTERCEPT );
            add( EquationForm.POINT_SLOPE );
        }};

        // random choosers
        final RandomChooser<Fraction> fractionChooser = new RandomChooser<Fraction>();
        final RandomChooser<Integer> integerChooser = new RandomChooser<Integer>();
        final RandomChooser<EquationForm> equationFormChooser = new RandomChooser<EquationForm>();

        // MTE, SI, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            Line line = Line.createSlopeIntercept( 0, 1, yIntercept );
            challenges.add( new MTE_Challenge( "slope=0",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // GTL, SI, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            Line line = Line.createSlopeIntercept( 0, 1, yIntercept );
            challenges.add( new GTL_Challenge( "slope=0",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
