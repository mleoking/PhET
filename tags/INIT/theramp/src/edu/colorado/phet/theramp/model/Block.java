/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:14:01 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class Block extends SimpleObservable {
    private Ramp ramp;

    private double mass = 5;//kg
    private double position = 0.0;
    private double velocity = 0.0;//m/s
    private double acceleration = 0.0;
    private double kineticFriction = 0.50;
    private double staticFriction = 0.80;

    public Block( Ramp ramp ) {
        this.ramp = ramp;
    }

    public double getPosition() {
        return position;
    }

    public Point2D getLocation() {
        return ramp.getLocation( position );
    }

    public void setPosition( double position ) {
        this.position = position;
        notifyObservers();
    }

    public double getMass() {
        return mass;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public void stepInTime( double dt ) {
        double origPosition = position;
        double origVelocity = velocity;
        double accValue = acceleration;
        if( Math.abs( accValue ) < 0.0000001 ) {
            accValue = 0.0;
        }
        velocity += accValue * dt;
        boolean changedVelSign = changedSign( origVelocity, velocity );
//        System.out.println( "acc=" + acceleration + ", origVelocity = " + origVelocity + " velocity=" + velocity + " ch=" + changedVelSign );
        if( changedVelSign ) {
            velocity = 0;
        }
        position += velocity * dt;
        if( position != origPosition ) {
            notifyObservers();
        }
    }

    public double getKineticEnergy() {
        return 0.5 * getMass() * getVelocity() * getVelocity();
    }

    public void setVelocity( double v ) {
        this.velocity = v;
    }

    static class Sign {
        static final Sign POSITIVE = new Sign( "+" );
        static final Sign NEGATIVE = new Sign( "-" );
        static final Sign ZERO = new Sign( "0" );
        private String s;

        public static Sign toSign( double value ) {
            if( value > 0 ) {
                return POSITIVE;
            }
            else if( value < 0 ) {
                return NEGATIVE;
            }
            else {
                return ZERO;
            }
        }

        public Sign( String s ) {
            this.s = s;
        }

        public boolean equals( Object obj ) {
            return obj instanceof Sign && ( (Sign)obj ).s.equals( s );
        }
    }

    private boolean changedSign( double origVelocity, double velocity ) {
        Sign origSign = Sign.toSign( origVelocity );
        Sign newSign = Sign.toSign( velocity );
        boolean leftChange = origSign.equals( Sign.POSITIVE ) && newSign.equals( Sign.NEGATIVE );
        boolean rightChange = origSign.equals( Sign.NEGATIVE ) && newSign.equals( Sign.POSITIVE );
        boolean changed = leftChange || rightChange;
        return changed;
    }

    public double getFrictionForce( double gravity, double otherForces ) {
//        if (true)
//        return 0;
        double N = this.getMass() * gravity * Math.cos( ramp.getAngle() );
        if( this.isMoving() ) {
            double sign = this.getVelocity() >= 0 ? -1 : 1;
            double kineticFrictionForce = sign * this.getKineticFriction() * N;
            return kineticFrictionForce;
        }
        else {//this was stationary
            double u = Math.max( this.getKineticFriction(), this.getStaticFriction() );
            double maxStaticFrictionForce = u * N;
            if( Math.abs( maxStaticFrictionForce ) > Math.abs( otherForces ) ) {
                //this stays at rest, friction balances applied force.
                return -otherForces;
            }
            else { //applied force overcomes friction force, this starts moving
                double sign = otherForces >= 0 ? -1 : 1;
                double frictionForce = sign * u * N;
                return frictionForce; //should be less than applied force
            }
        }
    }

    public double getStaticFriction() {
        return staticFriction;
    }

    public double getKineticFriction() {
        return kineticFriction;
    }

    private boolean isMoving() {
        return velocity != 0.0;
    }

}
