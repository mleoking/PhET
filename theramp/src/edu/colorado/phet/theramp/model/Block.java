/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:14:01 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class Block {
    private Ramp ramp;

    private double mass = 5;//kg
    private double position = 10.0;
    private double velocity = 0.0;//m/s
    private double acceleration = 0.0;
    private double kineticFriction = 0.50;
    private double staticFriction = 0.80;
    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public Block copyState() {
        Block dataBlock = new Block( ramp );
        dataBlock.mass = mass;
        dataBlock.position = position;
        dataBlock.velocity = velocity;
        dataBlock.acceleration = acceleration;
        dataBlock.kineticFriction = kineticFriction;
        dataBlock.staticFriction = staticFriction;
        return dataBlock;
    }

    public void setState( Block state ) {
        setMass( state.mass );
        setPosition( state.position );
        setVelocity( state.velocity );
        setAcceleration( state.acceleration );
        setKineticFriction( state.kineticFriction );
        setStaticFriction( state.staticFriction );
    }

    public static interface Listener {
        void positionChanged();

        void staticFrictionChanged();

        void kineticFrictionChanged();
    }

    public static class Adapter implements Listener {

        public void positionChanged() {
        }

        public void staticFrictionChanged() {
        }

        public void kineticFrictionChanged() {
        }
    }

    public Block( Ramp ramp ) {
        this.ramp = ramp;
    }

    public double getPosition() {
        return position;
    }

    public Point2D getLocation2D() {
        return ramp.getLocation( position );
    }

    public void setPosition( double position ) {
        this.position = position;
        notifyPositionChanged();
    }

    private void notifyPositionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.positionChanged();
        }
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
        //boundary conditions.
        applyBoundaryConditions();

        if( position != origPosition ) {
            notifyPositionChanged();
        }
    }

    private void applyBoundaryConditions() {

        if( position < 0 ) {
            position = 0;
            velocity = 0;
            //fire a collision.
        }
        else if( position > ramp.getLength() ) {
            position = ramp.getLength();
            velocity = 0;
        }
    }

    public double getKineticEnergy() {
        return 0.5 * getMass() * getVelocity() * getVelocity();
    }

    public void setVelocity( double v ) {
        this.velocity = v;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public void setStaticFriction( double staticFriction ) {
        if( this.staticFriction != staticFriction ) {
            this.staticFriction = staticFriction;
            notifyStaticFrictionChanged();
        }
    }

    private void notifyStaticFrictionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.staticFrictionChanged();
        }
    }

    public void setKineticFriction( double kineticFriction ) {
        if( this.kineticFriction != kineticFriction ) {
            this.kineticFriction = kineticFriction;
            notifyKineticFrictionChanged();
        }
    }

    private void notifyKineticFrictionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.kineticFrictionChanged();

        }
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
