package edu.colorado.phet.bernoulli;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.physics2d.Force;
import edu.colorado.phet.coreadditions.physics2d.Particle2d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 9:54:40 PM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class Drop extends ModelElement {
    Particle2d particle;
    double radius;
    static double gravity = -9.8;

    public Drop( double x, double y, double radius, double vx, double vy ) {
        this.radius = radius;
        particle = new Particle2d( x, y, vx, vy );
        particle.addForce( new Force() {
            public Point2D.Double getForce() {
                return new Point2D.Double( 0, gravity );//gravity
            }
        } );
    }

    public String toString() {
        return "velocity=" + particle.getVelocityVector().toString();
    }

    public PhetVector getPosition() {
        return particle.getPosition();
    }

    public double getRadius() {
        return radius;
    }

    public void stepInTime( double dt ) {
        particle.stepInTime( dt / 1000 );
        updateObservers();
    }
}
