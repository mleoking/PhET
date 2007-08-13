package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.rotation.model.DefaultTemporalVariable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class MotionBodyState implements Serializable {
    private transient ArrayList listeners = new ArrayList();
    private DefaultTemporalVariable position;
    private DefaultTemporalVariable velocity;
    private DefaultTemporalVariable acceleration;

    public MotionBodyState() {
    }

    public MotionBodyState( DefaultTemporalVariable x, DefaultTemporalVariable v, DefaultTemporalVariable a ) {
        this.position = x;
        this.velocity = v;
        this.acceleration = a;
    }

    public void setState( MotionBodyState motionBodyState ) {
        double origPosition = position.getValue();

        position = motionBodyState.position;
        velocity = motionBodyState.velocity;
        acceleration = motionBodyState.acceleration;
        notifyPositionChanged( position.getValue() - origPosition );
    }

    public void addListener( MotionBodyState.Listener listener ) {
        listeners.add( listener );
    }

    public double getPosition() {
        return position.getValue();
    }

    public void setPosition( double position ) {
        double origX = this.position.getValue();
        if( this.position.getValue() != position ) {
            this.position.setValue(position);
            notifyPositionChanged( position - origX );
        }
    }

    public void removeListener( MotionBodyState.Listener listener ) {
        listeners.remove( listener );
    }

    public double getAcceleration() {
        return acceleration.getValue();
    }

    public double getVelocity() {
        return velocity.getValue();
    }

    public void setVelocity( double velocity ) {
        if( this.velocity.getValue() != velocity ) {
            this.velocity.setValue(velocity);
            notifyVelocityChanged();
        }
    }

    private void notifyVelocityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).velocityChanged();
        }
    }

    public void setAcceleration( double acceleration ) {
        if( this.acceleration.getValue() != acceleration ) {
            this.acceleration.setValue(acceleration);
            notifyAccelerationChanged();
        }
    }

    private void notifyAccelerationChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).accelerationChanged();
        }
    }

    public static interface Listener {
        void positionChanged( double dx );

        void velocityChanged();

        void accelerationChanged();
    }

    public static class Adapter implements Listener {

        public void positionChanged( double dx ) {
        }

        public void velocityChanged() {
        }

        public void accelerationChanged() {
        }
    }

    public void notifyPositionChanged( double dtheta ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            MotionBodyState.Listener listener = (MotionBodyState.Listener)listeners.get( i );
            listener.positionChanged( dtheta );
        }
    }

    public String toString() {
        return "motion body: x=" + position + ", v=" + velocity + ", a=" + acceleration;
    }
}
