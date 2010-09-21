/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.util;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;


/**
 * QuadraticBezierSpline is a quadratic bezier spline, described by a start point,
 * an end point, and a control point.  de Caselijau's algorithm is used to find
 * points along the curve.
 *
 * @author Sam Reid, Chris Malley (cmalley@pixelzoom.com)
 */
public class QuadBezierSpline extends QuadCurve2D.Double {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param startPoint the starting point
     * @param controlPoint the control point
     * @param endPoint the end point
     */
    public QuadBezierSpline( Point startPoint, Point controlPoint, Point endPoint ) {
        super( startPoint.x, startPoint.y, controlPoint.x, controlPoint.y, endPoint.x, endPoint.y );
    }
    
    //----------------------------------------------------------------------------
    // Misc.
    //----------------------------------------------------------------------------
    
    /**
     * Uses the de Castelijau algorithm to determines the point that is some
     * fraction t of the way along the curve from the start point to the end point.
     * t=1 is at the start point, and t=0 is at the end point.
     * 
     * @param t a value between 0 and 1
     * @param pointDst point that stores the result (optional)
     * @return the point
     * @throws IllegalArgumentException if t is out of range
     */
    public Point2D evaluate( double t, Point2D pointDst ) {
        if ( t < 0 || t > 1 ) {
            throw new IllegalArgumentException( "t is out of range: " + t );
        }
        double x = ( getX1() * t * t ) + ( getCtrlX() * 2 * t * ( 1 - t ) ) + ( getX2() * ( 1 - t ) * ( 1 - t ) );
        double y = ( getY1() * t * t ) + ( getCtrlY() * 2 * t * ( 1 - t ) ) + ( getY2() * ( 1 - t ) * ( 1 - t ) );
        if ( pointDst == null ) {
            pointDst = new Point2D.Double();
        }
        pointDst.setLocation( x, y );
        return pointDst;
    }
    
    /**
     * Converts the curve to a path have a specified number of line segments.
     * 
     * @param numberOfLines the number of line segments
     * @return a GeneralPath
     */
    public GeneralPath toGeneralPath( int numberOfLines ) {
        double detailBias = 1.0 / numberOfLines;
        GeneralPath path = null;
        Point2D p = new Point2D.Double();
        for ( double t = 0.0; t <= 1.0; t += detailBias ) {
            evaluate( t, p /* output */ );
            if ( path == null ) {
                path = new GeneralPath();
                path.moveTo( (float) p.getX(), (float) p.getY() );
            }
            else {
                path.lineTo( (float) p.getX(), (float) p.getY() );
            }
        }
        return path;
    }
}