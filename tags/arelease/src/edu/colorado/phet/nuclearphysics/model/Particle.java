/**
 * Class: Particle
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Particle {
    private Point2D.Double position = new Point2D.Double();
    private double radius;

    public Particle( Point2D.Double position ) {
        this.position = position;
        this.radius = 20;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition( Point2D.Double position ) {
        this.position = position;
    }
}
