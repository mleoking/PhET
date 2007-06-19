package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 */

public class MotionModel implements IPositionDriven {
    private MotionModelState currentState;

    private ArrayList stateHistory = new ArrayList();//history is necessary for computing derivatives

    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();

    private UpdateStrategy updateStrategy = positionDriven;

    private SimulationVariable xVariable;
    private SimulationVariable vVariable;
    private SimulationVariable aVariable;

    private ArrayList listeners = new ArrayList();
    private TimeSeriesModel timeSeriesModel;

    private RecordableModel recordableModel = new RecordableModel() {
        public void stepInTime( double simulationTimeChange ) {
            MotionModel.this.stepInTime( simulationTimeChange );
        }

        public Object getState() {
            return currentState.copy();
        }

        public void setState( Object o ) {
            //the setState paradigm is used to allow attachment of listeners to model substructure
            //states are copied without listeners
            currentState.setState( (MotionModelState)o );
            xVariable.setValue( ( (MotionModelState)o ).getPosition() );
            vVariable.setValue( ( (MotionModelState)o ).getVelocity() );
            aVariable.setValue( ( (MotionModelState)o ).getAcceleration() );
        }

        public void resetTime() {
            currentState.setTime( 0.0 );
        }
    };

    public MotionModel( IClock clock ) {
        timeSeriesModel = new TimeSeriesModel( recordableModel, clock );
        timeSeriesModel.setRecordMode();
        currentState = createModelState();

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                timeSeriesModel.stepMode( clockEvent.getSimulationTimeChange() );
            }
        } );
        stateHistory.add( copyState() );

        xVariable = new SimulationVariable( getPosition() );
        vVariable = new SimulationVariable( getVelocity() );
        aVariable = new SimulationVariable( getAcceleration() );


        xVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                currentState.getMotionBody().setPosition( xVariable.getValue() );
            }
        } );
        vVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                currentState.getMotionBody().setVelocity( vVariable.getValue() );
            }
        } );
        aVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                currentState.getMotionBody().setAcceleration( aVariable.getValue() );
            }
        } );
    }

    protected MotionModelState createModelState() {
        return new MotionModelState();
    }

    private MotionModelState copyState() {
        return currentState.copy();
    }


    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
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

    public MotionModelState getLastState() {
        return (MotionModelState)stateHistory.get( stateHistory.size() - 1 );
    }

    public void stepInTime( double dt ) {
        stateHistory.add( copyState() );
        currentState.setPosition( currentState.getMotionBody().getPosition() );
        updateStrategy.update( this, dt );
        currentState.stepInTime( dt );

        xVariable.setValue( getPosition() );
        vVariable.setValue( getVelocity() );
        aVariable.setValue( getAcceleration() );

        doStepInTime( dt );
        notifySteppedInTime();
    }

    protected void doStepInTime( double dt ) {
    }

    public MotionModelState getStateFromEnd( int i ) {
        return getState( stateHistory.size() - 1 - i );
    }

    private MotionModelState getState( int index ) {
        return (MotionModelState)stateHistory.get( index );
    }

    public TimeData[] getAvailableAccelerationTimeSeries( int numPts ) {
        return getAccelerationTimeSeries( Math.min( numPts, getStateCount() ) );
    }

    /**
     * Returns values from the last numPts points of the acceleration time series.
     *
     * @param numPts the number of (most recent) points to get
     * @return the time series points.
     */
    public TimeData[] getAccelerationTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            MotionModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAcceleration(), state.getTime() );
        }
        return td;
    }

    public TimeData[] getVelocityTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            MotionModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getVelocity(), state.getTime() );
        }
        return td;
    }

    public TimeData[] getPositionTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            MotionModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getPosition(), state.getTime() );
        }
        return td;
    }

    private int getStateCount() {
        return stateHistory.size();
    }

    public TimeData[] getAvailableVelocityTimeSeries( int numPts ) {
        return getVelocityTimeSeries( Math.min( numPts, getStateCount() ) );
    }

    public TimeData[] getAvailablePositionTimeSeries( int numPts ) {
        return getPositionTimeSeries( Math.min( numPts, getStateCount() ) );
    }

    public SimulationVariable getXVariable() {
        return xVariable;
    }

    public SimulationVariable getVVariable() {
        return vVariable;
    }

    public SimulationVariable getAVariable() {
        return aVariable;
    }

    public void clear() {
        MotionModelState state = getLastState().copy();
        state.setTime( 0.0 );
        stateHistory.clear();
        stateHistory.add( state );
        notifySteppedInTime();//todo: this looks like a hack
    }

    public void setPosition( double position ) {
        currentState.setPosition( position );
    }

    public MotionBody getMotionBody() {
        return currentState.getMotionBody();
    }

    public double getVelocity() {
        return currentState.getVelocity();
    }

    public double getPosition() {
        return currentState.getPosition();
    }

    public void setVelocity( double velocity ) {
        currentState.setVelocity( velocity );
    }

    public double getAcceleration() {
        return currentState.getAcceleration();
    }

    public void setAcceleration( double acceleration ) {
        currentState.setAcceleration( acceleration );
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public MotionModelState getCurrentState() {
        return currentState;
    }

    public static interface Listener {
        void steppedInTime();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifySteppedInTime() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.steppedInTime();
        }
    }

    public double getTime() {
        return currentState.getTime();
    }

}
