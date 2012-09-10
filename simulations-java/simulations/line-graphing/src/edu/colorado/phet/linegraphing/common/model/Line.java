// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.linegraphing.common.LGColors;

/**
 * An immutable line, described by 2 points, (x1,y1) and (x2,y2).
 * Slope components (rise and run) are signed relative to (x1,y1).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Line {

    // standard lines
    public static final Line Y_EQUALS_X_LINE = new Line( 0, 0, 1, 1, LGColors.Y_EQUALS_X );  // y = x
    public static final Line Y_EQUALS_NEGATIVE_X_LINE = new Line( 0, 0, 1, -1, LGColors.Y_EQUALS_NEGATIVE_X );

    public final double x1, y1; // x and y components of the first point
    public final double x2, y2; // x and y components of the second point
    public final double rise, run; // vertical and horizontal components of the slope, relative to (x1,y1)
    public final Color color; // color used for visualizing the line

    // Specified using 2 points
    public Line( double x1, double y1, double x2, double y2, Color color ) {
        assert ( x1 != x2 || y1 != y2 ); // 2 different points are required
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.rise = y2 - y1;
        this.run = x2 - x1;
        this.color = color;
    }

    /*
     * Creates a line using point-slope description: (y - y1) = m(x - x1)
     * Need to use a factory method because params are identical to primary constructor.
     */
    public static Line createPointSlope( double x1, double y1, double rise, double run, Color color ) {
        return new Line( x1, y1, x1 + run, y1 + rise, color );
    }

    /*
     * Creates a line using slope-intercept description: y = mx + b
     * Using a factory method instead of a constructor to be consistent with createPointSlope.
     */
    public static Line createSlopeIntercept( double rise, double run, double yIntercept, Color color ) {
        return createPointSlope( 0, yIntercept, rise, run, color );
    }

    // Convenience method for creating a line with a different color.
    public Line withColor( Color color ) {
        return new Line( x1, y1, x2, y2, color );
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

    // Returns true if 2 points on the specified line are also on this line.
    public boolean same( Line line ) {
        return onLine( line.x1, line.y1 ) && onLine( line.x1 + run, line.y1 + rise );
    }

    /*
     * Creates a simplified instance of the line.
     * For our purposes, this means simplifying (aka, reducing) the slope.
     * Simplification uses Euclid's algorithm for computing the greatest common divisor (GCD) of two integers,
     * so simplification is performed only if the rise and run are integer values. Otherwise 'this' is returned.
     */
    public Line simplified() {
        if ( ( rise == (int) rise ) && ( run == (int) run ) ) { // true if rise and run are integers
            final int reducedRise = (int) ( rise / MathUtil.getGreatestCommonDivisor( (int) rise, (int) run ) );
            final int reducedRun = (int) ( run / MathUtil.getGreatestCommonDivisor( (int) rise, (int) run ) );
            return createPointSlope( x1, y1, reducedRise, reducedRun, color );
        }
        else {
            return this;
        }
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
        return "x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + ", rise=" + rise + ", run=" + run + ", color=" + color;
    }
}
