package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.rotation.model.DefaultTemporalVariable;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 6:19:17 PM
 */
public class MotionBody implements IUpdateStrategy {

    private DefaultTemporalVariable x = new DefaultTemporalVariable();
    private DefaultTemporalVariable v = new DefaultTemporalVariable();
    private DefaultTemporalVariable a = new DefaultTemporalVariable();

    /*Different strategies for updating simulation variables*/
    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();
    private UpdateStrategy updateStrategy = positionDriven; //current strategy

    private transient ArrayList listeners = new ArrayList();

    public void setTime( double time ) {
        setPosition( x.getValueForTime( time ) );
        setVelocity( v.getValueForTime( time ) );
        setAcceleration( a.getValueForTime( time ) );
    }

    public void stepInTime( double time, double dt ) {
        updateStrategy.update( this, dt, time );
    }

    public void setAcceleration( double acceleration ) {
        if( a.getValue() != acceleration ) {
            a.setValue( acceleration );
            notifyAccelerationChanged();
        }
    }

    public void setVelocity( double velocity ) {
        if( v.getValue() != velocity ) {
            v.setValue( velocity );
            notifyVelocityChanged();
        }
    }

    public void addListener( MBListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( MBListener listener ) {
        listeners.remove( listener );
    }

    public void clear() {
        x.clear();
        v.clear();
        a.clear();
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
        double origX = x.getValue();
        if( x.getValue() != position ) {
            x.setValue( position );
            notifyPositionChanged( position - origX );
        }
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

    public void reset() {
        clear();
        getPositionVariable().setValue( 0.0 );
        getVelocityVariable().setValue( 0.0 );
        getAccelerationVariable().setValue( 0.0 );
    }


    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
    }

    private class MotionBodySeries {

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

    private void notifyVelocityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (MBListener)listeners.get( i ) ).velocityChanged();
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

    public void setVelocityDriven() {
        setUpdateStrategy( velocityDriven );
    }

    public void setAccelerationDriven() {
        setUpdateStrategy( accelDriven );
    }

    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
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

    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
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

    public TimeData getLastPosition() {
        return x.getRecentData( 0 );
    }

    public TimeData getLastVelocity() {
        return v.getRecentData( 0 );
    }

    public TimeData getLastAcceleration() {
        return a.getRecentData( 0 );
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
}
