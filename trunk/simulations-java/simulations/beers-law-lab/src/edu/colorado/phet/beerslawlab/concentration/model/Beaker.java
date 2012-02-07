// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of an immutable beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beaker {

    private final ImmutableVector2D location; // bottom center
    private final PDimension size;
    private final double volume; // L

    public Beaker( ImmutableVector2D location, PDimension size, double volume ) {
        this.location = location;
        this.size = new PDimension( size );
        this.volume = volume;
    }

    public ImmutableVector2D getLocation() {
        return location;
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    // Gets the x coordinate of the left wall.
    public double getMinX() {
        return getX() - ( getWidth() / 2 );
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
