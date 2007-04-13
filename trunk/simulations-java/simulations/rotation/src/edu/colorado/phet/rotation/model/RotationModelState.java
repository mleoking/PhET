package edu.colorado.phet.rotation.model;

import java.util.Arrays;

/**
 * Immutable state object for Rotation simulation.
 */

public class RotationModelState implements Cloneable {
    private Body[] bodies;
    private double angle;
    private double angularVelocity;
    private double angularAcceleration;
    private double time;

    public RotationModelState() {
        this( new Body[]{new Body( 1.0 )}, 0, 0, 0, 0 );
    }

    public RotationModelState( Body[] bodies, double angle, double angularVelocity, double angularAcceleration, double time ) {
        this.bodies = bodies;
        this.angle = angle;
        this.angularVelocity = angularVelocity;
        this.angularAcceleration = angularAcceleration;
        this.time = time;
    }

    public RotationModelState copy() {
        return (RotationModelState)clone();
    }

    public String toString() {
        return "time=" + time + ", angle=" + angle + ", angularVelocity=" + angularVelocity + ", angularAcceleration=" + angularAcceleration + ", bodies=" + Arrays.asList( bodies );
    }

    public Object clone() {
        try {
            RotationModelState clone = (RotationModelState)super.clone();
            clone.angle = angle;
            clone.angularVelocity = angularVelocity;
            clone.angularAcceleration = angularAcceleration;
            clone.bodies = copyBodies();
            clone.time = time;
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    public Body[] copyBodies() {
        Body[] bodies = new Body[getBodyCount()];
        for( int i = 0; i < bodies.length; i++ ) {
            bodies[i] = getBody( i ).copy();
        }
        return bodies;
    }

    public Body getBody( int i ) {
        return bodies[i];
    }

    public int getBodyCount() {
        return bodies.length;
    }

    public Body[] getBodies() {
        return bodies;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public double getAngularAcceleration() {
        return angularAcceleration;
    }

    public double getTime() {
        return time;
    }

    public void setTime( double time ) {
        this.time = time;
    }
}
