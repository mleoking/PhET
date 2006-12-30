package edu.colorado.phet.rotation.model;

import java.util.Arrays;

/**
 * Immutable state object for Rotation simulation.
 */

public class RotationModelState {
    private Body[] bodies = new Body[0];
    private double angle = 0.0;
    private double angularVelocity = 0.0;
    private double angularAcceleration = 0.0;
    private double time = 0;

    public RotationModelState() {
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
}
