/**
 * Class: Cockpit
 * Class: edu.colorado.games4education.lostinspace.model
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 8:33:05 PM
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Point2D;

public class Cockpit implements ModelElement {

    private Point2D.Double location = new Point2D.Double();
    private double orientation;
    

    public Point2D.Double getLocation() {
        return location;
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
    }
}
