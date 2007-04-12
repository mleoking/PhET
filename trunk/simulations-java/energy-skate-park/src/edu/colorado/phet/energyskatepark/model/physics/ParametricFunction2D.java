package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Feb 14, 2007
 * Time: 3:09:47 AM
 * Copyright (c) Feb 14, 2007 by Sam Reid
 */

public abstract class ParametricFunction2D implements Cloneable, Serializable {

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
        double approx = getClosestPointFlatSearch( center, numTestPts );//todo: could do flat search for line
        SearchPoint low = new SearchPoint( approx - distBetweenTestPts * 1.5, line );
        SearchPoint high = new SearchPoint( approx + distBetweenTestPts * 1.5, line );

        int iterations = 0;
        double threshold = 1E-9;
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
            if( iterations > 50 || Math.abs( low.alpha - high.alpha ) < threshold ) {
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

    public abstract String toStringSerialization();

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
            if( iterations > 30 || Math.abs( low.alpha - high.alpha ) < threshold ) {
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

    public abstract Point2D evaluate( double alpha );

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
        double epsilon = 1E-8;
//        double epsilon = 1E-10;

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
//        double epsilon = 1E-4;
        double epsilon = 1E-8;
//        double epsilon = 1E-6;
        double a0 = alpha - epsilon / 2;
        double a1 = alpha + epsilon / 2;
//        System.out.println( "a0 = " + a0 +", a1="+a1);

        Vector2D.Double vector = new Vector2D.Double( evaluate( a0 ), evaluate( a1 ) );
        if( vector.getMagnitude() == 0 ) {
            throw new RuntimeException( "unit parallel vector failed: alpha=" + alpha + ", eval=" + evaluate( alpha ) );
        }
        return vector.getNormalizedInstance();
    }

    public AbstractVector2D getUnitNormalVector( double alpha ) {
        return getUnitParallelVector( alpha ).getNormalVector();
    }

    public double getAngle( double alpha ) {
        return getUnitParallelVector( alpha ).getAngle();
    }


    public Point2D getOffsetPoint( double alpha, double dist, boolean top ) {
        return getUnitNormalVector( alpha ).getInstanceOfMagnitude( dist * ( top ? 1 : -1 ) ).getDestination( evaluate( alpha ) );
    }

    public Point2D[] getOffsetSpline( double dist, boolean top, int numPts ) {
        double alpha = 0;
        double dAlpha = 1.0 / ( numPts - 1 );
        Point2D[] pts = new Point2D[numPts];
        for( int i = 0; i < numPts; i++ ) {
            pts[i] = getOffsetPoint( alpha, dist, top );
            alpha += dAlpha;
        }
        return pts;
    }
}
