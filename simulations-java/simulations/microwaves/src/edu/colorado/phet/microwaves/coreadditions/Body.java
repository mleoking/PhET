/**
 * Class: Body
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 22, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import edu.colorado.phet.common_microwaves.model.ModelElement;

import java.awt.geom.Point2D;

public abstract class Body extends ModelElement {
    private double omega;
    private double alpha;
    private double mass;
    private double charge;
    private Point2D.Double location = new Point2D.Double();
    private Vector2D velocity = new Vector2D();
    private Vector2D acceleration = new Vector2D();
    private Body lastColidedBody = null;

    protected Body() {
    }

    protected Body( Point2D.Double position, Vector2D velocity,
                    Vector2D acceleration, float mass, float charge ) {
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

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity( Vector2D velocity ) {
        this.velocity = velocity;
    }

    public void setVelocity( float vx, float vy ) {
        this.velocity.setX( vx );
        this.velocity.setY( vy );
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( Vector2D acceleration ) {
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
        double vxPrime = velocity.getX() + dt * acceleration.getX();
        double vyPrime = velocity.getY() + dt * acceleration.getY();

        // New acceleration

        // New velocity
    }

    public abstract Point2D.Double getCM();

    public abstract double getMomentOfInertia();

    public double getKineticEnergy() {
        return ( getMass() * getVelocity().getMagnitudeSq() / 2 ) +
               getMomentOfInertia() * omega * omega / 2;
    }

    public Body getLastColidedBody() {
        return lastColidedBody;
    }

    public void setLastColidedBody( Body lastColidedBody ) {
        this.lastColidedBody = lastColidedBody;
    }
}

