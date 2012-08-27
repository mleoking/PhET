// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.linegraphing.common.LGColors;

/**
 * An immutable straight line, which can be specified in slope-intercept or point-slope form.
 * Note that slope-intercept is a special case of point-slope, with the point being on the y axis.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeLine {

    // standard lines
    public static final SlopeInterceptLine Y_EQUALS_X_LINE = new SlopeInterceptLine( 1, 1, 0, LGColors.Y_EQUALS_X );  // y = x
    public static final SlopeInterceptLine Y_EQUALS_NEGATIVE_X_LINE = new SlopeInterceptLine( -1, 1, 0, LGColors.Y_EQUALS_NEGATIVE_X ); // y = -x

    public final double rise; // vertical component of the slope
    public final double run; // horizontal component of the slope
    public final double x1, y1; // some point
    public final Color color; // color used for visualizing the line

    // point-slope form: (y - y1) = m(x - x1)
    public PointSlopeLine( double x1, double y1, double rise, double run, Color color ) {
        assert( !( rise == 0 && run == 0 ) ); // a line with slope=0/0 is undefined
        this.rise = rise;
        this.run = run;
        this.x1 = x1;
        this.y1 = y1;
        this.color = color;
    }

    // Creates a new instance with a different color.
    public PointSlopeLine withColor( Color color ) {
        return new PointSlopeLine( x1, y1, rise, run, color );
    }

    /*
     * Given x, solve y = m(x - x1) + y1
     * Returns Double.NaN if the solution is not unique, or there is no solution (x can't possibly be on the line.)
     * This occurs when we have a vertical line, with no rise.
     */
    public double solveY( double x ) {
        if ( run == 0 ) {
            return Double.NaN;
        }
        else {
            return ( ( rise / run ) * ( x - x1 ) ) + y1;
        }
    }

    /*
     * Given y, solve x = ((y - y1)/m) + x1
     * Returns Double.NaN if the solution is not unique, or there is no solution (y can't possibly be on the line.)
     * This occurs when we have a horizontal line, with no run.
     */
    public double solveX( double y ) {
        if ( rise == 0 ) {
            return Double.NaN;
        }
        else {
            return ( ( y - y1 ) / ( rise / run ) ) + x1;
        }
    }

    /*
     * Creates a simplified instance of the line.
     * For our purposes, this means simplifying (aka, reducing) the slope.
     * Simplification uses Euclid's algorithm for computing the greatest common divisor (GCD) of two integers,
     * so this is effective only if the rise and run are integer values. Otherwise 'this' is returned.
     */
    public PointSlopeLine simplified() {
        if ( ( rise == (int) rise ) && ( run == (int) run ) ) { // true if rise and run are integers
            final int reducedRise = (int) ( rise / MathUtil.getGreatestCommonDivisor( (int) rise, (int) run ) );
            final int reducedRun = (int) ( run / MathUtil.getGreatestCommonDivisor( (int) rise, (int) run ) );
            return new PointSlopeLine( x1, y1, reducedRise, reducedRun, color );
        }
        else {
            return this;
        }
    }

    // Returns true if 2 points on the specified line are also on this line.
    public boolean same( PointSlopeLine line ) {
        return onLine( line.x1, line.y1 ) && onLine( line.x1 + run, line.y1 + rise );
    }

    // Returns true if point (x,y) is on this line.
    private boolean onLine( double x, double y ) {
        if ( rise == 0 ) {
            return ( y == y1 );
        }
        else if ( run == 0 ) {
            return ( x == x1 );
        }
        else {
            return ( x == solveX( y ) );
        }
    }

    @Override public String toString() {
        return "rise=" + rise + ",run=" + run + ",x1=" + x1 + ",y1=" + y1 + ",color=" + color;
    }
}
