/**
 * Class: Containment
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.collision.Box2D;

import java.awt.geom.Rectangle2D;

public class Containment extends Box2D {
    private Rectangle2D bounds;

    public Containment( Rectangle2D bounds ) {
        this.bounds = bounds;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void stepInTime( double dt ) {
    }
}
