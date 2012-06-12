// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.linegraphing.common.LGColors;

/**
 * A straight line, which can be specified in slope-intercept or point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StraightLine {

    // standard lines
    public static final StraightLine Y_EQUALS_X_LINE = new StraightLine( 1, 1, 0, LGColors.Y_EQUALS_X, LGColors.Y_EQUALS_X );  // y = x
    public static final StraightLine Y_EQUALS_NEGATIVE_X_LINE = new StraightLine( -1, 1, 0, LGColors.Y_EQUALS_NEGATIVE_X, LGColors.Y_EQUALS_NEGATIVE_X ); // y = -x

    public final double rise; // vertical component of the slope
    public final double run; // horizontal component of the slope
    public final double x1, y1; // some point
    public final double yIntercept; // y intercept, Double.NaN if the line doesn't intersect the y axis
    public final Color color, highlightColor; // colors used for visualizing the line

    // slope-intercept form: y = mx + b
    public StraightLine( double rise, double run, double yIntercept, Color color, Color highlightColor ) {
        this.rise = rise;
        this.run = run;
        this.x1 = 0;
        this.y1 = yIntercept;
        this.yIntercept = yIntercept;
        this.color = color;
        this.highlightColor = highlightColor;
    }

    // point-slope form: y = m(x-x1) + y1, or (y-y1) = m(x-x1)
    public StraightLine( double rise, double run, double x1, double y1, Color color, Color highlightColor ) {
        this.rise = rise;
        this.run = run;
        this.x1 = x1;
        this.y1 = y1;
        this.yIntercept = ( x1 == 0 )? y1 : solveY( 0 );
        this.color = color;
        this.highlightColor = highlightColor;
    }

    // duplicates a line with different colors
    public StraightLine( StraightLine line, Color color, Color highlightColor ) {
        this( line.rise, line.run, line.x1, line.y1, color, highlightColor );
    }

    // Line is undefined if its slope is undefined.
    public boolean isUndefined() {
        return ( run == 0 );
    }

    /*
     * Given x, solve y = m(x - x1) + y1
     * Returns Double.NaN if the slope is undefined.
     */
    public double solveY( double x ) {
        if ( isUndefined() ) {
            return Double.NaN;
        }
        else {
            return ( ( rise / run ) * ( x - x1 ) ) + y1;
        }
    }

    /*
     * Given y, solve x = ((y - y1)/m) + x1
     * Returns Double.NaN if the slope is undefined or solution is not unique.
     */
    public double solveX( double y ) {
        if ( isUndefined() || rise == 0 ) {
            return Double.NaN;
        }
        else {
            return ( ( y - y1 ) / ( rise / run ) ) + x1;
        }
    }

    // Get the reduced form of the rise. For example, if rise/run=6/4, reduced slope=3/2, and reduced rise=3.
    public int getReducedRise() {
        return MathUtil.round( rise / getGCD() );
    }

    // Get the reduced form of the rise. For example, if rise/run=6/4, reduced slope=3/2, and reduced run=3.
    public int getReducedRun() {
        return MathUtil.round( run / getGCD() );
    }

    // Gets the greatest common divisor (GCD) of the rise and run.
    private int getGCD() {
        return MathUtil.getGreatestCommonDivisor( MathUtil.round( Math.abs( rise ) ), MathUtil.round( Math.abs( run ) ) );
    }

    @Override public String toString() {
        return "rise=" + rise + ",run=" + run + ",x1=" + x1 + ",y1=" + y1 + ",yIntercept=" + yIntercept;
    }
}
