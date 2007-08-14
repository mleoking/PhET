package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.rotation.model.DefaultTemporalVariable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 6:19:17 PM
 */
public class MotionBody implements IUpdateStrategy {

    private DefaultTemporalVariable x = new DefaultTemporalVariable();
    private DefaultTemporalVariable v = new DefaultTemporalVariable();
    private DefaultTemporalVariable a = new DefaultTemporalVariable();
    private MotionBodySeries motionBodySeries = new MotionBodySeries();
    private MotionBodyState motionBodyState = new MotionBodyState();//current state

    public MotionBody() {
    }

    public void setTime( double time ) {
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValueForTime( time ) );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValueForTime( time ) );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValueForTime( time ) );
    }

    public void stepInTime( double time, double dt ) {
        motionBodySeries.stepInTime( this, time, dt );
        updateStateFromSeries();
    }

    public void updateStateFromSeries() {
        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValue() );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValue() );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValue() );
    }

    public void clear() {
        motionBodySeries.clear();
    }

    public double getAcceleration() {
        return a.getValue();
    }

    public double getVelocity() {
        return v.getValue();
    }

    public double getPosition() {
        return x.getValue();
    }

    public void setPosition( double position ) {
        motionBodyState.setPosition( position );
    }

    public PositionDriven getPositionDriven() {
        return motionBodySeries.getPositionDriven();
    }

    public void setPositionDriven() {
        motionBodySeries.setPositionDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        motionBodySeries.setUpdateStrategy( updateStrategy );
    }

    public UpdateStrategy getUpdateStrategy() {
        return motionBodySeries.getUpdateStrategy();
    }

    public ITemporalVariable getPositionVariable() {
        return x;
    }

    public ITemporalVariable getVelocityVariable() {
        return v;
    }

    public ITemporalVariable getAccelerationVariable() {
        return a;
    }

    public VelocityDriven getVelocityDriven() {
        return motionBodySeries.getVelocityDriven();
    }

    public AccelerationDriven getAccelDriven() {
        return motionBodySeries.getAccelDriven();
    }

    public boolean isPositionDriven() {
        return motionBodySeries.isPositionDriven();
    }

    public boolean isVelocityDriven() {
        return motionBodySeries.isVelocityDriven();
    }

    public boolean isAccelerationDriven() {
        return motionBodySeries.isAccelerationDriven();
    }

    public void reset() {
        clear();
        getPositionVariable().setValue( 0.0 );
        getVelocityVariable().setValue( 0.0 );
        getAccelerationVariable().setValue( 0.0 );
    }

    public int getPositionSampleCount() {
        return motionBodySeries.getPositionSampleCount();
    }

    public TimeData[] getRecentPositionTimeSeries( int numPts ) {
        return motionBodySeries.getRecentPositionTimeSeries( numPts );
    }

    public int getVelocitySampleCount() {
        return motionBodySeries.getVelocitySampleCount();
    }

    public TimeData[] getRecentVelocityTimeSeries( int num ) {
        return motionBodySeries.getRecentVelocityTimeSeries( num );
    }

    public void addPositionData( double position, double time ) {
        motionBodySeries.addPositionData( position, time );
    }

    public void addVelocityData( double value, double time ) {
        motionBodySeries.addVelocityData( value, time );
    }

    public void addAccelerationData( double value, double time ) {
        motionBodySeries.addAccelerationData( value, time );
    }

    public int getAccelerationSampleCount() {
        return motionBodySeries.getAccelerationSampleCount();
    }

    public TimeData getLastAcceleration() {
        return motionBodySeries.getLastAcceleration();
    }

    public TimeData getLastVelocity() {
        return motionBodySeries.getLastVelocity();
    }

    public TimeData[] getRecentAccelerationTimeSeries( int i ) {
        return motionBodySeries.getRecentAccelerationTimeSeries( i );
    }

    public TimeData getLastPosition() {
        return motionBodySeries.getLastPosition();
    }

    public void setAccelerationDriven() {
        motionBodySeries.setAccelerationDriven();
    }

    public void setVelocityDriven() {
        motionBodySeries.setVelocityDriven();
    }

    public TimeData getMaxVelocity() {
        return motionBodySeries.getMaxVelocity();
    }

    public TimeData getMaxAcceleration() {
        return motionBodySeries.getMaxAcceleration();
    }

    public TimeData getMinAcceleration() {
        return motionBodySeries.getMinAcceleration();
    }

    public TimeData getVelocity( int index ) {
        return motionBodySeries.getVelocity( index );
    }

    public void setAcceleration( double v ) {
        motionBodyState.setAcceleration( v );
    }

    public void setVelocity( double v ) {
        motionBodyState.setVelocity( v );
    }

    public void addListener( MBListener listener ) {
        motionBodyState.addListener( listener );
    }

    public void removeListener( MBListener listener ) {
        motionBodyState.removeListener( listener );
    }

    private class MotionBodySeries {

        /*Different strategies for updating simulation variables*/
        private PositionDriven positionDriven = new PositionDriven();
        private VelocityDriven velocityDriven = new VelocityDriven();
        private AccelerationDriven accelDriven = new AccelerationDriven();
        private UpdateStrategy updateStrategy = positionDriven; //current strategy

        public ITemporalVariable getXTimeSeries() {
            return x;
        }

        public ITemporalVariable getVTimeSeries() {
            return v;
        }

        public ITemporalVariable getATimeSeries() {
            return a;
        }

        public TimeData getLastPosition() {
            return x.getRecentData( 0 );
        }

        public TimeData getLastVelocity() {
            return v.getRecentData( 0 );
        }

        public TimeData getLastAcceleration() {
            return a.getRecentData( 0 );
        }

        public void addPositionData( double position, double time ) {
            x.addValue( position, time );
        }

        public void addVelocityData( double velocity, double time ) {
            v.addValue( velocity, time );
        }

        public void addAccelerationData( double accel, double time ) {
            a.addValue( accel, time );
        }

        public TimeData getVelocity( int index ) {
            return v.getData( index );
        }

        public TimeData getMaxVelocity() {
            return v.getMax();
        }

        public TimeData getMaxAcceleration() {
            return a.getMax();
        }

        public TimeData getMinAcceleration() {
            return a.getMin();
        }

        /**
         * Returns values from the last numPts points of the acceleration time series.
         *
         * @param numPts the number of (most recent) points to get
         * @return the time series points.
         */
        public TimeData[] getRecentAccelerationTimeSeries( int numPts ) {
            return a.getRecentSeries( numPts );
        }

        public TimeData[] getRecentVelocityTimeSeries( int numPts ) {
            return v.getRecentSeries( numPts );
        }

        public TimeData[] getRecentPositionTimeSeries( int numPts ) {
            return x.getRecentSeries( numPts );
        }

        public int getAccelerationSampleCount() {
            return a.getSampleCount();
        }

        public int getVelocitySampleCount() {
            return v.getSampleCount();
        }

        public int getPositionSampleCount() {
            return x.getSampleCount();
        }

        public void clear() {
            x.clear();
            v.clear();
            a.clear();
        }

        /**
         * These getters are provided for convenience in setting up listeners; i.e. to set up a different
         * mode, the client has to pass a different object, not call a different method.
         *
         * @return the strategy
         */
        public PositionDriven getPositionDriven() {
            return positionDriven;
        }

        public VelocityDriven getVelocityDriven() {
            return velocityDriven;
        }

        public AccelerationDriven getAccelDriven() {
            return accelDriven;
        }

        public void setVelocityDriven() {
            setUpdateStrategy( velocityDriven );
        }

        public void setAccelerationDriven() {
            setUpdateStrategy( accelDriven );
        }

        public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
            this.updateStrategy = updateStrategy;
        }

        public void setPositionDriven() {
            setUpdateStrategy( positionDriven );
        }

        public void stepInTime( MotionBody motionBody, double time, double dt ) {
            updateStrategy.update( motionBody, dt, time );
        }

        public UpdateStrategy getUpdateStrategy() {
            return updateStrategy;
        }

        public boolean isPositionDriven() {
            return updateStrategy == getPositionDriven();
        }

        public boolean isVelocityDriven() {
            return updateStrategy == getVelocityDriven();
        }

        public boolean isAccelerationDriven() {
            return updateStrategy == getAccelDriven();
        }
    }

    private class MotionBodyState implements Serializable {
        private transient ArrayList listeners = new ArrayList();
        private DefaultTemporalVariable position;
        private DefaultTemporalVariable velocity;
        private DefaultTemporalVariable acceleration;

        public void setState( MotionBodyState motionBodyState ) {
            double origPosition = position.getValue();

            position = motionBodyState.position;
            velocity = motionBodyState.velocity;
            acceleration = motionBodyState.acceleration;
            notifyPositionChanged( position.getValue() - origPosition );
        }

        public void addListener( MBListener listener ) {
            listeners.add( listener );
        }

        public double getPosition() {
            return position.getValue();
        }

        public void setPosition( double position ) {
            double origX = this.position.getValue();
            if( this.position.getValue() != position ) {
                this.position.setValue( position );
                notifyPositionChanged( position - origX );
            }
        }

        public void removeListener( MBListener listener ) {
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
                this.velocity.setValue( velocity );
                notifyVelocityChanged();
            }
        }

        private void notifyVelocityChanged() {
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (MBListener)listeners.get( i ) ).velocityChanged();
            }
        }

        public void setAcceleration( double acceleration ) {
            if( this.acceleration.getValue() != acceleration ) {
                this.acceleration.setValue( acceleration );
                notifyAccelerationChanged();
            }
        }

        private void notifyAccelerationChanged() {
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (MBListener)listeners.get( i ) ).accelerationChanged();
            }
        }


        public void notifyPositionChanged( double dtheta ) {
            for( int i = 0; i < listeners.size(); i++ ) {
                MBListener listener = (MBListener)listeners.get( i );
                listener.positionChanged( dtheta );
            }
        }

        public String toString() {
            return "motion body: x=" + position + ", v=" + velocity + ", a=" + acceleration;
        }
    }


    public static interface MBListener {
        void positionChanged( double dx );

        void velocityChanged();

        void accelerationChanged();
    }

    public static class MBAdapter implements MBListener {

        public void positionChanged( double dx ) {
        }

        public void velocityChanged() {
        }

        public void accelerationChanged() {
        }
    }

}
