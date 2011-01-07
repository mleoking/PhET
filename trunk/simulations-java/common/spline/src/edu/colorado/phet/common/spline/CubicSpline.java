// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.spline;

import Jama.Matrix;
import java.io.Serializable;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Feb 13, 2007
 * Time: 10:08:12 PM
 * <p/>
 * <p/>
 * see http://en.wikipedia.org/wiki/Spline_interpolation
 */

public class CubicSpline implements Serializable {
    private CubicSplineSegment[] segments;
    private double[] xTrain;
    private double[] yTrain;

    public CubicSpline( CubicSplineSegment[] segments, double[] x, double[] y ) {
        this.segments = segments;
        this.xTrain = x;
        this.yTrain = y;
    }

    public boolean equals( Object obj ) {
        if( obj instanceof CubicSpline ) {
            CubicSpline cubicSpline = (CubicSpline)obj;
            return Arrays.equals( cubicSpline.segments, segments ) && Arrays.equals( cubicSpline.xTrain, xTrain ) && Arrays.equals( cubicSpline.yTrain, yTrain );
        }
        else {
            return false;
        }
    }

    public double evaluate( double x ) {
        for( int i = 0; i < xTrain.length - 1; i++ ) {//todo could be binary search..?
            if( x >= xTrain[i] && x <= xTrain[i + 1] ) {
                if( i >= segments.length ) {
                    throw new RuntimeException( "out of bounds" );
                }
                return segments[i].evaluate( x );
            }
        }
        if( x < 0 ) {
            return segments[0].evaluate( x );
        }
        else {
            return segments[segments.length - 1].evaluate( x );
        }
//        throw new RuntimeException( "value not contained in spline: " + x + ", min=" + xTrain[0] + ", max=" + xTrain[xTrain.length - 1] );
    }

    static class CubicSplineSegment implements Cloneable, Serializable {
        private double h;
        private double z0;
        private double z1;

        private double x0;
        private double x1;

        private double y0;
        private double y1;

        public CubicSplineSegment( double h, double z0, double z1, double x0, double x1, double y0, double y1 ) {
            this.h = h;
            this.z0 = z0;
            this.z1 = z1;
            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;
            this.y1 = y1;
        }

        public boolean equals( Object obj ) {
            if( obj instanceof CubicSplineSegment ) {
                CubicSplineSegment c = (CubicSplineSegment)obj;
                return c.h == h && c.z0 == z0 && c.z1 == z1 && c.x0 == x0 && c.x1 == x1 && c.y0 == y0 && c.y1 == y1;
            }
            else {
                return false;
            }
        }

        public double evaluate( double x ) {
            return ( z1 * pow3( x - x0 ) + z0 * pow3( x1 - x ) ) / ( 6 * h ) +
                   ( y1 / h - h / 6 * z1 ) * ( x - x0 ) + ( y0 / h - h / 6 * z0 ) * ( x1 - x );
        }
    }

    private static double pow3( double a ) {//improves performance by 6% or more in a particular test
        return a * a * a;
    }

    /**
     * Returns the CubicSpline segments between each of the control points.
     *
     * @param x
     * @return
     */
    public static CubicSpline interpolate( double[] x, double[] y ) {
        int n = x.length;
        double[] h = new double[x.length - 1];
        for( int i = 0; i < h.length; i++ ) {
            h[i] = x[i + 1] - x[i];
        }

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
        Matrix zMat = A.solve( b );
        double[] z = zMat.getColumnPackedCopy();
        CubicSplineSegment[] segments = new CubicSplineSegment[n - 1];
        for( int i = 0; i < segments.length; i++ ) {
            segments[i] = new CubicSplineSegment( h[i], z[i], z[i + 1], x[i], x[i + 1], y[i], y[i + 1] );
        }
        return new CubicSpline( segments, x, y );
    }

    public static void main( String[] args ) {
//        CubicSpline spline = interpolate( new double[]{0, 1, 2}, new double[]{0, 1, 0} );
//        for( double x = 0; x < 2.0-0.01; x += 0.1 ) {
//            EnergySkateParkLogging.println( "x = " + x + ", y=" + spline.evaluate( x ) );
//        }
//        EnergySkateParkLogging.println( "x = " + 2.0 + ", y=" + spline.evaluate( 2.0 ) );

        CubicSpline spline = interpolate( new double[]{0, 1, 2}, new double[]{0, 1, 0} );
        for( double x = 0; x < 2.0 - 0.01; x += 0.1 ) {
            System.out.println( spline.evaluate( x ) );
//            EnergySkateParkLogging.println( "x = " + x + ", y=" + spline.evaluate( x ) );
        }
        System.out.println( spline.evaluate( 2.0 ) );
//        EnergySkateParkLogging.println( "x = " + 2.0 + ", y=" + spline.evaluate( 2.0 ) );
    }
}
