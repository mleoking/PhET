package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;

import java.io.Serializable;

/**
 * Author: Sam Reid
 * May 14, 2007, 10:44:25 PM
 */
public class ModelState implements Serializable {

    private MotionBody motionBody = new MotionBody();
    private double time = 0.0;

    public ModelState() {
    }

    protected void setMotionBody( MotionBody motionBody ) {
        this.motionBody = motionBody;
    }

    public void setState( ModelState state ) {
        time = state.time;
        motionBody.setState( state.motionBody );
    }

    public ModelState copy() {
        try {
            return (ModelState)PersistenceUtil.copy( this );
        }
        catch( PersistenceUtil.CopyFailedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public double getAcceleration() {
        return motionBody.getAcceleration();
    }

    public MotionBody getMotionBody() {
        return motionBody;
    }

    public double getTime() {
        return time;
    }

    public double getPosition() {
        return motionBody.getPosition();
    }

    public double getVelocity() {
        return motionBody.getVelocity();
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public void setPosition( double angle ) {
        motionBody.setPosition( angle );
    }

    public void setVelocity( double angularVelocity ) {
        motionBody.setVelocity( angularVelocity );
    }

    public void setAcceleration( double angularAcceleration ) {
        motionBody.setAcceleration( angularAcceleration );
    }

    public void stepInTime( double dt ) {
        time += dt;
    }

}
