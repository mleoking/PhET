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
    public static final StraightLine Y_EQUALS_X_LINE = new StraightLine( 1, 1, 0, LGColors.Y_EQUALS_X );  // y = x
    public static final StraightLine Y_EQUALS_NEGATIVE_X_LINE = new StraightLine( -1, 1, 0, LGColors.Y_EQUALS_NEGATIVE_X ); // y = -x

    public final double rise; // vertical component of the slope
    public final double run; // horizontal component of the slope
    public final double x1, y1; // some point
    public final double yIntercept; // y intercept, Double.NaN if the line doesn't intersect the y axis
    public final Color color; // color used for visualizing the line

    // slope-intercept form: y = mx + b
    public StraightLine( double rise, double run, double yIntercept, Color color ) {
        this.rise = rise;
        this.run = run;
        this.x1 = 0;
        this.y1 = yIntercept;
        this.yIntercept = yIntercept;
        this.color = color;
    }

    // point-slope form: (y - y1) = m(x - x1)
    public StraightLine( double rise, double run, double x1, double y1, Color color ) {
        this.rise = rise;
        this.run = run;
        this.x1 = x1;
        this.y1 = y1;
        this.yIntercept = ( x1 == 0 )? y1 : solveY( 0 );
        this.color = color;
    }

    // Creates a new instance with a different color.
    public StraightLine withColor( Color color ) {
        return new StraightLine( rise, run, x1, y1, color );
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

    /*
     * Creates a simplified instance of the line.
     * For our purposes, this means simplifying (aka, reducing) the slope.
     * Simplification uses Euclid's algorithm for computing the greatest common divisor (GCD) of two integers,
     * so this is effective only if the rise and run are integer values. Otherwise 'this' is returned.
     */
    public StraightLine simplified() {
        if ( ( rise == (int) rise ) && ( run == (int) run ) ) { // true if rise and run are integers
            final int reducedRise = (int)( rise / MathUtil.getGreatestCommonDivisor( (int) rise, (int) run ) );
            final int reducedRun = (int)( run / MathUtil.getGreatestCommonDivisor( (int) rise, (int) run ) );
            return new StraightLine( reducedRise, reducedRun, x1, y1, color );
        }
        else {
            return this;
        }
    }

    @Override public String toString() {
        return "rise=" + rise + ",run=" + run + ",x1=" + x1 + ",y1=" + y1 + ",yIntercept=" + yIntercept;
    }

    /*
     * Two line objects are equal if they have identical field values.
     * Note that this is different than being "the same line"; two objects may describe the same line, but be unequal.
     */
    @Override public boolean equals( Object o ) {
        if ( !(o instanceof  StraightLine ) ) {
            return false;
        }
        else {
            StraightLine line = (StraightLine) o;
            return ( rise == line.rise ) &&
                   ( run == line.run ) &&
                   ( x1 == line.x1 ) &&
                   ( y1 == line.y1 ) &&
                   ( yIntercept == line.yIntercept );
            //Note: color is not significant for comparison //TODO this could cause problems
        }
    }

    //TODO override hashCode, since we overrode equals

    // Two lines are the same if they have the same simplified form.
    public boolean same( StraightLine line ) {
        return simplified().equals( line.simplified() );
    }

        // Specialization that rounds its constructor args to integers, using nearest-neighbor rounding.
    public static class RoundedStraightLine extends StraightLine {

        public RoundedStraightLine( double rise, double run, double yIntercept, Color color ) {
            super( MathUtil.roundHalfUp( rise ), MathUtil.roundHalfUp( run ), MathUtil.roundHalfUp( yIntercept ), color );
        }

        public RoundedStraightLine( double rise, double run, double x1, double y1, Color color ) {
            super( MathUtil.roundHalfUp( rise ), MathUtil.roundHalfUp( run ), MathUtil.roundHalfUp( x1 ), MathUtil.roundHalfUp( y1 ), color );
        }
    }
}
