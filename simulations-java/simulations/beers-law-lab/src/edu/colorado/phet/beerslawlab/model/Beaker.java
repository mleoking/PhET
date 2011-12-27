// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of an immutable beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beaker {

    private final Point2D location; // bottom center
    private final PDimension size;
    private final double volume; // L

    public Beaker( Point2D location, PDimension size, double volume ) {
        this.location = location;
        this.size = new PDimension( size );
        this.volume = volume;
    }

    public Point2D getLocation() {
        return new Point2D.Double( location.getX(), location.getY() );
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public PDimension getSize() {
        return new PDimension( size );
    }

    public double getWidth() {
        return size.getWidth();
    }

    public double getHeight() {
        return size.getHeight();
    }

    public double getVolume() {
        return volume;
    }
}
