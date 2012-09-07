// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Factories for creating lines.
 * These factories are used to handle instantiation of lines in classes that are parameterized using generics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFactory<T extends PointPointLine> {

    // Creates a line with a different point
    public abstract T withPoint( T line, double x1, double y1 );

    // Creates a line with a different slope
    public abstract T withSlope( T line, double rise, double run );

    // Creates a line with a different color
    public abstract T withColor( T line, Color color );

    /*
     * Creates a simplified instance of the line.
     * For our purposes, this means simplifying (aka, reducing) the slope.
     * Simplification uses Euclid's algorithm for computing the greatest common divisor (GCD) of two integers,
     * so this is effective only if the rise and run are integer values. Otherwise 'this' is returned.
     */
    public T simplified( T line ) {
        if ( ( line.rise == (int) line.rise ) && ( line.run == (int) line.run ) ) { // true if rise and run are integers
            final int reducedRise = (int) ( line.rise / MathUtil.getGreatestCommonDivisor( (int) line.rise, (int) line.run ) );
            final int reducedRun = (int) ( line.run / MathUtil.getGreatestCommonDivisor( (int) line.rise, (int) line.run ) );
            return withSlope( line, reducedRise, reducedRun );
        }
        else {
            return line;
        }
    }

    // Factory for creating lines in point-slope form.
    public static class PointSlopeLineFactory extends LineFactory<PointSlopeLine> {

        public PointSlopeLine withPoint( PointSlopeLine line, double x1, double y1 ) {
            return new PointSlopeLine( x1, y1, line.rise, line.run, line.color );
        }

        public PointSlopeLine withSlope( PointSlopeLine line, double rise, double run ) {
            return new PointSlopeLine( line.x1, line.y1, rise, run, line.color );
        }

        public PointSlopeLine withColor( PointSlopeLine line, Color color ) {
            return new PointSlopeLine( line.x1, line.y1, line.rise, line.run, color );
        }
    }

    // Factory for creating lines in slope-intercept form.
    public static class SlopeInterceptLineFactory extends LineFactory<SlopeInterceptLine> {

        public SlopeInterceptLine withPoint( SlopeInterceptLine line, double x1, double y1 ) {
            assert ( x1 == 0 ); //TODO would be nice if we could avoid this
            return new SlopeInterceptLine( line.rise, line.run, y1, line.color );
        }

        public SlopeInterceptLine withSlope( SlopeInterceptLine line, double rise, double run ) {
            return new SlopeInterceptLine( rise, run, line.y1, line.color );
        }

        public SlopeInterceptLine withColor( SlopeInterceptLine line, Color color ) {
            return new SlopeInterceptLine( line.rise, line.run, line.y1, color );
        }
    }
}
