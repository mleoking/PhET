package edu.colorado.phet.common.motion.model;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 5:09:15 PM
 */
public class MotionBodySeries {

    private DefaultTimeSeries positionTimeSeries = new DefaultTimeSeries();
    private DefaultTimeSeries velocityTimeSeries = new DefaultTimeSeries();
    private DefaultTimeSeries accelerationTimeSeries = new DefaultTimeSeries();

    /*Different strategies for updating simulation variables*/
    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();
    private UpdateStrategy updateStrategy = positionDriven; //current strategy


    public DefaultTimeSeries getXTimeSeries() {
        return positionTimeSeries;
    }

    public DefaultTimeSeries getVTimeSeries() {
        return velocityTimeSeries;
    }

    public DefaultTimeSeries getATimeSeries() {
        return accelerationTimeSeries;
    }

    public TimeData getLastPosition() {
        return positionTimeSeries.getRecentData( 0 );
    }

    public TimeData getLastVelocity() {
        return velocityTimeSeries.getRecentData( 0 );
    }

    public TimeData getLastAcceleration() {
        return accelerationTimeSeries.getRecentData( 0 );
    }

    public void addPositionData( double position, double time ) {
        positionTimeSeries.addValue( position, time );
    }

    public void addVelocityData( double velocity, double time ) {
        velocityTimeSeries.addValue( velocity, time );
    }

    public void addAccelerationData( double accel, double time ) {
        accelerationTimeSeries.addValue( accel, time );
    }

    public TimeData getVelocity( int index ) {
        return velocityTimeSeries.getData( index );
    }

    public TimeData getMaxVelocity() {
        return velocityTimeSeries.getMax();
    }

    public TimeData getMaxAcceleration() {
        return accelerationTimeSeries.getMax();
    }

    public TimeData getMinAcceleration() {
        return accelerationTimeSeries.getMin();
    }


    /**
     * Returns values from the last numPts points of the acceleration time series.
     *
     * @param numPts the number of (most recent) points to get
     * @return the time series points.
     */
    public TimeData[] getRecentAccelerationTimeSeries( int numPts ) {
        return accelerationTimeSeries.getRecentSeries( numPts );
    }

    public TimeData[] getRecentVelocityTimeSeries( int numPts ) {
        return velocityTimeSeries.getRecentSeries( numPts );
    }

    public TimeData[] getRecentPositionTimeSeries( int numPts ) {
        return positionTimeSeries.getRecentSeries( numPts );
    }

    public int getAccelerationSampleCount() {
        return accelerationTimeSeries.getSampleCount();
    }

    public int getVelocitySampleCount() {
        return velocityTimeSeries.getSampleCount();
    }

    public int getPositionSampleCount() {
        return positionTimeSeries.getSampleCount();
    }

    public void clear() {

        positionTimeSeries.clear();
        velocityTimeSeries.clear();
        accelerationTimeSeries.clear();
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
}
