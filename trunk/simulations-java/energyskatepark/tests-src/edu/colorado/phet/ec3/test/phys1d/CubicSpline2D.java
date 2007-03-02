package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Feb 14, 2007
 * Time: 3:09:47 AM
 * Copyright (c) Feb 14, 2007 by Sam Reid
 */

public class CubicSpline2D {
    private CubicSpline x;
    private CubicSpline y;
    private Point2D[] pts;

    public CubicSpline2D( Point2D[] pts ) {
        this.pts = pts;
        update();
    }

    public void translateControlPoint( int i, double dx, double dy ) {
        setControlPoint( i, new Point2D.Double( pts[i].getX() + dx, pts[i].getY() + dy ) );
    }

    public Point2D getControlPoint( int i ) {
        return new Point2D.Double( pts[i].getX(), pts[i].getY() );
    }

    public void setControlPoint( int i, Point2D pt ) {
        pts[i] = new Point2D.Double( pt.getX(), pt.getY() );
        update();
    }

    private void update() {
        double[] s = new double[pts.length];
        double[] x = new double[pts.length];
        double[] y = new double[pts.length];
        for( int i = 0; i < pts.length; i++ ) {
            s[i] = ( 1.0 / ( pts.length - 1 ) ) * i;//*2.0;
            x[i] = pts[i].getX();
            y[i] = pts[i].getY();
        }
        s[s.length - 1] = Math.round( s[s.length - 1] );//to be exact, in case of roundoff error
        this.x = CubicSpline.interpolate( s, x );
        this.y = CubicSpline.interpolate( s, y );
        notifyTrackChanged();
    }

    private ArrayList listeners = new ArrayList();

    public int getNumControlPoints() {
        return pts.length;
    }

    public double getClosestPoint( Point2D pt ) {
//        return getClosestPointFlatSearch( pt );
        return getClosestPointBinarySearch( pt );
    }

    public double getClosestPoint( Line2D.Double line ) {
        return getClosestPointBinarySearch( line );
    }

    private double getClosestPointBinarySearch( Line2D.Double line ) {
        int numTestPts = 100;
        double distBetweenTestPts = 1.0 / numTestPts;
        Point2D center = new Point2D.Double( ( line.getX1() + line.getX2() ) / 2.0, ( line.getY1() + line.getY2() ) / 2.0 );
        double approx = getClosestPointFlatSearch( center, numTestPts );
        SearchPoint low = new SearchPoint( approx - distBetweenTestPts * 1.5, line );
        SearchPoint high = new SearchPoint( approx + distBetweenTestPts * 1.5, line );

        int iterations = 0;
        double threshold = 1E-6;
        while( true ) {
            SearchPoint mid = new SearchPoint( ( low.alpha + high.alpha ) / 2.0, line );
            SearchPoint[] pts = new SearchPoint[]{low, mid, high};
            Arrays.sort( pts );
            if( pts[0].alpha < pts[1].alpha ) {
                low = pts[0];
                high = pts[1];
            }
            else {
                low = pts[1];
                high = pts[0];
            }
            iterations++;
            if( iterations > 10 || Math.abs( low.alpha - high.alpha ) < threshold ) {
                break;
            }
            //take the best 2 as our new endpoints
        }
        return ( low.alpha + high.alpha ) / 2.0;
    }

    public AbstractVector2D getCurvatureDirection( double alpha ) {
        double epsilon = 0.001;
        Point2D a0 = evaluate( alpha - epsilon / 2.0 );
        Point2D a1 = evaluate( alpha + epsilon / 2.0 );
        Point2D center = evaluate( alpha );
        Point2D avg = new Point2D.Double( ( a0.getX() + a1.getX() ) / 2.0, ( a0.getY() + a1.getY() ) / 2.0 );
        Vector2D.Double dir = new Vector2D.Double( center, avg );
        AbstractVector2D vec = new Vector2D.Double( getUnitNormalVector( alpha ) );
        if( dir.dot( vec ) < 0 ) {
            vec = vec.getScaledInstance( -1.0 );
        }
        return vec;
    }

    class SearchPoint implements Comparable {
        double alpha;
        double dist;

        public SearchPoint( double alpha, Line2D lineSegment ) {
            this( alpha, lineSegment.ptLineDist( evaluate( alpha ) ) );//this is for the infinitely extended line
        }

        public SearchPoint( double alpha, Point2D pt ) {
            this( alpha, evaluate( alpha ).distance( pt ) );
        }

        public SearchPoint( double alpha, double dist ) {
            this.alpha = alpha;
            this.dist = dist;
        }

        public int compareTo( Object o ) {
            return new Double( dist ).compareTo( new Double( ( (SearchPoint)o ).dist ) );
        }

        public String toString() {
            return "alpha=" + alpha + ", dist=" + dist;
        }
    }

    private double getClosestPointBinarySearch( Point2D pt ) {
        int numTestPts = 100;
        double distBetweenTestPts = 1.0 / numTestPts;
        double approx = getClosestPointFlatSearch( pt, numTestPts );
        SearchPoint low = new SearchPoint( approx - distBetweenTestPts * 1.5, pt );
        SearchPoint high = new SearchPoint( approx + distBetweenTestPts * 1.5, pt );

        int iterations = 0;
        double threshold = 1E-6;
        while( true ) {
            SearchPoint mid = new SearchPoint( ( low.alpha + high.alpha ) / 2.0, pt );
            SearchPoint[] pts = new SearchPoint[]{low, mid, high};
            Arrays.sort( pts );
            if( pts[0].alpha < pts[1].alpha ) {
                low = pts[0];
                high = pts[1];
            }
            else {
                low = pts[1];
                high = pts[0];
            }
            iterations++;
            if( iterations > 10 || Math.abs( low.alpha - high.alpha ) < threshold ) {
                break;
            }
            //take the best 2 as our new endpoints
        }
        return ( low.alpha + high.alpha ) / 2.0;
    }

    private double getClosestPointFlatSearch( Point2D pt, int numSegments ) {
        double closestDist = Double.POSITIVE_INFINITY;
        double bestAlpha = Double.NaN;
        for( int k = 0; k < numSegments; k++ ) {
            double alpha = ( (double)k ) / numSegments;
            double dist = evaluate( alpha ).distance( pt );
            if( dist < closestDist ) {
                closestDist = dist;
                bestAlpha = alpha;
            }
        }
        return bestAlpha;
    }

    public static interface Listener {
        void trackChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyTrackChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.trackChanged();
        }
    }

    public Point2D[] getControlPoints() {
        Point2D[] a = new Point2D[pts.length];
        for( int i = 0; i < a.length; i++ ) {
            a[i] = new Point2D.Double( pts[i].getX(), pts[i].getY() );
        }
        return a;
    }

    public static CubicSpline2D interpolate( Point2D[] pts ) {
        return new CubicSpline2D( pts );
    }

    public Point2D evaluate( double alpha ) {
        return new Point2D.Double( x.evaluate( alpha ), y.evaluate( alpha ) );
    }

    public double getMetricDelta( double a0, double a1 ) {
//        return getMetricDeltaRecursive( a0, a1 );
        return getMetricDeltaIterative( a0, a1 );
    }

    public double getMetricDeltaIterative( double a0, double a1 ) {
        if( a1 == a0 ) {
            return 0;
        }
        if( a1 < a0 ) {
            return -getMetricDeltaIterative( a1, a0 );
        }
        int numSegments = 10;
        double da = ( a1 - a0 ) / ( numSegments - 1 );
        Point2D prev = evaluate( a0 );
        double sum = 0;
        for( int i = 1; i < numSegments; i++ ) {
            double a = a0 + i * da;
            Point2D pt = evaluate( a );
            double dist = pt.distance( prev );
            sum += dist;
            prev = pt;
        }
        return sum;
    }

    /*
   * Returns the metric distance between 2 fractional points.
    */
    public double getMetricDeltaRecursive( double a0, double a1 ) {
        if( a0 > a1 ) {
            return -getMetricDeltaRecursive( a1, a0 );
        }
        if( a0 == a1 ) {
            return 0;
        }
        double da = 1E-1;
        if( Math.abs( a0 - a1 ) < da ) {
            return evaluate( a0 ).distance( evaluate( a1 ) );
        }
        else {
            return getMetricDeltaRecursive( a0, a0 + da / 2 ) + getMetricDeltaRecursive( a0 + da / 2, a1 - da / 2 ) + getMetricDeltaRecursive( a1 - da / 2, a1 );
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
        double epsilon = 1E-6;
        int count = 0;
        while( Math.abs( metricDelta - ds ) > epsilon ) {
            if( metricDelta > ds ) {
                upperBound = guess;
            }
            else {
                lowerBound = guess;
            }
            guess = ( upperBound + lowerBound ) / 2.0;
            metricDelta = getMetricDelta( alpha0, guess );
            count++;
            if( count > 100 ) {
                System.out.println( "binary search failed: count=" + count );
                break;
            }
        }
//        System.out.println( "count = " + count );
        return guess - alpha0;
    }

    public AbstractVector2D getUnitParallelVector( double alpha ) {
        double epsilon = 1E-4;
//        double epsilon = 1E-6;
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


    public static void main( String[] args ) {

        CubicSpline2D cubicSpline2D = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 1, 1 ),
                new Point2D.Double( 2, 0 )
        } );
        for( double s = 0.0; s < 1.0; s += 0.1 ) {
            Point2D at = cubicSpline2D.evaluate( s );
            System.out.println( "s = " + s + ", at=" + at );
        }
        double delta = cubicSpline2D.getMetricDelta( 0, 1 );
        System.out.println( "Spline length=" + delta );

        double fracDist = cubicSpline2D.getFractionalDistance( 0, delta );
        System.out.println( "fracDist = " + fracDist );

    }

}
