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

    public abstract double solveX( double y );

    public abstract double solveY( double x );

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
}
