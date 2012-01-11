// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * One particle that makes up the precipitate that forms on the bottom of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticle {

    private final Point2D location; // location of the particle in the coordinate frame of the beaker
    private final double orientation; // rotation angle, in radians
    private final Color color;
    private final double size; // particles are square, this is the length of one side

    public PrecipitateParticle( Point2D location, double orientation, Color color, double size ) {
        this.location = location;
        this.orientation = orientation;
        this.color = color;
        this.size = size;
    }

    public Point2D getLocation() {
        return new Point2D.Double( location.getX(), location.getY() );
    }

    public double getOrientation() {
        return orientation;
    }

    public Color getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }
}
