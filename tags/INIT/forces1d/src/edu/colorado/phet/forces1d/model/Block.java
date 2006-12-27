/** Sam Reid*/
package edu.colorado.phet.forces1d.model;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 9:58:08 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Block {
    private double kineticFriction;
    private double staticFriction;
    private double mass;
    private double position;
    private double velocity;
    private double acceleration;

    public Block() {
    }

    public double getKineticFriction() {
        return kineticFriction;
    }

    public void setKineticFriction( double kineticFriction ) {
        this.kineticFriction = kineticFriction;
    }

    public double getStaticFriction() {
        return staticFriction;
    }

    public void setStaticFriction( double staticFriction ) {
        this.staticFriction = staticFriction;
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition( double position ) {
        this.position = position;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity( double velocity ) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public void stepInTime( double dt ) {
    }
}
