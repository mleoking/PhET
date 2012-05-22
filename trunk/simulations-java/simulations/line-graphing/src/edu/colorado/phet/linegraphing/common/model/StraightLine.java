// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Base class for all straight lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StraightLine {

    public final double rise;
    public final double run;
    public final Color color, highlightColor;

    public StraightLine( double rise, double run, Color color, Color highlightColor ) {
        this.rise = rise;
        this.run = run;
        this.color = color;
        this.highlightColor = highlightColor;
    }

    // Given y, solve for x.
    public abstract double solveX( double y );

    // Given x, solve for y.
    public abstract double solveY( double x );

    // Gets the y intercept.
    public abstract double getIntercept();

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
}
