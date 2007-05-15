package edu.colorado.phet.rotation.model;

import edu.colorado.phet.rotation.RotationBody;

import javax.crypto.interfaces.DHPrivateKey;
import java.util.Arrays;

/**
 * Immutable state object for Rotation simulation.
 */

public class RotationModelState implements Cloneable {
    private RotationBody[] bodies;
    private double angle;
    private double angularVelocity;
    private double angularAcceleration;
    private double time;

//    public RotationModelState() {
//        this( new RotationBody[]{new RotationBody( 1.0 )}, 0, 0, 0, 0 );
//    }

    public RotationModelState( RotationBody[] bodies, double angle, double angularVelocity, double angularAcceleration, double time ) {
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

    public RotationBody[] copyBodies() {
        RotationBody[] bodies = new RotationBody[getRotationBodyCount()];
        for( int i = 0; i < bodies.length; i++ ) {
            bodies[i] = getRotationBody( i ).copy();
        }
        return bodies;
    }

    public RotationBody getRotationBody( int i ) {
        return bodies[i];
    }

    public int getRotationBodyCount() {
        return bodies.length;
    }

    public RotationBody[] getBodies() {
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

    public RotationBody getBody( int i ) {
        return bodies[i];
    }
}
