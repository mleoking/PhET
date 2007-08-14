package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

import java.sql.CallableStatement;

import junit.extensions.TestDecorator;

/**
 * This class contains a single MotionBody, and convenience methods for interacting with it.
 */
public class SingleBodyMotionModel extends MotionModel implements IPositionDriven, IUpdateStrategy {

    private MotionBody motionBody;

    public SingleBodyMotionModel( ConstantDtClock clock ) {
        super( clock );
        motionBody = new MotionBody();
    }

    protected void setTime( double time ) {
        super.setTime( time );
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

    public ITemporalVariable getVTimeSeries() {
        return motionBody.getVelocityVariable();
    }

    public ITemporalVariable getATimeSeries() {
        return motionBody.getAccelerationVariable();
    }

    public PositionDriven getPositionDriven() {
        return motionBody.getPositionDriven();
    }

    public VelocityDriven getVelocityDriven() {
        return motionBody.getVelocityDriven();
    }

    public AccelerationDriven getAccelDriven() {
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
        return motionBody.getRecentAccelerationTimeSeries(i);
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
        return motionBody.getVelocity(index);
    }
}
