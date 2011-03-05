// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

/**
 * @author Sam Reid
 */
public class BodyPrototype {
    public double mass;
    public double radius;
    public double x;
    public double y;
    public double vx;
    public double vy;

    public BodyPrototype( double mass, double radius, double x, double y, double vx, double vy ) {
        this.mass = mass;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }
}
