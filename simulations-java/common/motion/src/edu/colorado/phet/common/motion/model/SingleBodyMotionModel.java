package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

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

    public MotionBodySeries getMotionBodySeries() {
        return motionBody.getMotionBodySeries();
    }

    public IVariable getXVariable() {
        return motionBody.getXVariable();
    }

    public IVariable getVVariable() {
        return motionBody.getVVariable();
    }

    public IVariable getAVariable() {
        return motionBody.getAVariable();
    }

    public MotionBodyState getMotionBodyState() {
        return motionBody.getMotionBodyState();
    }

    public void setPositionDriven() {
        getMotionBodySeries().setPositionDriven();
    }

    public DefaultTimeSeries getXTimeSeries() {
        return getMotionBodySeries().getXTimeSeries();
    }

    public DefaultTimeSeries getVTimeSeries() {
        return getMotionBodySeries().getVTimeSeries();
    }

    public DefaultTimeSeries getATimeSeries() {
        return getMotionBodySeries().getATimeSeries();
    }

    public UpdateStrategy getPositionDriven() {
        return getMotionBodySeries().getPositionDriven();
    }

    public UpdateStrategy getVelocityDriven() {
        return getMotionBodySeries().getVelocityDriven();
    }

    public UpdateStrategy getAccelDriven() {
        return getMotionBodySeries().getAccelDriven();
    }

    public void setAccelerationDriven() {
        getMotionBodySeries().setAccelerationDriven();
    }

    public void setVelocityDriven() {
        getMotionBodySeries().setVelocityDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        getMotionBodySeries().setUpdateStrategy( updateStrategy );
    }

}
