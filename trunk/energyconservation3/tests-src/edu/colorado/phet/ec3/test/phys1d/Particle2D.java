package edu.colorado.phet.ec3.test.phys1d;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:42:28 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class Particle2D {
    double x;
    double y;

    double vx;
    double vy;
    double g = 9.8 * 10000;

    public void stepInTime( double dt ) {
        y += vy * dt + 0.5 * g * dt * dt;
        x += vx * dt;
        vy += g * dt;
        vx += 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
    }
}
