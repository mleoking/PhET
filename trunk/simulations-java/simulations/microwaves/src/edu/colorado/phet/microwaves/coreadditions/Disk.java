/**
 * Class: Disk
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 26, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import java.awt.geom.Point2D;

public class Disk extends Body {

    private double radius;

    public Disk( Point2D.Double center, double radius ) {
        setLocation( center.getX(), center.getY() );
        this.radius = radius;
    }

    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        // MR^2 / 2. We assume mass is equal to area
        // TODO: fix!!!
        double mass = radius * radius * Math.PI;
        return radius * radius * mass / 2;
    }

    public double getRadius() {
        return radius;
    }

//    public double getMass() {
//        return crosshairRadius * crosshairRadius * Math.PI;
//    }
}
