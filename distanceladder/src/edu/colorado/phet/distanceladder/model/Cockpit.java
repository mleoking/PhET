/**
 * Class: Cockpit
 * Class: edu.colorado.phet.distanceladder.model
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 8:33:05 PM
 */
package edu.colorado.phet.distanceladder.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.Body;

import java.awt.geom.Point2D;

public class Cockpit extends Body {
    private Point2D.Double location = new Point2D.Double();
    private double orientation;

    public Cockpit( StarField starField ) {
//        starView = new StarView( starField, Math.PI / 2 );
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public void setLocation( Point2D.Double location ) {
        this.location = location;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation( double orientation ) {
        this.orientation = orientation;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    public void moveIncr( double dx, double dy ) {
        location.setLocation( location.getX() + dx,  location.getY() + dy );
    }
}
