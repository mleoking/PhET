package edu.colorado.phet.common.motion.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class MotionBody implements Serializable {
    private transient ArrayList listeners = new ArrayList();
    private double position;
    private double velocity = 0.0;
    private double acceleration = 0.0;

    public MotionBody() {
    }

    public void setState( MotionBody motionBody ) {
        double origPosition = position;

        position = motionBody.position;
        velocity = motionBody.velocity;
        acceleration = motionBody.acceleration;
        notifyAngleChanged( position - origPosition );
    }

    public void addListener( MotionBody.Listener listener ) {
        listeners.add( listener );
    }

    public double getPosition() {
        return position;
    }

    public void setPosition( double position ) {
        double origAngle = this.position;
        if( this.position != position ) {
            this.position = position;
            notifyAngleChanged( position - origAngle );
        }
    }

    public void removeListener( MotionBody.Listener listener ) {
        listeners.remove( listener );
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity( double velocity ) {
        this.velocity = velocity;
    }

    public void setAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public static interface Listener {
        void angleChanged( double dtheta );
    }

    public void notifyAngleChanged( double dtheta ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            MotionBody.Listener listener = (MotionBody.Listener)listeners.get( i );
            listener.angleChanged( dtheta );
        }
    }

    public String toString() {
        return "motion body: x=" + position + ", v=" + velocity + ", a=" + acceleration;
    }
}
