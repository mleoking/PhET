// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.linegraphing.common.LGColors;

/**
 * Model of an immutable line, using slope-intercept form, y=mx+b.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine {

    public static final SlopeInterceptLine Y_EQUALS_X_LINE = new SlopeInterceptLine( 1, 1, 0, LGColors.Y_EQUALS_X );  // y = x
    public static final SlopeInterceptLine Y_EQUALS_NEGATIVE_X_LINE = new SlopeInterceptLine( -1, 1, 0, LGColors.Y_EQUALS_NEGATIVE_X ); // y = -x

    public final double rise;
    public final double run;
    public final double intercept;
    public final Color color, highlightColor;

    public SlopeInterceptLine( double rise, double run, double intercept, Color color ) {
        this( rise, run, intercept, color, color );
    }

    public SlopeInterceptLine( double rise, double run, double intercept, Color color, Color highlightColor ) {
        this.rise = rise;
        this.run = run;
        this.intercept = intercept;
        this.color = color;
        this.highlightColor = highlightColor;
    }

    // Duplicates a line with different colors
    public SlopeInterceptLine( SlopeInterceptLine line, Color color, Color highlightColor ) {
        this( line.rise, line.run, line.intercept, color, highlightColor );
    }

    // Line is undefined if it's slope (rise/run) is undefined.
    public boolean isDefined() {
        return ( run != 0 );
    }

    // Gets the slope, m=rise/run.
    private double getSlope() {
        assert ( run != 0 );
        return rise / run;
    }

    // y = mx + b, returns 0 if there is no unique solution
    public double solveY( double x ) {
        return solveY( getSlope(), x, intercept );
    }

    // y = mx + b
    private static double solveY( double m, double x, double b ) {
        return ( m * x ) + b;
    }

    // x = (y-b)/m
    public double solveX( double y ) {
        if ( run == 0 ) {
            return 0;
        }
        else {
            return solveX( y, intercept, getSlope() );
        }
    }

    // x = (y-b)/m
    private static double solveX( double y, double b, double m ) {
        assert ( m != 0 );
        return ( y - b ) / m;
    }

    public int getReducedRise() {
        return MathUtil.round( rise / getGCD() );
    }

    public int getReducedRun() {
        return MathUtil.round( run / getGCD() );
    }

    // Gets the greatest common divisor (GCD) of the rise and run.
    private int getGCD() {
        return MathUtil.getGreatestCommonDivisor( MathUtil.round( Math.abs( rise ) ), MathUtil.round( Math.abs( run ) ) );
    }

    @Override public String toString() {
        return MessageFormat.format( "y=({0}/{1})x{2}", rise, run, intercept ) + ", color=" + color + ", highlightColor=" + highlightColor;
    }
}
