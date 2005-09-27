/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:37 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public abstract class AbstractSpline {
    private ArrayList points = new ArrayList();

    protected AbstractSpline() {
    }

    public void addControlPoint( Point2D point ) {
        points.add( point );
    }

    public void addControlPoint( double x, double y ) {
        addControlPoint( new Point2D.Double( x, y ) );
    }

    public abstract Point2D[] getInterpolationPoints();

    public Point2D[] getControlPoints() {
        return (Point2D[])points.toArray( new Point2D.Double[0] );
    }

    public GeneralPath getInterpolationPath() {
        Point2D[] pts = getInterpolationPoints();
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( pts[0].getX(), pts[0].getY() );
        for( int i = 1; i < pts.length; i++ ) {
            path.lineTo( pts[i] );
        }
        return path.getGeneralPath();
    }

    public int numControlPoints() {
        return points.size();
    }

    public Point2D controlPointAt( int i ) {
        return (Point2D)points.get( i );
    }

    public void translateControlPoint( int index, double x, double y ) {
        Point2D.Double pt = (Point2D.Double)points.get( index );
        pt.x += x;
        pt.y += y;
        //todo notify this moved.
    }
}
