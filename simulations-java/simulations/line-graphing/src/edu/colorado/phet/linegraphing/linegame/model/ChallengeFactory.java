// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.linegraphing.common.model.Fraction;

/**
 * Base class for challenge factories. These factories handle generation of challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class ChallengeFactory {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( ChallengeFactory.class.getCanonicalName() );

    private static final boolean USE_HARD_CODED_CHALLENGES = true;

    /**
     * Creates challenges for the specified level.
     *
     * @param level  game level
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     * @throws IllegalArgumentException if level is out of range
     */
    public static ArrayList<Challenge> createChallenges( int level, IntegerRange xRange, IntegerRange yRange ) {

        if ( USE_HARD_CODED_CHALLENGES ) {
            return ChallengeFactoryHardCoded.createChallenges( level, xRange, yRange );
        }

        switch( level ) {
            case 1:
                return new ChallengeFactory1().createChallenges( xRange, yRange );
            case 2:
                return new ChallengeFactory2().createChallenges( xRange, yRange );
            case 3:
                return new ChallengeFactory3().createChallenges( xRange, yRange );
            case 4:
                return new ChallengeFactory4().createChallenges( xRange, yRange );
            default:
                //TODO throw exception
                return ChallengeFactoryHardCoded.createChallenges( level, xRange, yRange );
        }
    }

    protected final Random random;

    protected ChallengeFactory() {
        this.random = new Random();
    }

    /**
     * Creates challenges for the factory's game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public abstract ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange );

    // Converts an integer range to a list of values that are in that range.
    protected static ArrayList<Integer> rangeToList( IntegerRange range ) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for ( int i = range.getMin(); i <= range.getMax(); i++ ) {
            list.add( i );
        }
        assert ( list.size() > 0 );
        return list;
    }

    // Gets a random index for a specified list.
    protected int randomIndex( List list ) {
        return random.nextInt( list.size() );
    }

    // Picks a manipulation mode, removes it from the list.
    protected ManipulationMode pickManipulationMode( ArrayList<ManipulationMode> list ) {
        int index = randomIndex( list );
        final ManipulationMode manipulationMode = list.get( index );
        list.remove( index );
        return manipulationMode;
    }

    // Picks a line form, removes it from the list.
    protected LineForm pickLineForm( ArrayList<LineForm> list ) {
        int index = randomIndex( list );
        final LineForm lineForm = list.get( index );
        list.remove( index );
        return lineForm;
    }

    // Picks an integer, removes it from the bin.
    protected int pickInteger( ArrayList<ArrayList<Integer>> bins ) {
        return pickInteger( bins, rangeToList( new IntegerRange( 0, bins.size() - 1 ) ) );
    }

    // Picks an integer, removes it from the bin, removes the bin from the binIndices.
    protected int pickInteger( ArrayList<ArrayList<Integer>> bins, ArrayList<Integer> binIndices ) {
        int index = randomIndex( binIndices );
        ArrayList<Integer> bin = bins.get( binIndices.get( index ) );
        binIndices.remove( index );
        index = randomIndex( bin );
        final int value = bin.get( index );
        bin.remove( index );
        return value;
    }

    // Picks a fraction, removes it from the bin.
    protected Fraction pickFraction( ArrayList<ArrayList<Fraction>> bins ) {
        return pickFraction( bins, rangeToList( new IntegerRange( 0, bins.size() - 1 ) ) );
    }

    // Picks a fraction, removes it from the bin, removes the bin from the binIndices.
    protected Fraction pickFraction( ArrayList<ArrayList<Fraction>> bins, ArrayList<Integer> binIndices ) {
        int index = randomIndex( binIndices );
        ArrayList<Fraction> bin = bins.get( binIndices.get( index ) );
        binIndices.remove( index );
        index = randomIndex( bin );
        final Fraction value = bin.get( index );
        bin.remove( index );
        return value;
    }

    // Picks a point, removes it from the bin.
    protected Point2D pickPoint( ArrayList<ArrayList<Point2D>> pointBins ) {
        return pickPoint( pointBins, rangeToList( new IntegerRange( 0, pointBins.size() - 1 ) ) );
    }

    // Picks a point, removes it from the bin, removes the bin from the binIndices.
    protected Point2D pickPoint( ArrayList<ArrayList<Point2D>> bins, ArrayList<Integer> binIndices ) {
        int index = randomIndex( binIndices );
        ArrayList<Point2D> bin = bins.get( binIndices.get( index ) );
        binIndices.remove( index );
        index = randomIndex( bin );
        final Point2D point = bin.get( index );
        bin.remove( index );
        return point;
    }

    // Picks a point that keeps the slope indicator on the graph.
    protected Point2D pickPointForSlope( final Fraction slope, final IntegerRange graphXRange, final IntegerRange graphYRange ) {

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
    protected Point2D pickPointForInvertedSlope( final Fraction slope, final IntegerRange graphXRange, final IntegerRange graphYRange ) {

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

        LOGGER.info( "slope=" + rise + "/" + run +
                     " xRange=[" + xRange.getMin() + "," + xRange.getMax() + "]" +
                     " yRange=[" + yRange.getMin() + "," + yRange.getMax() + "]" +
                     " (x1,y1)=(" + x1 + "," + y1 + ")" +
                     " (x2,y2)=(" + x2 + "," + y2 + ")" );

        // (x1,x2) must be on the graph, (x2,y2) must be off the graph
        assert( graphXRange.contains( x1 ) && !graphXRange.contains( x2 ) );
        assert( graphYRange.contains( y1 ) && !graphYRange.contains( y2 ) );

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
