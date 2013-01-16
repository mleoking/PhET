// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;

/**
 * Base class for challenge factories. These factories handle quasi-random creation of challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class ChallengeFactory {

    //TODO RandomChooser has this same field
    protected final Random random; // random number generator

    // random choosers
    protected final RandomChooser<Fraction> fractionChooser = new RandomChooser<Fraction>();
    protected final RandomChooser<Integer> integerChooser = new RandomChooser<Integer>();
    protected final RandomChooser<Point2D> pointChooser = new RandomChooser<Point2D>();
    protected final RandomChooser<ManipulationMode> manipulationModeChooser = new RandomChooser<ManipulationMode>();
    protected final RandomChooser<EquationForm> equationFormChooser = new RandomChooser<EquationForm>();

    protected ChallengeFactory() {
        this.random = new Random();
    }

    /**
     * Creates challenges for the factory's game level.
     *
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public abstract ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange );

    //TODO move this somewhere else?
    // Converts an integer range to a list of values that are in that range.
    public static ArrayList<Integer> rangeToList( IntegerRange range ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int i = range.getMin(); i <= range.getMax(); i++ ) {
            list.add( i );
        }
        assert ( list.size() > 0 );
        return list;
    }

    //TODO this is duplicated in RandomChooser
    // Gets a random index for a specified list.
    protected int randomIndex( List list ) {
        return random.nextInt( list.size() );
    }

    // Picks a point that keeps the slope indicator on the graph.
    protected Point2D choosePointForSlope( final Fraction slope, final IntegerRange graphXRange, final IntegerRange graphYRange ) {

        final int rise = slope.numerator;
        final int run = slope.denominator;

        // x coordinates
        IntegerRange xRange;
        if ( run >= 0 ) {
            xRange = new IntegerRange( graphXRange.getMin(), graphYRange.getMax() - run );
        }
        else {
            xRange = new IntegerRange( graphXRange.getMin() - run, graphYRange.getMax() );
        }
        ArrayList<Integer> xList = rangeToList( xRange );

        // y coordinates
        IntegerRange yRange;
        if ( rise >= 0 ) {
            yRange = new IntegerRange( graphYRange.getMin(), graphYRange.getMax() - rise );
        }
        else {
            yRange = new IntegerRange( graphYRange.getMin() - rise, graphYRange.getMax() );
        }
        ArrayList<Integer> yList = rangeToList( yRange );

        // random point
        final int x = xList.get( randomIndex( xList ) );
        final int y = yList.get( randomIndex( yList ) );
        return new Point2D.Double( x, y );
    }

    // Picks a point (x1,x2) on the graph that results in the slope indicator (x2,y2) being off the graph. This forces the user to invert the slope.
    protected Point2D choosePointForSlopeInversion( final Fraction slope, final IntegerRange graphXRange, final IntegerRange graphYRange ) {

        final int rise = slope.numerator;
        final int run = slope.denominator;

        // x1 coordinates
        IntegerRange xRange;
        if ( run >= 0 ) {
            xRange = new IntegerRange( graphXRange.getMax() - run + 1, graphXRange.getMax() );
        }
        else {
            xRange = new IntegerRange( graphXRange.getMin(), graphXRange.getMin() - run - 1 );
        }
        ArrayList<Integer> xList = rangeToList( xRange );

        // y1 coordinates
        IntegerRange yRange;
        if ( rise >= 0 ) {
            yRange = new IntegerRange( graphYRange.getMax() - rise + 1, graphYRange.getMax() );
        }
        else {
            yRange = new IntegerRange( graphYRange.getMin(), graphYRange.getMin() - rise - 1 );
        }
        ArrayList<Integer> yList = rangeToList( yRange );

        // random point
        final int x1 = xList.get( randomIndex( xList ) );
        final int y1 = yList.get( randomIndex( yList ) );

        final int x2 = x1 + run;
        final int y2 = y1 + rise;

        // (x1,x2) must be on the graph, (x2,y2) must be off the graph
        assert ( graphXRange.contains( x1 ) && !graphXRange.contains( x2 ) );
        assert ( graphYRange.contains( y1 ) && !graphYRange.contains( y2 ) );

        return new Point2D.Double( x1, y1 );
    }

    // Shuffles a list of challenges.
    protected ArrayList<Challenge> shuffle( ArrayList<Challenge> list ) {
        ArrayList<Challenge> shuffledList = new ArrayList<Challenge>();
        while ( list.size() != 0 ) {
            int index = randomIndex( list );
            shuffledList.add( list.get( index ) );
            list.remove( index );
        }
        return shuffledList;
    }
}
