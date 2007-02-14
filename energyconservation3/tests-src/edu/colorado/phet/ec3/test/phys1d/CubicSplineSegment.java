package edu.colorado.phet.ec3.test.phys1d;

import Jama.Matrix;

/**
 * User: Sam Reid
 * Date: Feb 13, 2007
 * Time: 10:08:12 PM
 * Copyright (c) Feb 13, 2007 by Sam Reid
 * <p/>
 * see http://en.wikipedia.org/wiki/Spline_interpolation
 */

public class CubicSplineSegment {
    private double h;
    private double z0;
    private double z1;

    private double x0;
    private double x1;

    private double y0;
    private double y1;

    /**
     * Returns the CubicSpline segments between each of the control points.
     *
     * @param x
     * @return
     */
    public static CubicSplineSegment[] interpolate( double[] x ) {
        int n = x.length;
        Jama.Matrix A = new Matrix( n, n );
        A.set( 0, 0, 1 );
        A.set( n - 1, n - 1, 1 );
        for( int i = 1; i < n - 1; i++ ) {
            A.set( i, i - 1, h[i - 1] );
            A.set( i, i, 2 * ( h[i - 1] + h[i] ) );
            A.set( i, i + 1, h[i] );
        }

        Matrix b = new Matrix( n, 1 );
        b.set( 0, 0, 0 );
        b.set( n - 1, 0, 0 );
        for( int i = 1; i < n - 1; i++ ) {
            double a1 = ( ( y[i + 1] - y[i] ) / h[i] );
            double a2 = ( ( y[i] - y[i - 1] ) / h[i - 1] );
            b.set( i, 0, 6 * ( a1 - a2 ) );
        }
        Matrix z = A.solve( b );

    }
}
