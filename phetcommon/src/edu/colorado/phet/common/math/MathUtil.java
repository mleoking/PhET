/*
 * Class: MathUtil
 * Package: edu.colorado.phet.common.imaging
 *
 * Created by: Ron LeMaster
 * Date: Dec 12, 2002
 */
package edu.colorado.phet.common.math;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A class of static general purpose math utilities
 */
public class MathUtil {

    /**
     * Returns +1 or -1, reflecting the sign of the argument
     *
     * @param d
     * @return +1 or -1
     */
    public static int getSign( double d ) {
        if( d != 0
            && !Double.isNaN( d )
            && !Double.isInfinite( d ) ) {
            return (int)( Math.abs( d ) / d );
        }
        else {
            return 1;
        }
    }

    /**
     * Finds the roots of a quadratic equation.
     *
     * @param quadRoots An array in which the roots are to be returned
     * @param a
     * @param b
     * @param c
     * @return The roots of the equation, if not imaginary. Otherwise,
     *         returns NaN
     */
    public static float[] quadraticRoots( float[] quadRoots, float a, float b, float c ) {
        float sqrt = (float)Math.sqrt( ( b * b ) - 4 * a * c );

        quadRoots[0] = ( -b + sqrt ) / ( 2 * a );
        quadRoots[1] = ( -b - sqrt ) / ( 2 * a );
        return quadRoots;
    }

    /**
     * Computes the roots of a quadratic equation
     *
     * @param a
     * @param b
     * @param c
     * @return the roots of a quadratic equation
     */
    public static float[] quadraticRoots( float a, float b, float c ) {
        float[] roots = new float[2];
        return quadraticRoots( roots, a, b, c );
    }

    /**
     * Tells if two float values are equal, within a specified tolerance
     *
     * @param x
     * @param y
     * @param eps The tolerance to be applied to the equality test
     * @return true if the two values are within epsilon of each other (exclusive).
     */
    public static boolean isApproxEqual( float x, float y, float eps ) {
        return Math.abs( x - y ) < eps;
    }

    /**
     * Finds the positione of the apparent reflection of a point across a line
     *
     * @param p
     * @param linePt1
     * @param lineAngle
     * @return the position of the reflection of a point across a line.
     */
    public static Point2D.Double reflectPointHorizontal( Point2D p, Point2D linePt1, double lineAngle ) {

        double bx = linePt1.getX() + ( linePt1.getY() - p.getY() ) / Math.tan( Math.toRadians( lineAngle ) );

        // Note that this line is what constrains this to a horizontal reflection
        double by = p.getY();
        Point2D b = new Point2D.Double( bx, by );
        double d = p.distance( b );
        double beta = 180 - 2 * lineAngle;
        double ppx = bx + d * Math.cos( Math.toRadians( beta ) );
        double ppy = by + d * Math.sin( Math.toRadians( beta ) );
        Point2D.Double pp = new Point2D.Double( ppx, ppy );
        return pp;
    }

    /**
     * Computes the intersection of two line segments.
     *
     * @param l1
     * @param l2
     * @return the intersection of two line segments.
     */
    public static Point2D.Double getLineSegmentsIntersection( Line2D l1, Line2D l2 ) {
        return getLineSegmentsIntersection( l1.getP1(), l1.getP2(), l2.getP1(), l2.getP2() );
    }

    /**
     * Computes the intersection of two line segments. Algorithm taked from Paul Bourke, 1989:
     * http://astronomy.swin.edu.au/~pbourke/geometry/lineline2d/
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return the intersection of two line segments.
     */
    public static Point2D.Double getLineSegmentsIntersection( Point2D p1, Point2D p2,
                                                              Point2D p3, Point2D p4 ) {
        Point2D.Double result = new Point2D.Double();
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double numA = ( x4 - x3 ) * ( y1 - y3 ) - ( y4 - y3 ) * ( x1 - x3 );
        double numB = ( x2 - x1 ) * ( y1 - y3 ) - ( y2 - y1 ) * ( x1 - x3 );
        double denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // If denominator is 0, the lines are parallel or coincident
        if( denom == 0 ) {
            result.setLocation( Float.NaN, Float.NaN );
        }
        else {
            double ua = numA / denom;
            double ub = numB / denom;

            // ua and ub must both be in the range 0 to 1 for the segments
            // to have an interesection pt.
            if( !( ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1 ) ) {
                result.setLocation( Float.NaN, Float.NaN );
            }
            else {
                double x = x1 + ua * ( x2 - x1 );
                double y = y1 + ua * ( y2 - y1 );
                result.setLocation( x, y );
            }
        }
        return result;
    }

    /**
     * Computes the intersection of a line segment with a line. Algorithm taked from Paul Bourke, 1989:
     * http://astronomy.swin.edu.au/~pbourke/geometry/lineline2d/
     *
     * @param p1 Endpoint of line segment
     * @param p2 Other endpoint of line segment
     * @param p3 Point on line
     * @param p4 Point on line
     * @return the intersection of a segment with a line.
     */
    public static Point2D.Double getSegmentLineIntersection( Point2D.Double p1, Point2D.Double p2,
                                                             Point2D.Double p3, Point2D.Double p4 ) {
        Point2D.Double result = new Point2D.Double();
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double numA = ( x4 - x3 ) * ( y1 - y3 ) - ( y4 - y3 ) * ( x1 - x3 );
        double numB = ( x2 - x1 ) * ( y1 - y3 ) - ( y2 - y1 ) * ( x1 - x3 );
        double denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // If denominator is 0, the lines are parallel or coincident
        if( denom == 0 ) {
            result.setLocation( Float.NaN, Float.NaN );
        }
        else {
            double ua = numA / denom;
            double ub = numB / denom;

            // ua and ub must both be in the range 0 to 1 for the segments
            // to have an interesection pt.
            if( !( ub >= 0 && ub <= 1 ) ) {
                result.setLocation( Double.NaN, Double.NaN );
            }
            else {
                double x = x1 + ua * ( x2 - x1 );
                double y = y1 + ua * ( y2 - y1 );
                result.setLocation( x, y );
            }
        }
        return result;
    }

    /**
     * Computes the intersection of two lines. Algorithm taked from Paul Bourke, 1989:
     * http://astronomy.swin.edu.au/~pbourke/geometry/lineline2d/
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return the intersection of two lines.
     */
    public static Point2D.Double getLinesIntersection( Point2D.Double p1, Point2D.Double p2,
                                                       Point2D.Double p3, Point2D.Double p4 ) {
        Point2D.Double result = new Point2D.Double();
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();

        double numA = ( x4 - x3 ) * ( y1 - y3 ) - ( y4 - y3 ) * ( x1 - x3 );
        double denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // If denominator is 0, the lines are parallel or coincident
        if( denom == 0 ) {
            result.setLocation( Double.NaN, Double.NaN );
        }
        else {
            double ua = numA / denom;
            double x = x1 + ua * ( x2 - x1 );
            double y = y1 + ua * ( y2 - y1 );
            result.setLocation( x, y );
        }
        return result;
    }


    /**
     * Tells it a line segment is intersected by a line. Algorithm taked from Paul Bourke, 1989:
     * http://astronomy.swin.edu.au/~pbourke/geometry/lineline2d/
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return true if a segment intersects a line.
     */
    public static boolean segmentIntersectsLine( float x1, float y1,
                                                 float x2, float y2,
                                                 float x3, float y3,
                                                 float x4, float y4 ) {
        boolean result = false;
        float numA = ( x4 - x3 ) * ( y1 - y3 ) - ( y4 - y3 ) * ( x1 - x3 );
        float denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // If denominator is 0, the lines are parallel or coincident
        if( denom == 0 ) {
            result = false;
        }
        else {
            float ua = numA / denom;
            // ub must both be in the range 0 to 1 for the segment to
            // to interesect the line
            result = ua >= 0 && ua <= 1;
        }
        return result;
    }

    /**
     * @param m1
     * @param b1
     * @param m2
     * @param b2
     * @return the intersection of two lines.
     */
    public static Point2D.Float getLinesIntersection( float m1, float b1, float m2, float b2 ) {

        Point2D.Float result = new Point2D.Float( 0, 0 );
        if( m1 == m2 ) {
            result.setLocation( Float.NaN, Float.NaN );
        }
        else if( m1 == Float.NEGATIVE_INFINITY || m1 == Float.POSITIVE_INFINITY ) {
            // Handle vertical lines!!
            throw new RuntimeException( "Method does not handle vertical lines yet" );
        }
        else {
            float x = ( b2 - b1 ) / ( m1 - m2 );
            float y = m1 * x + b1;
            result.setLocation( x, y );
        }
        return result;
    }
}
