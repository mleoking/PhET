/**
 * Class: Point2DPolar
 * Class: edu.colorado.games4education.lostinspace.model
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 8:29:18 AM
 */
package edu.colorado.games4education.lostinspace.model;

import java.awt.geom.Point2D;

public class Point2DPolar {
    private double r;
    private double theta;

    public Point2DPolar( double r, double theta ) {
        this.r = r;
        this.theta = theta;
    }

    public Point2DPolar( Point2D.Double cartCoords, Point2D.Double polarOrigin ) {
        r = polarOrigin.distance( cartCoords );

        double dx = cartCoords.getX() - polarOrigin.getX();
        double dy = cartCoords.getY() - polarOrigin.getY();
        if( dx == 0 ) {
            if( dy > 0 ) {
                theta = Math.PI / 2;
            }
            else {
                theta = Math.PI * 3 / 2;
            }
        }
        else {
            theta = Math.atan( dy / dx );
            theta = ( dx < 0 ? theta + Math.PI / 2 : theta );
        }
    }

    public Point2D.Double toPoint2D( Point2D polarOrigin ) {
        double x = polarOrigin.getX() + getR() * Math.cos( getTheta() );
        double y = polarOrigin.getY() + getR() * Math.sin( getTheta() );
        return new Point2D.Double( x, y );
    }

    public double getR() {
        return r;
    }

    public double getTheta() {
        return theta;
    }
}
