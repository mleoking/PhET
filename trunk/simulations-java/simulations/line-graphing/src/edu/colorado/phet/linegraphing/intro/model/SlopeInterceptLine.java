// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

/**
 * Model of an immutable line, using slope-intercept form, y=mx+b.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine {

    public final double rise;
    public final double run;
    public final double intercept;

    public SlopeInterceptLine( double rise, double run, double intercept ) {
        this.rise = rise;
        this.run = run;
        this.intercept = intercept;
    }

    // Line is undefined if it's slope (rise/run) is undefined.
    public boolean isDefined() {
        return ( run != 0 );
    }

    // Gets the slope, m=rise/run.
    private double getSlope() {
        assert( run != 0 );
        return ( (double) rise ) / run;
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
        assert( m != 0 );
        return ( y - b ) / m;
    }
}
