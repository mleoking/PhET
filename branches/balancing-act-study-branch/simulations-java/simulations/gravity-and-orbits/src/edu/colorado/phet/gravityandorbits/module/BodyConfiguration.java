// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * Class that facilitates configuration of body instances for a GravityAndOrbitsMode; a data structure that describes a body's parameters.
 *
 * @author Sam Reid
 */
public class BodyConfiguration {
    public double mass;
    public double radius;
    public double x;
    public double y;
    public double vx;
    public double vy;
    public boolean fixed = false;//True if the object doesn't move when the clock ticks

    public BodyConfiguration( double mass, double radius, double x, double y, double vx, double vy ) {
        this.mass = mass;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public Vector2D getMomentum() {
        return new Vector2D( vx * mass, vy * mass );
    }
}