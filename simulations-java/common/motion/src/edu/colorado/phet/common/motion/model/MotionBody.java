package edu.colorado.phet.common.motion.model;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 6:19:17 PM
 */
public class MotionBody implements UpdateableObject, IMotionBody {

    private ITemporalVariable x;
    private ITemporalVariable v;
    private ITemporalVariable a;

    /*Different strategies for updating simulation variables*/
    private UpdateStrategy.PositionDriven positionDriven;
    private UpdateStrategy.VelocityDriven velocityDriven;
    private UpdateStrategy.AccelerationDriven accelDriven;
    private UpdateStrategy updateStrategy;

    public MotionBody(int smoothXV,int smoothXA,int smoothVA) {
        this( smoothXV, smoothXA, smoothVA,new DefaultTemporalVariable(), new DefaultTemporalVariable(), new DefaultTemporalVariable() );
    }

    public MotionBody( int smoothXV, int smoothXA,int smoothVA,TimeSeriesFactory factory ) {
        this( smoothXV, smoothXA, smoothVA,new DefaultTemporalVariable( factory ), new DefaultTemporalVariable( factory ), new DefaultTemporalVariable( factory ) );
    }

    public MotionBody( int smoothXV, int smoothXA,int smoothVA, DefaultTemporalVariable x, DefaultTemporalVariable v, DefaultTemporalVariable a ) {
        this.x = x;
        this.v = v;
        this.a = a;
        positionDriven = new UpdateStrategy.PositionDriven(smoothXV,smoothXA,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        velocityDriven = new UpdateStrategy.VelocityDriven(smoothVA,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        accelDriven = new UpdateStrategy.AccelerationDriven();
        updateStrategy = positionDriven;
    }

    public void setTime( double time ) {
        x.setPlaybackTime( time );
        v.setPlaybackTime( time );
        a.setPlaybackTime( time );
    }

    public void stepInTime( double time, double dt ) {
        updateStrategy.update( this, dt, time );
    }

    public void setVelocity( double velocity ) {
        v.setValue( velocity );
    }

    public void setPosition( double position ) {
        x.setValue( position );
    }

    public void setAcceleration( double acceleration ) {
        a.setValue( acceleration );
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

    public boolean isAccelDriven() {
        return updateStrategy == accelDriven;
    }
    public boolean isPositionDriven(){
        return updateStrategy==positionDriven;
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
    public UpdateStrategy.PositionDriven getPositionDriven() {
        return positionDriven;
    }

    public UpdateStrategy.VelocityDriven getVelocityDriven() {
        return velocityDriven;
    }

    public UpdateStrategy.AccelerationDriven getAccelDriven() {
        return accelDriven;
    }

    public void addPositionData( double position, double time ) {
        x.addValue( position, time );
    }

    public void addPositionData( TimeData data ) {
        addPositionData( data.getValue(), data.getTime() );
    }

    public void addVelocityData( TimeData data ) {
        addVelocityData( data.getValue(), data.getTime() );
    }

    public void addAccelerationData( TimeData data ) {
        addAccelerationData( data.getValue(),data.getTime());
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

    protected UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }
}
