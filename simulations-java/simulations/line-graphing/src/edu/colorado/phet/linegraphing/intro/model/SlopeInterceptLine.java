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

    public double getSlope() {
        return ( (double) rise ) / run;
    }

    // y=mx+b
    public double solve( double x ) {
        return solve( getSlope(), x, intercept );
    }

    // y=mx+b
    public static double solve( double m, double x, double b ) {
        return ( m * x ) + b;
    }
}
