/**
 * Class: Point2DPolar
 * Class: edu.colorado.phet.distanceladder.model
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 8:29:18 AM
 */
package edu.colorado.phet.distanceladder.model;

import java.awt.geom.Point2D;

public class Point2DPolar {
    private double r;
    private double theta;
    private Point2D.Double utilPt = new Point2D.Double( );

    public Point2DPolar( double r, double theta ) {
        this.r = r;
        this.theta = theta;
    }

    public Point2DPolar( Point2D.Double cartCoords, Point2D.Double polarOrigin ) {
        r = polarOrigin.distance( cartCoords );
        double dx = cartCoords.getX() - polarOrigin.getX();
        double dy = cartCoords.getY() - polarOrigin.getY();
        theta = Math.atan2( dy, dx );
    }

    public Point2DPolar( Point2D.Double cartCoords, double polarOriginX, double polarOriginY ) {
        this( cartCoords, new Point2D.Double( polarOriginX, polarOriginY ));
    }

    /**
     * Creates a Point2DPolar with components relative to a specified PointOfView.
     * That is, r is the distance between the specifried point in cartesian space,
     * and the specified PointOfView. Theta is the angular distance between the specified
     * cartesian coordinates and the direction of the PointOfView
     * @param cartCoords
     * @param pov
     */
    public Point2DPolar( Point2D.Double cartCoords, PointOfView pov ) {
        this( cartCoords, (Point2D.Double)pov );
        this.theta = this.theta - pov.getTheta();        
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

    public static void main( String[] args ) {
        Point2D.Double s = new Point2D.Double( 100, 100 );
        Point2D.Double p = new Point2D.Double( 0, 0 );
        Point2DPolar pp = new Point2DPolar( s, p );
        s.setLocation( s.getX() + 1, s.getY() + 1 );
        pp = new Point2DPolar( s, p );
        return;
    }
}
