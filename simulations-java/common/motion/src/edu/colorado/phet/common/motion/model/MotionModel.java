package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 */

public class MotionModel extends BasicMotionModel implements IPositionDriven {

    private MotionBody motionBody;

    public MotionModel( IClock clock ) {
        super( clock );
        motionBody = createMotionBody();
    }

    //todo: temporary workaround to resolve problems with offset in record/playback times
    protected void setRecordMode() {
        if( motionBody != null ) {
            double x = motionBody.getMotionBodyState().getPosition();
            double v = motionBody.getMotionBodyState().getVelocity();
            double a = motionBody.getMotionBodyState().getAcceleration();
            getTimeSeriesModel().setPlaybackTime( getTimeSeriesModel().getRecordTime() );
            getTimeSeriesModel().superSetRecordMode();
            motionBody.getMotionBodyState().setPosition( x );
            motionBody.getMotionBodyState().setVelocity( v );
            motionBody.getMotionBodyState().setAcceleration( a );
        }
        else {
            getTimeSeriesModel().superSetRecordMode();
        }
    }

    public MotionBodySeries getMotionBodySeries() {
        return motionBody.getMotionBodySeries();
    }

    protected void setTime( double time ) {
        super.setTime( time );
        motionBody.setTime( time );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        motionBody.stepInTime( time, dt );
    }

    public void clear() {
        super.clear();
        motionBody.clear();
    }

    protected MotionBody createMotionBody() {
        return new MotionBody();
    }

    public ISimulationVariable getXVariable() {
        return motionBody.getXVariable();
    }

    public ISimulationVariable getVVariable() {
        return motionBody.getVVariable();
    }

    public ISimulationVariable getAVariable() {
        return motionBody.getAVariable();
    }

    public MotionBodyState getMotionBodyState() {
        return motionBody.getMotionBodyState();
    }

    public void setPositionDriven() {
        getMotionBodySeries().setPositionDriven();
    }

    public ITimeSeries getXTimeSeries() {
        return getMotionBodySeries().getXTimeSeries();
    }

    public ITimeSeries getVTimeSeries() {
        return getMotionBodySeries().getVTimeSeries();
    }

    public ITimeSeries getATimeSeries() {
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
