package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.model.TimeState;

/**
 * Represents a base model which can be used for managing collections of MotionBody objects.
 * See SingleBodyMotionModel as an example.
 */
public class MotionModel {
    private TimeSeriesModel timeSeriesModel;
    private ITemporalVariable timeVariable;

    public MotionModel( ConstantDtClock clock, TimeSeriesFactory timeSeriesFactory ) {
        timeVariable = new DefaultTemporalVariable( timeSeriesFactory );
        RecordableModel recordableModel = new RecordableModel() {
            public void stepInTime( double simulationTimeChange ) {
                MotionModel.this.stepInTime( simulationTimeChange );
            }

            public Object getState() {
                return new Double( timeVariable.getValue() );
            }

            public void setState( Object o ) {
                //the setState paradigm is used to allow attachment of listeners to model substructure
                //states are copied without listeners
                setPlaybackTime( ( (Double) o ).doubleValue() );
            }

            public void resetTime() {
                timeVariable.setValue( 0.0 );
            }

            public void clear() {
                MotionModel.this.clear();
            }
        };
        timeSeriesModel = createTimeSeriesModel( recordableModel, clock );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                if ( timeSeriesModel.isRecordMode() ) {
                    TimeState timeState = timeSeriesModel.getSeries().getLastPoint();
                    if ( timeState != null ) {
                        Object o = timeState.getValue();
                        setPlaybackTime( ( (Double) o ).doubleValue() );
                    }
                }
            }
        } );
        timeSeriesModel.setRecordMode();
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                timeSeriesModel.stepMode( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    protected TimeSeriesModel createTimeSeriesModel( RecordableModel recordableModel, ConstantDtClock clock ) {
        return new MotionTimeSeriesModel( recordableModel, clock );
    }

    protected void setPlaybackTime( double time ) {
        timeVariable.setPlaybackTime( time );
    }

    public void stepInTime( double dt ) {
        double newTime = timeVariable.getValue() + dt;
        timeVariable.addValue( newTime, newTime );
    }

    public void clear() {
        timeVariable.setValue( 0.0 );
        timeVariable.clear();
        timeSeriesModel.clear();
    }

    public double getTime() {
        return timeVariable.getValue();
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public void resetAll() {
        clear();
    }

    public ITemporalVariable getTimeVariable() {
        return timeVariable;
    }

    public void setMaxAllowedRecordTime( double maxAllowedRecordTime ) {
        getTimeSeriesModel().setMaxAllowedRecordTime( maxAllowedRecordTime );
    }
}
