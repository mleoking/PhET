package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:27:53 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class RotationModelState {
    private Body[] bodies = new Body[0];
    private double angle = 0.0;
    private double angularVelocity = 0.0;
    private double angularAcceleration = 0.0;

    public RotationModelState() {
    }

    public RotationModelState copy() {
        return (RotationModelState)clone();
    }

    public Object clone() {
        try {
            RotationModelState clone = (RotationModelState)super.clone();
            clone.angle = angle;
            clone.angularVelocity = angularVelocity;
            clone.angularAcceleration = angularAcceleration;
            Body[] bodies = new Body[getBodyCount()];
            for( int i = 0; i < bodies.length; i++ ) {
                bodies[i] = getBody( i ).copy();
            }
            clone.bodies = bodies;
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    public Body getBody( int i ) {
        return bodies[i];
    }

    public int getBodyCount() {
        return bodies.length;
    }
}
