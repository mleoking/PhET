/**
 * Class: NuclearParticle
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class NuclearParticle {
    private Point2D.Double position = new Point2D.Double();
    private double radius;

    public NuclearParticle( Point2D.Double position ) {
        this.position = position;
        this.radius = RADIUS;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition( Point2D.Double position ) {
        this.position = position;
    }

    public double getRadius() {
        return this.radius;
    }

    //
    // Statics
    //
    public final static double RADIUS = 5;
}
