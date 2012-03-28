// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

/**
 * Model of an immutable line, using slope-intercept form, y=mx+b.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine {

    public final int rise;
    public final int run;
    public final int intercept;

    public SlopeInterceptLine( int rise, int run, int intercept ) {
        this.rise = rise;
        this.run = run;
        this.intercept = intercept;
    }

    //TODO deal with the case where run == 0, and the equation does not have unique solutions.

    public double getSlope() {
        return ( (double) rise ) / run;
    }

    // y = mx + b
    public double solveY( double x ) {
        return solveY( getSlope(), x, intercept );
    }

    // y = mx + b
    public static double solveY( double m, double x, double b ) {
        return ( m * x ) + b;
    }

    // x = (y-b)/m
    public double solveX( double y ) {
       return solveX( y, intercept, getSlope() );
    }

    // x = (y-b)/m
    public static double solveX( double y, double b, double m ) {
        return ( y - b ) / m;
    }
}
