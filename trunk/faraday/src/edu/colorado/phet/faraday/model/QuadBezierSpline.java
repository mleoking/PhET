/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * QuadraticBezierSpline is a quadratic bezier spline, described by a start point,
 * and end point, and a control point.  de Caselijau's algorithm is used to find
 * points along the curve.
 *
 * @author Sam Reid, Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QuadBezierSpline {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point _startPoint;
    private Point _controlPoint;
    private Point _endPoint;

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
        _startPoint = startPoint;
        _controlPoint = controlPoint;
        _endPoint = endPoint;
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
     * @return the point
     */
    public Point2D evaluate( double t ) {
        double x = ( _startPoint.x * t * t ) + ( _controlPoint.x * 2 * t * ( 1 - t ) ) + ( _endPoint.x * ( 1 - t ) * ( 1 - t ) );
        double y = ( _startPoint.y * t * t ) + ( _controlPoint.y * 2 * t * ( 1 - t ) ) + ( _endPoint.y * ( 1 - t ) * ( 1 - t ) );
        return new Point2D.Double( x, y );
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
        for ( double t = 0.0; t <= 1.0; t += detailBias ) {
            Point2D pt = evaluate( t );
            if ( path == null ) {
                path = new GeneralPath();
                path.moveTo( (float) pt.getX(), (float) pt.getY() );
            }
            else {
                path.lineTo( (float) pt.getX(), (float) pt.getY() );
            }
        }
        return path;
    }
}