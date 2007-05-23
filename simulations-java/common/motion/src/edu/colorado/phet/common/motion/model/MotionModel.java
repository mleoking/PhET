package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.timeseries.model.RecordableModel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 */

public class MotionModel implements IPositionDriven {
    private ModelState currentState;

    private ArrayList stateHistory = new ArrayList();

    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();

    private UpdateStrategy updateStrategy = positionDriven;

    private SimulationVariable xVariable;
    private SimulationVariable vVariable;
    private SimulationVariable aVariable;

    private ArrayList listeners = new ArrayList();

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
            currentState.setState( (ModelState)o );
        }

        public void resetTime() {
            currentState.setTime( 0.0 );
        }
    };
    private TimeSeriesModel timeSeriesModel;

    public MotionModel() {
        timeSeriesModel = new TimeSeriesModel( recordableModel, 1.0 );
        timeSeriesModel.setRecordMode();
        currentState = createModelState();


        stateHistory.add( copyState() );

        xVariable = new SimulationVariable( getAngle() );
        vVariable = new SimulationVariable( getAngularVelocity() );
        aVariable = new SimulationVariable( getAngularAcceleration() );


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

    protected ModelState createModelState() {
        return new ModelState();
    }

    private ModelState copyState() {
        return currentState.copy();
    }


    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
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

    public ModelState getLastState() {
        return (ModelState)stateHistory.get( stateHistory.size() - 1 );
    }

    public void stepInTime( double dt ) {
        stateHistory.add( copyState() );
        currentState.setAngle( currentState.getMotionBody().getPosition() );
        updateStrategy.update( this, dt );
        currentState.stepInTime( dt );

        xVariable.setValue( getAngle() );
        vVariable.setValue( getAngularVelocity() );
        aVariable.setValue( getAngularAcceleration() );

        doStepInTime( dt );
        notifySteppedInTime();
    }

    protected void doStepInTime( double dt ) {
    }

    public ModelState getStateFromEnd( int i ) {
        return getState( stateHistory.size() - 1 - i );
    }

    private ModelState getState( int index ) {
        return (ModelState)stateHistory.get( index );
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
            ModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngularAcceleration(), state.getTime() );
        }
        return td;
    }

    public TimeData[] getVelocityTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            ModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngularVelocity(), state.getTime() );
        }
        return td;
    }

    public TimeData[] getPositionTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            ModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngle(), state.getTime() );
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
        ModelState state = getLastState().copy();
        state.setTime( 0.0 );
        stateHistory.clear();
        stateHistory.add( state );
        notifySteppedInTime();//todo: this looks like a hack
    }

    public void setAngle( double angle ) {
        currentState.setAngle( angle );
    }

    public MotionBody getMotionBody() {
        return currentState.getMotionBody();
    }

    public double getAngularVelocity() {
        return currentState.getAngularVelocity();
    }

    public double getAngle() {
        return currentState.getAngle();
    }

    public void setAngularVelocity( double newAngVel ) {
        currentState.setAngularVelocity( newAngVel );
    }

    public double getAngularAcceleration() {
        return currentState.getAngularAcceleration();
    }

    public void setAngularAcceleration( double angularAcceleration ) {
        currentState.setAngularAcceleration( angularAcceleration );
    }

    public RecordableModel getTimeSeries() {
        return recordableModel;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public void clockTicked( ClockEvent clockEvent ) {
        timeSeriesModel.clockTicked( clockEvent );
    }

    public ModelState getCurrentState() {
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
