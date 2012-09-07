// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

/**
 * An immutable line, described by 2 points.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointPointLine {

    public final double x1, y1; // x and y components of the first point
    public final double x2, y2; // x and y components of the second point
    public final double rise, run; // vertical and horizontal components of the slope
    public final Color color; // color used for visualizing the line

    // Specified using 2 points
    public PointPointLine( double x1, double y1, double x2, double y2, Color color ) {
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
    public boolean same( PointPointLine line ) {
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
        return "x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + ", rise=" + rise + ", run=" + run + ", color=" + color;
    }
}
