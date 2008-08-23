/* Copyright 2002-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.forces1d.common_force1d.math;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A class of static general purpose math utilities
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MathUtil {

    /**
     * Returns the logarithm of a number to a specified base.
     *
     * @param number
     * @param base
     * @return
     */
    public static double logBaseX( double number, double base ) {
        return Math.log( number ) / Math.log( base );
    }

    /**
     * Returns +1 or -1, reflecting the sign of the argument
     *
     * @param d
     * @return +1 or -1
     */
    public static int getSign( double d ) {
        if ( d != 0
             && !Double.isNaN( d )
             && !Double.isInfinite( d ) ) {
            return (int) ( Math.abs( d ) / d );
        }
        else {
            return 1;
        }
    }

    /**
     * Returns +1 or -1, reflecting the sign of the argument
     *
     * @param f
     * @return +1 or -1
     */
    public static int getSign( float f ) {
        if ( f != 0
             && !Float.isNaN( f )
             && !Float.isInfinite( f ) ) {
            return (int) ( Math.abs( f ) / f );
        }
        else {
            return 1;
        }
    }

    /**
     * Returns +1 or -1, reflecting the sign of the argument
     *
     * @param i
     * @return +1 or -1
     */
    public static int getSign( int i ) {
        return i >= 0 ? 1 : -1;
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
        float sqrt = (float) Math.sqrt( ( b * b ) - 4 * a * c );

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
     * Determines the position of a point that is the reflection of a specified point across a line.
     *
     * @param p
     * @param ptOnLine
     * @param lineAngle Anlge of line in radians
     * @return the reflected point.
     */
    public static Point2D reflectPointAcrossLine( Point2D p, Point2D ptOnLine, double lineAngle ) {

        double alpha = lineAngle % ( Math.PI * 2 );
        Point2D l = ptOnLine;
        double gamma = Math.atan2( ( p.getY() - l.getY() ), ( p.getX() - l.getX() ) ) % ( Math.PI * 2 );
        double theta = ( 2 * alpha - gamma ) % ( Math.PI * 2 );
        double d = p.distance( l );
        Point2D.Double e = new Point2D.Double( l.getX() + d * Math.cos( theta ),
                                               l.getY() + d * Math.sin( theta ) );
        return e;
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
        if ( denom == 0 ) {
            result.setLocation( Float.NaN, Float.NaN );
        }
        else {
            double ua = numA / denom;
            double ub = numB / denom;

            // ua and ub must both be in the range 0 to 1 for the segments
            // to have an interesection pt.
            if ( !( ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1 ) ) {
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
    public static Point2D.Double getSegmentLineIntersection( Point2D p1, Point2D p2,
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
        if ( denom == 0 ) {
            result.setLocation( Float.NaN, Float.NaN );
        }
        else {
            double ua = numA / denom;
            double ub = numB / denom;

            // ua and ub must both be in the range 0 to 1 for the segments
            // to have an interesection pt.
            if ( !( ub >= 0 && ub <= 1 ) ) {
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
    public static Point2D.Double getLinesIntersection( Point2D p1, Point2D p2,
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
        double denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // If denominator is 0, the lines are parallel or coincident
        if ( denom == 0 ) {
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
    public static boolean segmentIntersectsLine( double x1, double y1,
                                                 double x2, double y2,
                                                 double x3, double y3,
                                                 double x4, double y4 ) {
        boolean result = false;
        double numA = ( x4 - x3 ) * ( y1 - y3 ) - ( y4 - y3 ) * ( x1 - x3 );
        double denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // If denominator is 0, the lines are parallel or coincident
        if ( denom == 0 ) {
            result = false;
        }
        else {
            double ua = numA / denom;
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
        if ( m1 == m2 ) {
            result.setLocation( Float.NaN, Float.NaN );
        }
        else if ( m1 == Float.NEGATIVE_INFINITY || m1 == Float.POSITIVE_INFINITY ) {
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

    /**
     * Clamps a value to a specified range.
     *
     * @param min   the minimum value
     * @param value the value to be clamped
     * @param max   the maximum value
     * @return the clamped value
     */
    public static double clamp( double min, double value, double max ) {
        if ( Double.isNaN( min ) || Double.isNaN( value ) || Double.isNaN( max ) ) {
            return Double.NaN;
        }
        else if ( value < min ) {
            return min;
        }
        else if ( value > max ) {
            return max;
        }
        return value;
    }


}
