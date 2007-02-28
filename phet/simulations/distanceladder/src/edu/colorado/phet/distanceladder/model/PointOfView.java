/**
 * Class: PointOfView
 * Class: edu.colorado.phet.distanceladder.model
 * User: Ron LeMaster
 * Date: Apr 8, 2004
 * Time: 11:45:07 AM
 */
package edu.colorado.phet.distanceladder.model;

import java.awt.geom.Point2D;

public class PointOfView extends Point2D.Double {
    private double theta;

    public PointOfView() {
    }

    public PointOfView( PointOfView pov ) {
        this( pov.getX(), pov.getY(), pov.getTheta() );
    }

    public PointOfView( double x, double y, double theta ) {
        super( x, y );
        this.theta = theta;
    }

    public PointOfView( Point2D povPt, double povTheta ) {
        super( povPt.getX(), povPt.getY() );
        this.theta = povTheta;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta( double theta ) {
        this.theta = theta;
    }

    public void setPointOfView( PointOfView pov ) {
        setLocation( pov );
        setTheta( pov.getTheta() );
    }
}
