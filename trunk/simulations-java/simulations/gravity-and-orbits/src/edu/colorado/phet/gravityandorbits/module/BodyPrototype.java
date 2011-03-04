// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

/**
 * @author Sam Reid
 */
public class BodyPrototype {
    public final double radius;
    public final double mass;
    public final double x;
    public final double y;
    public final double vx;
    public final double vy;

    public BodyPrototype( double radius, double mass, double x, double y, double vx, double vy ) {
        this.radius = radius;
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }
}
