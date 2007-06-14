/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/math/Point2DPolar.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */
package edu.colorado.phet.common.math;

import java.awt.geom.Point2D;

/**
 * A class for 2-dimensional points expressed in polar coordinates.
 */
public class Point2DPolar {
    private double r;
    private double theta;
    private Point2D.Double utilPt = new Point2D.Double();

    /**
     * @param r
     * @param theta
     */
    public Point2DPolar( double r, double theta ) {
        this.r = r;
        this.theta = theta;
    }

    /**
     * Creates a Point2DPolar for a Point2D, assuming the origin of the polar coordinate
     * system is at the origin of the Point2D's cartesian coordinate system
     *
     * @param cartCoords
     */
    public Point2DPolar( Point2D cartCoords ) {
        this( cartCoords, 0, 0 );
    }

    /**
     * @param cartCoords
     * @param polarOrigin The origin of the the polar coordinate space, expressed in
     *                    cartesian coordinates
     */
    public Point2DPolar( Point2D cartCoords, Point2D polarOrigin ) {
        r = polarOrigin.distance( cartCoords );
        double dx = cartCoords.getX() - polarOrigin.getX();
        double dy = cartCoords.getY() - polarOrigin.getY();
        theta = Math.atan2( dy, dx );
    }

    /**
     * @param cartCoords
     * @param polarOriginX
     * @param polarOriginY
     */
    public Point2DPolar( Point2D cartCoords, double polarOriginX, double polarOriginY ) {
        this( cartCoords, new Point2D.Double( polarOriginX, polarOriginY ) );
    }

    /**
     * @param polarOrigin
     * @return the point2D
     */
    public Point2D.Double toPoint2D( Point2D polarOrigin ) {
        double x = polarOrigin.getX() + getR() * Math.cos( getTheta() );
        double y = polarOrigin.getY() + getR() * Math.sin( getTheta() );
        return new Point2D.Double( x, y );
    }

    /**
     * @return the distance from the origin
     */
    public double getR() {
        return r;
    }

    /**
     * @param r
     */
    public void setR( double r ) {
        this.r = r;
    }

    /**
     * @return the angle
     */
    public double getTheta() {
        return theta;
    }

    /**
     * @param theta
     */
    public void setTheta( double theta ) {
        this.theta = theta;
    }
}
