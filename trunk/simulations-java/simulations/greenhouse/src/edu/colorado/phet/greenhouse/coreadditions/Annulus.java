/**
 * Class: Annulus
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions;

import java.awt.geom.Point2D;

public class Annulus extends Body {

    private Point2D.Double center;
    private double innerDiameter;
    private double outerDiameter;

    public Annulus( Point2D.Double center, double innerDiameter, double outerDiameter ) {
        this.center = center;
        this.innerDiameter = innerDiameter;
        this.outerDiameter = outerDiameter;
    }

    public double distanceFromInnerDiameter( Point2D.Double point ) {
        double distance = Math.abs( point.distance( center ) - ( innerDiameter / 2 ) );
        return distance;
    }

    public Point2D.Double getCenter() {
        return center;
    }

    public double getInnerDiameter() {
        return innerDiameter;
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public Point2D.Double getCM() {
        return center;
    }

    public double getMomentOfInertia() {
        return ( Math.PI / 4 ) * ( ( outerDiameter * outerDiameter * outerDiameter * outerDiameter ) -
                                   ( innerDiameter * innerDiameter * innerDiameter * innerDiameter ) );
    }
}
