// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Hard-coded challenges, used in initial development of the game.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactoryHardCoded {

    private static final String DESCRIPTION = "hardcoded";

    private ChallengeFactoryHardCoded() {}

    /**
     * Creates hard-coded challenges, for development testing.
     * @param level the game level
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public static ArrayList<Challenge> createChallenges( int level, IntegerRange xRange, IntegerRange yRange ) {
        ArrayList<Challenge> challenges = new ArrayList<Challenge>();
        switch( level ) {
            case 1:
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createSlopeIntercept( 1, 1, -2 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createSlopeIntercept( 5, 1, 1 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createSlopeIntercept( 4, 2, 3 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createSlopeIntercept( 3, 3, -3 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.TWO_POINTS, xRange, yRange ) );
                // mismatched equation form and graph manipulators
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createSlopeIntercept( 3, 3, -3 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
                break;
            case 2:
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createPointSlope( 2, 1, 1, 2 ), EquationForm.POINT_SLOPE, ManipulationMode.SLOPE, xRange, yRange ) );
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createPointSlope( 1, -3, 1, 3 ), EquationForm.POINT_SLOPE, ManipulationMode.POINT, xRange, yRange ) );
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createPointSlope( -2, 1, -4, 3 ), EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createPointSlope( 5, 4, 3, 2 ), EquationForm.POINT_SLOPE, ManipulationMode.TWO_POINTS, xRange, yRange ) );
                // mismatched equation form and graph manipulators
                challenges.add( new GraphTheLine( DESCRIPTION, Line.createSlopeIntercept( 4, 2, 3 ), EquationForm.POINT_SLOPE, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
                break;
            case 3:
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createSlopeIntercept( 1, 1, -2 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.INTERCEPT, xRange, yRange ) );
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createSlopeIntercept( 5, 1, 1 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE, xRange, yRange ) );
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createSlopeIntercept( 4, 2, 3 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createSlopeIntercept( 3, 3, -3 ), EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
                break;
            case 4:
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createPointSlope( 2, 1, 1, 2 ), EquationForm.POINT_SLOPE, ManipulationMode.SLOPE, xRange, yRange ) );
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createPointSlope( 1, -3, 1, 3 ), EquationForm.POINT_SLOPE, ManipulationMode.POINT, xRange, yRange ) );
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createPointSlope( -2, 1, -4, 3 ), EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
                challenges.add( new MakeTheEquation( DESCRIPTION, Line.createPointSlope( 5, 4, 3, 2 ), EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
                break;
            case 5:
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createSlopeIntercept( 1, 1, -2 ), EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createSlopeIntercept( 5, 1, 1 ), EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createSlopeIntercept( 4, 2, 3 ), EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createSlopeIntercept( 3, 3, -3 ), EquationForm.SLOPE_INTERCEPT, xRange, yRange ) );
                break;
            case 6:
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createPointSlope( 2, 1, 1, 2 ), EquationForm.POINT_SLOPE, xRange, yRange ) );
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createPointSlope( 1, -3, 1, 3 ), EquationForm.POINT_SLOPE, xRange, yRange ) );
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createPointSlope( -2, 1, -4, 3 ), EquationForm.POINT_SLOPE, xRange, yRange ) );
                challenges.add( new PlaceThePoints( DESCRIPTION, Line.createPointSlope( 5, 4, 3, 2 ), EquationForm.POINT_SLOPE, xRange, yRange ) );
                break;
            default:
                throw new IllegalArgumentException( "unsupported level: " + level );
        }
        return challenges;
    }
}
