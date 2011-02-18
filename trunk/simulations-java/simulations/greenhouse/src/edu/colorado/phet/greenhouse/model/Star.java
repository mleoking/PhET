// Copyright 2002-2011, University of Colorado

/**
 * Class: Star
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;

public class Star extends HorizontalPhotonEmitter {
    Disk extent;
    private double radius;
    private Point2D.Double location;

    public Star( double radius, Point2D.Double location, Rectangle2D.Double bounds ) {
        super( bounds, WavelengthConstants.SUNLIGHT_WAVELENGTH );
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
