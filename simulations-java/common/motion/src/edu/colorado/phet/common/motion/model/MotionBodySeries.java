package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.rotation.model.DefaultTemporalVariable;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 5:09:15 PM
 */
public class MotionBodySeries {

    private final DefaultTemporalVariable x;
    private final DefaultTemporalVariable v;
    private final DefaultTemporalVariable a;

    /*Different strategies for updating simulation variables*/
    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();
    private UpdateStrategy updateStrategy = positionDriven; //current strategy

    public MotionBodySeries( DefaultTemporalVariable x, DefaultTemporalVariable v, DefaultTemporalVariable a ) {
        this.x = x;
        this.v = v;
        this.a = a;
    }

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

    public void stepInTime( double time, MotionBodyState motionBodyState, double dt ) {
        updateStrategy.update( this, dt, motionBodyState, time );
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
