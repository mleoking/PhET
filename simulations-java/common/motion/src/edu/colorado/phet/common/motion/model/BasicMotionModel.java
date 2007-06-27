package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.model.TimeState;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 11:14:12 PM
 */
public class BasicMotionModel {
    protected TimeSeriesModel timeSeriesModel;
    protected double time = 0;
    private DefaultTimeSeries timeTimeSeries = new DefaultTimeSeries();

    public BasicMotionModel( IClock clock ) {
        RecordableModel recordableModel = new RecordableModel() {
            public void stepInTime( double simulationTimeChange ) {
                BasicMotionModel.this.stepInTime( simulationTimeChange );
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
                BasicMotionModel.this.time = 0;
            }

            public void clear() {
                BasicMotionModel.this.clear();
            }
        };
        timeSeriesModel = new TimeSeriesModel( recordableModel, clock );
        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void modeChanged() {
                if( timeSeriesModel.isRecordMode() ) {
                    TimeState timeState = timeSeriesModel.getSeries().getLastPoint();
                    if( timeState != null ) {
                        Object o = timeState.getValue();
                        setTime( ( (Double)o ).doubleValue() );
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

    protected void setTime( double time ) {
        this.time = time;
    }

    public void stepInTime( double dt ) {
        time += dt;
        timeTimeSeries.addValue( time, time );
    }

    public void clear() {
        time = 0;
        timeSeriesModel.clear();
    }

    public double getTime() {
        return time;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }
}
