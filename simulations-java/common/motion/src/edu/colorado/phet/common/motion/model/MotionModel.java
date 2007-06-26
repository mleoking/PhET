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

public class MotionModel implements IPositionDriven{
    private TimeSeriesModel timeSeriesModel;

    private double time = 0;
    private DefaultTimeSeries timeTimeSeries = new DefaultTimeSeries();

    private MotionBodySeries motionBodySeries = new MotionBodySeries();
    private MotionBodyState motionBodyState = new MotionBodyState();//current state

    public MotionBodySeries getMotionBodySeries() {
        return motionBodySeries;
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
                MotionModel.this.time = ( (Double)o ).doubleValue();
                System.out.println( "MotionModel.setState: time=" + MotionModel.this.time );
                
                motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValueForTime( time ) );
                motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValueForTime( time ) );
                motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValueForTime( time ) );
            }

            public void resetTime() {
                MotionModel.this.time = 0;
            }
        };
        timeSeriesModel = new TimeSeriesModel( recordableModel, clock ) {

            public void setRecordMode() {
                double x = motionBodyState.getPosition();
                double v = motionBodyState.getVelocity();
                double a = motionBodyState.getAcceleration();
                setPlaybackTime( getRecordTime() );//todo: temporary workaround
                super.setRecordMode();
                motionBodyState.setPosition( x );
                motionBodyState.setVelocity( v );
                motionBodyState.setAcceleration( a );
            }
        };
        timeSeriesModel.setRecordMode();
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                timeSeriesModel.stepMode( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    public void stepInTime( double dt ) {
        time += dt;
        timeTimeSeries.addValue( time, time );
        
        motionBodySeries.stepInTime(time,motionBodyState, dt);

        motionBodyState.setPosition( motionBodySeries.getXTimeSeries().getValue() );
        motionBodyState.setVelocity( motionBodySeries.getVTimeSeries().getValue() );
        motionBodyState.setAcceleration( motionBodySeries.getATimeSeries().getValue() );

        doStepInTime( dt );
    }

    /* Any other operations that must be completed before notifySteppedInTime is called should
    be performed here.
     */
    protected void doStepInTime( double dt ) {
    }

    public void clear() {
        time = 0;
        motionBodySeries.clear();
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public ISimulationVariable getXVariable() {
        final DefaultSimulationVariable x = new DefaultSimulationVariable();
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void positionChanged( double dx ) {
                x.setValue( motionBodyState.getPosition() );
            }
        } );
        x.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setPosition( x.getValue() );
            }
        } );
        return x;
    }

    public ISimulationVariable getVVariable() {
        final DefaultSimulationVariable v = new DefaultSimulationVariable();
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void velocityChanged() {
                v.setValue( motionBodyState.getVelocity() );
            }
        } );
        v.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setVelocity( v.getValue() );
            }
        } );
        return v;
    }

    public ISimulationVariable getAVariable() {
        final DefaultSimulationVariable a = new DefaultSimulationVariable();
        motionBodyState.addListener( new MotionBodyState.Adapter() {
            public void accelerationChanged() {
                a.setValue( motionBodyState.getAcceleration() );
            }
        } );
        a.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                motionBodyState.setAcceleration( a.getValue() );
            }
        } );
        return a;
    }

    public MotionBodyState getMotionBody() {
        return motionBodyState;
    }

    public double getTime() {
        return time;
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
