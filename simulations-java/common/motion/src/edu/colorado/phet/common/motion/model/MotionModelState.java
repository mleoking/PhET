package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;

import java.io.Serializable;

/**
 * Author: Sam Reid
 * May 14, 2007, 10:44:25 PM
 */
public class MotionModelState implements Serializable {

    private MotionBodyState motionBodyState = new MotionBodyState();
    private double time = 0.0;

    public MotionModelState() {
    }

    protected void setMotionBody( MotionBodyState motionBodyState ) {
        this.motionBodyState = motionBodyState;
    }

    public void setState( MotionModelState state ) {
        time = state.time;
        motionBodyState.setState( state.motionBodyState );
    }

    public MotionModelState copy() {
        try {
            return (MotionModelState)PersistenceUtil.copy( this );
        }
        catch( PersistenceUtil.CopyFailedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public double getAcceleration() {
        return motionBodyState.getAcceleration();
    }

    public MotionBodyState getMotionBody() {
        return motionBodyState;
    }

    public double getTime() {
        return time;
    }

    public double getPosition() {
        return motionBodyState.getPosition();
    }

    public double getVelocity() {
        return motionBodyState.getVelocity();
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public void setPosition( double angle ) {
        motionBodyState.setPosition( angle );
    }

    public void setVelocity( double angularVelocity ) {
        motionBodyState.setVelocity( angularVelocity );
    }

    public void setAcceleration( double angularAcceleration ) {
        motionBodyState.setAcceleration( angularAcceleration );
    }

    public void stepInTime( double dt ) {
        time += dt;
    }

    public String toString() {
        return super.toString() + " motionBody=" + motionBodyState.toString();
    }
}
