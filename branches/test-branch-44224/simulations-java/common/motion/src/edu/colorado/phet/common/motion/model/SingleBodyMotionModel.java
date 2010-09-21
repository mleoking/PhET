package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class contains a single MotionBody, and convenience methods for interacting with it.
 */
public class SingleBodyMotionModel extends MotionModel implements IPositionDriven, UpdateableObject {

    private MotionBody motionBody;

    public SingleBodyMotionModel( ConstantDtClock clock ) {
        this(4,4,4,clock);
    }

    public SingleBodyMotionModel( int smoothXV,int smoothXA,int smoothVA,ConstantDtClock clock ) {
        this( smoothXV,smoothXA,smoothVA,clock, new TimeSeriesFactory.Default() );
    }

    public SingleBodyMotionModel( int smoothXV, int smoothXA, int smoothVA, ConstantDtClock clock, TimeSeriesFactory timeSeriesFactory ) {
        super( clock, timeSeriesFactory );
        motionBody = new MotionBody( smoothXV,smoothXA,smoothVA,timeSeriesFactory );
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        motionBody.setTime( time );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        motionBody.stepInTime( getTime(), dt );
    }

    public void clear() {
        super.clear();
        motionBody.clear();
    }

    public ITemporalVariable getXVariable() {
        return motionBody.getPositionVariable();
    }

    public ITemporalVariable getVVariable() {
        return motionBody.getVelocityVariable();
    }

    public ITemporalVariable getAVariable() {
        return motionBody.getAccelerationVariable();
    }

    public MotionBody getMotionBody() {
        return motionBody;
    }

    public void setPositionDriven() {
        motionBody.setPositionDriven();
    }

    public ITemporalVariable getXTimeSeries() {
        return motionBody.getPositionVariable();
    }

    public UpdateStrategy.PositionDriven getPositionDriven() {
        return motionBody.getPositionDriven();
    }

    public UpdateStrategy.VelocityDriven getVelocityDriven() {
        return motionBody.getVelocityDriven();
    }

    public UpdateStrategy.AccelerationDriven getAccelDriven() {
        return motionBody.getAccelDriven();
    }

    public void setAccelerationDriven() {
        motionBody.setAccelerationDriven();
    }

    public void setVelocityDriven() {
        motionBody.setVelocityDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        motionBody.setUpdateStrategy( updateStrategy );
    }

    public TimeData getLastPosition() {
        return motionBody.getLastPosition();
    }

    public TimeData getLastVelocity() {
        return motionBody.getLastVelocity();
    }

    public TimeData getLastAcceleration() {
        return motionBody.getLastAcceleration();
    }

    public TimeData[] getRecentAccelerationTimeSeries( int i ) {
        return motionBody.getRecentAccelerationTimeSeries( i );
    }

    public TimeData getMaxVelocity() {
        return motionBody.getMaxVelocity();
    }

    public TimeData getMaxAcceleration() {
        return motionBody.getMaxAcceleration();
    }

    public TimeData getMinAcceleration() {
        return motionBody.getMinAcceleration();
    }

    public int getVelocitySampleCount() {
        return motionBody.getVelocitySampleCount();
    }

    public TimeData getVelocity( int index ) {
        return motionBody.getVelocity( index );
    }
}
