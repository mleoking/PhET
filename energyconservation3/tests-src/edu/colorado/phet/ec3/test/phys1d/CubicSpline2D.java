package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 14, 2007
 * Time: 3:09:47 AM
 * Copyright (c) Feb 14, 2007 by Sam Reid
 */

public class CubicSpline2D {
    private CubicSpline x;
    private CubicSpline y;

    public CubicSpline2D( CubicSpline x, CubicSpline y ) {
        this.x = x;
        this.y = y;
    }

    public static CubicSpline2D interpolate( Point2D[] pts ) {
        double[] s = new double[pts.length];
        double[] x = new double[pts.length];
        double[] y = new double[pts.length];
        for( int i = 0; i < pts.length; i++ ) {
            s[i] = ( 1.0 / ( pts.length - 1 ) ) * i;//*2.0;
            x[i] = pts[i].getX();
            y[i] = pts[i].getY();
        }
        s[s.length - 1] = Math.round( s[s.length - 1] );//to be exact, in case of roundoff error
        return new CubicSpline2D( CubicSpline.interpolate( s, x ), CubicSpline.interpolate( s, y ) );
    }

    public Point2D evaluate( double s ) {
        return new Point2D.Double( x.evaluate( s ), y.evaluate( s ) );
    }

    public static void main( String[] args ) {

        CubicSpline2D cubicSpline2D = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 1, 1 ),
                new Point2D.Double( 2, 0 )
        } );
        for( double s = 0.0; s < 1.0; s += 0.1 ) {
            Point2D at = cubicSpline2D.evaluate( s );
            System.out.println( "s = " + s + ", at=" + at );
//            System.out.println( at.getX()+"\t"+at.getY() );
//            System.out.println( at.getY() );
        }
        double delta = cubicSpline2D.getMetricDelta( 0, 1 );
        System.out.println( "Spline length=" + delta );

        double fracDist = cubicSpline2D.getFractionalDistance( 0, delta );
        System.out.println( "fracDist = " + fracDist );

    }

    /*
    * Returns the metric distance between 2 fractional points.
     */
    public double getMetricDelta( double a0, double a1 ) {
//        if( a0 < 0 || a1 > 1 ) {
//            throw new RuntimeException( "Out of bounds, a0=" + a0 + ", a1=" + a1 );
//        }
        if( a0 > a1 ) {
            return -getMetricDelta( a1, a0 );
        }
        if( a0 == a1 ) {
            return 0;
        }
//        double da = 1E-1;
        double da = 1E-1;
//        double da = 1.5;
        if( Math.abs( a0 - a1 ) < da ) {
            return evaluate( a0 ).distance( evaluate( a1 ) );
        }
        else {
            return getMetricDelta( a0, a0 + da / 2 ) + getMetricDelta( a0 + da / 2, a1 - da / 2 ) + getMetricDelta( a1 - da / 2, a1 );
        }
    }

    /*
    * Finds the fractional distance along the track that corresponds to metric distance ds
    */
    public double getFractionalDistance( double alpha0, double ds ) {
        double lowerBound = -1;
        double upperBound = 2;

        double guess = ( upperBound + lowerBound ) / 2.0;

        double metricDelta = getMetricDelta( alpha0, guess );
        double epsilon = 1E-2;
        while( Math.abs( metricDelta - ds ) > epsilon ) {
            if( metricDelta > ds ) {
                upperBound = guess;
            }
            else {
                lowerBound = guess;
            }
            guess = ( upperBound + lowerBound ) / 2.0;
            metricDelta = getMetricDelta( alpha0, guess );
        }
        return guess - alpha0;
    }

    public AbstractVector2D getUnitParallelVector( double alpha ) {
        double epsilon = 1E-4;
        double a0 = alpha - epsilon / 2;
        double a1 = alpha + epsilon / 2;
        return new Vector2D.Double( evaluate( a0 ), evaluate( a1 ) ).getNormalizedInstance();
    }

    public AbstractVector2D getUnitNormalVector( double alpha ) {
        return getUnitParallelVector( alpha ).getNormalVector();
    }

    public double getAngle( double alpha ) {
        return getUnitParallelVector( alpha ).getAngle();
    }
}
