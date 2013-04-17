// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of an immutable beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beaker {

    public final Vector2D location; // bottom center
    public final PDimension size;
    public final double volume; // L

    public Beaker( Vector2D location, PDimension size, double volume ) {
        this.location = location;
        this.size = new PDimension( size );
        this.volume = volume;
    }

    // Gets the x coordinate of the left wall.
    public double getMinX() {
        return location.getX() - ( size.getWidth() / 2 );
    }
}
