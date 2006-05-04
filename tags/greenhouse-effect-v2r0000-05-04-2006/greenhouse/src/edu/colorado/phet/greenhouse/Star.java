/**
 * Class: Star
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.coreadditions.Disk;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Star extends HorizontalPhotonEmitter {
    Disk extent;
    private double radius;
    private Point2D.Double location;

    public Star( double radius, Point2D.Double location, Rectangle2D.Double bounds ) {
        super( bounds, GreenhouseConfig.sunlightWavelength );
        this.radius = radius;
        this.location = location;
    }

    public double getRadius() {
        return radius;
    }

    public Point2D.Double getLocation() {
        return location;
    }
}
