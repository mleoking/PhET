package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 */

public class MotionModel implements IPositionDriven {
    private TimeSeriesModel timeSeriesModel;

    private double time = 0;
    private DefaultTimeSeries timeTimeSeries = new DefaultTimeSeries();
    private MotionBody motionBody = new MotionBody();

    public MotionBodySeries getMotionBodySeries() {
        return motionBody.getMotionBodySeries();
    }

    public MotionModel( IClock clock ) {
        RecordableModel recordableModel = new RecordableModel() {
            public void stepInTime( double simulationTimeChange ) {
                MotionModel.this.stepInTime( simulationTimeChange );
            }

            public Object getState() {
                return new Double( time );
            }

            public void setState( Object o ) {
                //the setState paradigm is used to allow attachment of listeners to model substructure
                //states are copied without listeners
                setTime( ( (Double)o ).doubleValue() );
            }

            public void resetTime() {
                MotionModel.this.time = 0;
            }
        };
        timeSeriesModel = new TimeSeriesModel( recordableModel, clock ) {

            public void setRecordMode() {
                //todo: temporary workaround to resolve problems with offset in record/playback times
                double x = motionBody.getMotionBodyState().getPosition();
                double v = motionBody.getMotionBodyState().getVelocity();
                double a = motionBody.getMotionBodyState().getAcceleration();
                setPlaybackTime( getRecordTime() );
                super.setRecordMode();
                motionBody.getMotionBodyState().setPosition( x );
                motionBody.getMotionBodyState().setVelocity( v );
                motionBody.getMotionBodyState().setAcceleration( a );
            }
        };
        timeSeriesModel.setRecordMode();
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                timeSeriesModel.stepMode( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    private void setTime( double time ) {
        this.time = time;
        motionBody.setTime( time );
    }

    public void stepInTime( double dt ) {
        time += dt;
        timeTimeSeries.addValue( time, time );
        motionBody.stepInTime( time, dt );
    }

    public void clear() {
        time = 0;
        motionBody.clear();
        timeSeriesModel.clear();
    }

    public double getTime() {
        return time;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
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

    public MotionBodyState getMotionBody() {
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
