// Copyright 2002-2012, University of Colorado

/**
 * Class: Body
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 22, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;

public abstract class Body extends Observable implements ModelElement {
    private double omega;
    private double alpha;
    private double mass;
    private double charge;
    private Point2D.Double location = new Point2D.Double();
    private MutableVector2D velocity = new MutableVector2D();
    private MutableVector2D acceleration = new MutableVector2D();
    private Body lastColidedBody = null;

    protected Body() {
    }

    protected Body( Point2D.Double position, MutableVector2D velocity,
                    MutableVector2D acceleration, float mass, float charge ) {
//        super( position, velocity, acceleration, mass, charge );
//        setLocation( position );
        setVelocity( velocity );
        setAcceleration( acceleration );
        setMass( mass );
        setCharge( charge );
    }

    public Point2D.Double getLocation() {
        return location;
    }

//    public void setLocation( Point2D.Double location ) {
//        this.location = location;
//    }

    public void setLocation( double x, double y ) {
        location.setLocation( x, y );
    }

    public void setLocation( Point2D location ) {
        setLocation( location.getX(), location.getY() );
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega( double omega ) {
        this.omega = omega;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha( double alpha ) {
        this.alpha = alpha;
    }

    public MutableVector2D getVelocity() {
        return velocity;
    }

    public void setVelocity( MutableVector2D velocity ) {
        this.velocity = velocity;
    }

    public void setVelocity( double vx, double vy ) {
        this.velocity.setX( vx );
        this.velocity.setY( vy );
    }

    public MutableVector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( MutableVector2D acceleration ) {
        this.acceleration = acceleration;
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge( double charge ) {
        this.charge = charge;
    }

    /**
     * Determines the new state of the body using the Verlet method
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        // New location
        double xNew = location.getX()
                      + dt * velocity.getX()
                      + dt * dt * acceleration.getX() / 2;
        double yNew = location.getY()
                      + dt * velocity.getY()
                      + dt * dt * acceleration.getY() / 2;
        setLocation( xNew, yNew );

        // velocity prime
//        double vxPrime = velocity.getX() + dt * acceleration.getX();
//        double vyPrime = velocity.getY() + dt * acceleration.getY();

        // New acceleration

        // New velocity

        setChanged();
        notifyObservers();
    }

    public abstract Point2D.Double getCM();

    public abstract double getMomentOfInertia();

    public double getKineticEnergy() {
        return ( getMass() * getVelocity().magnitudeSquared() / 2 ) +
               getMomentOfInertia() * omega * omega / 2;
    }

    public Body getLastColidedBody() {
        return lastColidedBody;
    }

    public void setLastColidedBody( Body lastColidedBody ) {
        this.lastColidedBody = lastColidedBody;
    }
}

