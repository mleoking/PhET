package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 14, 2007, 10:44:25 PM
 */
public class RotationModelState implements Serializable {
    private ArrayList rotationBodies = new ArrayList();
    private RotationPlatform rotationPlatform = new RotationPlatform();
    private double time = 0.0;

    public RotationBody getRotationBody( int i ) {
        return (RotationBody)rotationBodies.get( i );
    }

    public RotationModelState copy() {
        try {
            return (RotationModelState)PersistenceUtil.copy( this );
        }
        catch( PersistenceUtil.CopyFailedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public double getAngularAcceleration() {
        return rotationPlatform.getAngularAcceleration();
    }

    public RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

    public double getTime() {
        return time;
    }

    public double getAngle() {
        return rotationPlatform.getAngle();
    }

    public double getAngularVelocity() {
        return rotationPlatform.getAngularVelocity();
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public void setAngle( double angle ) {
        rotationPlatform.setAngle(angle );
    }

    public void setAngularVelocity( double angularVelocity ) {
        rotationPlatform.setAngularVelocity(angularVelocity);
    }

    public void setAngularAcceleration( double angularAcceleration ) {
        rotationPlatform.setAngularAcceleration(angularAcceleration);
    }

    public void stepInTime( double dt ) {
        time += dt;
    }

    public int getNumRotationBodies() {
        return rotationBodies.size();
    }

    public void addRotationBody( RotationBody rotationBody ) {
        rotationBodies.add( rotationBody );
    }
}
