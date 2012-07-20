// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.spline;

import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * User: Sam Reid
 * Date: Feb 14, 2007
 * Time: 3:09:47 AM
 */

public abstract class ParametricFunction2D implements Serializable {

    public static int count = 0;
    public final int index = count++;

    public double getClosestPoint( SerializablePoint2D pt ) {
        return getClosestPointBinarySearch( pt );
    }

    public double getClosestPoint( Line2D.Double line ) {
        return getClosestPointBinarySearch( line );
    }

    private double getClosestPointBinarySearch( Line2D.Double line ) {
        int numTestPts = 100;
        double distBetweenTestPts = 1.0 / numTestPts;
        SerializablePoint2D center = new SerializablePoint2D( ( line.getX1() + line.getX2() ) / 2.0, ( line.getY1() + line.getY2() ) / 2.0 );
        double approx = getClosestPointFlatSearch( center, numTestPts );//todo: could do flat search for line
        SearchPoint low = new SearchPoint( approx - distBetweenTestPts * 1.5, line );
        SearchPoint high = new SearchPoint( approx + distBetweenTestPts * 1.5, line );

        int iterations = 0;
        double threshold = 1E-9;
        while ( true ) {
            SearchPoint mid = new SearchPoint( ( low.alpha + high.alpha ) / 2.0, line );
            SearchPoint[] pts = new SearchPoint[] { low, mid, high };
            Arrays.sort( pts );
            if ( pts[0].alpha < pts[1].alpha ) {
                low = pts[0];
                high = pts[1];
            }
            else {
                low = pts[1];
                high = pts[0];
            }
            iterations++;
            if ( iterations > 50 || Math.abs( low.alpha - high.alpha ) < threshold ) {
                break;
            }
            //take the best 2 as our new endpoints
        }
        return ( low.alpha + high.alpha ) / 2.0;
    }

    public Vector2D getCurvatureDirection( double alpha ) {
        double epsilon = 0.001;
        SerializablePoint2D a0 = evaluate( alpha - epsilon / 2.0 );
        SerializablePoint2D a1 = evaluate( alpha + epsilon / 2.0 );
        SerializablePoint2D center = evaluate( alpha );
        SerializablePoint2D avg = new SerializablePoint2D( ( a0.getX() + a1.getX() ) / 2.0, ( a0.getY() + a1.getY() ) / 2.0 );
        MutableVector2D dir = new MutableVector2D( center, avg );
        Vector2D vec = new Vector2D( getUnitNormalVector( alpha ) );
        if ( dir.dot( vec ) < 0 ) {
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

        public SearchPoint( double alpha, SerializablePoint2D pt ) {
            this( alpha, evaluate( alpha ).distance( pt ) );
        }

        public SearchPoint( double alpha, double dist ) {
            this.alpha = alpha;
            this.dist = dist;
        }

        public int compareTo( Object o ) {
            return new Double( dist ).compareTo( new Double( ( (SearchPoint) o ).dist ) );
        }

        public String toString() {
            return "alpha=" + alpha + ", dist=" + dist;
        }
    }

    private double getClosestPointBinarySearch( SerializablePoint2D pt ) {
        int numTestPts = 100;
        double distBetweenTestPts = 1.0 / numTestPts;
        double approx = getClosestPointFlatSearch( pt, numTestPts );
        SearchPoint low = new SearchPoint( approx - distBetweenTestPts * 1.5, pt );
        SearchPoint high = new SearchPoint( approx + distBetweenTestPts * 1.5, pt );

        int iterations = 0;
        double threshold = 1E-6;
        while ( true ) {
            SearchPoint mid = new SearchPoint( ( low.alpha + high.alpha ) / 2.0, pt );
            SearchPoint[] pts = new SearchPoint[] { low, mid, high };
            Arrays.sort( pts );
            if ( pts[0].alpha < pts[1].alpha ) {
                low = pts[0];
                high = pts[1];
            }
            else {
                low = pts[1];
                high = pts[0];
            }
            iterations++;
            if ( iterations > 30 || Math.abs( low.alpha - high.alpha ) < threshold ) {
                break;
            }
            //take the best 2 as our new endpoints
        }
        return ( low.alpha + high.alpha ) / 2.0;
    }

    private double getClosestPointFlatSearch( SerializablePoint2D pt, int numSegments ) {
        double closestDist = Double.POSITIVE_INFINITY;
        double bestAlpha = Double.NaN;
        for ( int k = 0; k < numSegments; k++ ) {
            double alpha = ( (double) k ) / numSegments;
            double dist = evaluate( alpha ).distance( pt );
            if ( dist < closestDist ) {
                closestDist = dist;
                bestAlpha = alpha;
            }
        }
        return bestAlpha;
    }

    public static interface Listener {
        void trackChanged();
    }

    public abstract SerializablePoint2D evaluate( double alpha );

    public double getMetricDelta( double a0, double a1 ) {
//        return getMetricDeltaRecursive( a0, a1 );
        return getMetricDeltaIterative( a0, a1 );
    }

    public double getMetricDeltaIterative( double a0, double a1 ) {
        if ( a1 == a0 ) {
            return 0;
        }
        if ( a1 < a0 ) {
            return -getMetricDeltaIterative( a1, a0 );
        }
        int numSegments = 10;
        double da = ( a1 - a0 ) / ( numSegments - 1 );
        SerializablePoint2D prev = evaluate( a0 );
        double sum = 0;
        for ( int i = 1; i < numSegments; i++ ) {
            double a = a0 + i * da;
            SerializablePoint2D pt = evaluate( a );
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
        if ( a0 > a1 ) {
            return -getMetricDeltaRecursive( a1, a0 );
        }
        if ( a0 == a1 ) {
            return 0;
        }
        double da = 1E-1;
        if ( Math.abs( a0 - a1 ) < da ) {
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
        while ( Math.abs( metricDelta - ds ) > epsilon ) {
            if ( metricDelta > ds ) {
                upperBound = guess;
            }
            else {
                lowerBound = guess;
            }
            guess = ( upperBound + lowerBound ) / 2.0;
            metricDelta = getMetricDelta( alpha0, guess );
            count++;
            if ( count > 100 ) {
                System.out.println( "binary search failed: count=" + count );
                break;
            }
        }
//        EnergySkateParkLogging.println( "count = " + count );
        return guess - alpha0;
    }

    public Vector2D getUnitParallelVector( double alpha ) {
//        double epsilon = 1E-4;
        double epsilon = 1E-8;
//        double epsilon = 1E-6;
        double a0 = alpha - epsilon / 2;
        double a1 = alpha + epsilon / 2;
//        EnergySkateParkLogging.println( "a0 = " + a0 +", a1="+a1);

        MutableVector2D vector = new MutableVector2D( evaluate( a0 ), evaluate( a1 ) );
        if ( vector.getMagnitude() == 0 ) {
            throw new RuntimeException( "unit parallel vector failed: alpha=" + alpha + ", eval=" + evaluate( alpha ) );
        }
        return vector.getNormalizedInstance();
    }

    public Vector2D getUnitNormalVector( double alpha ) {
        return getUnitParallelVector( alpha ).getNormalVector();
    }

    public double getAngle( double alpha ) {
        return getUnitParallelVector( alpha ).getAngle();
    }


    public SerializablePoint2D getOffsetPoint( double alpha, double dist, boolean top ) {
        return new SerializablePoint2D( getUnitNormalVector( alpha ).getInstanceOfMagnitude( dist * ( top ? 1 : -1 ) ).getDestination( evaluate( alpha ) ) );
    }

    public SerializablePoint2D[] getOffsetSpline( double dist, boolean top, int numPts ) {
        double alpha = 0;
        double dAlpha = 1.0 / ( numPts - 1 );
        SerializablePoint2D[] pts = new SerializablePoint2D[numPts];
        for ( int i = 0; i < numPts; i++ ) {
            pts[i] = getOffsetPoint( alpha, dist, top );
            alpha += dAlpha;
        }
        return pts;
    }
}
