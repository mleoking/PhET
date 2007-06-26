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
    private TimeSeriesModel timeSeriesModel;
    private double time = 0;
    private DefaultTimeSeries timeTimeSeries = new DefaultTimeSeries( time, time );
    private DefaultTimeSeries positionTimeSeries = new DefaultTimeSeries( 0, time );
    private DefaultTimeSeries velocityTimeSeries = new DefaultTimeSeries( 0, time );
    private DefaultTimeSeries accelerationTimeSeries = new DefaultTimeSeries( 0, time );

    /*Different strategies for updating simulation variables*/
    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();

    private UpdateStrategy updateStrategy = positionDriven; //current strategy

    private ArrayList listeners = new ArrayList();

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
//                currentState.setState( (MotionModelState)o );
//                xVariable.setValue( ( (MotionModelState)o ).getPosition() );
//                vVariable.setValue( ( (MotionModelState)o ).getVelocity() );
//                aVariable.setValue( ( (MotionModelState)o ).getAcceleration() );
            }

            public void resetTime() {
                MotionModel.this.time = 0;
            }
        };
        timeSeriesModel = new TimeSeriesModel( recordableModel, clock );
        timeSeriesModel.setRecordMode();
//        currentState = createModelState();

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                timeSeriesModel.stepMode( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    protected MotionModelState createModelState() {
        return new MotionModelState();
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

    public void stepInTime( double dt ) {
        time += dt;
        timeTimeSeries.addValue( time, time );
        updateStrategy.update( this, dt );

        doStepInTime( dt );
        notifySteppedInTime();
    }

    /* Any other operations that must be completed before notifySteppedInTime is called should
    be performed here.
     */
    protected void doStepInTime( double dt ) {
    }

    /**
     * Returns values from the last numPts points of the acceleration time series.
     *
     * @param numPts the number of (most recent) points to get
     * @return the time series points.
     */
    public TimeData[] getRecentAccelerationTimeSeries( int numPts ) {
        return accelerationTimeSeries.getRecentSeries( numPts );
    }

    public TimeData[] getRecentVelocityTimeSeries( int numPts ) {
        return velocityTimeSeries.getRecentSeries( numPts );
    }

    public TimeData[] getRecentPositionTimeSeries( int numPts ) {
        return positionTimeSeries.getRecentSeries( numPts );
    }

    public int getAccelerationSampleCount() {
        return accelerationTimeSeries.getSampleCount();
    }

    public int getVelocitySampleCount() {
        return velocityTimeSeries.getSampleCount();
    }

    public int getPositionSampleCount() {
        return positionTimeSeries.getSampleCount();
    }

    public void clear() {
        time = 0;
        positionTimeSeries.clear();
        velocityTimeSeries.clear();
        accelerationTimeSeries.clear();
    }

    public void setPosition( double position ) {
        positionTimeSeries.setValue( position );
    }

    public double getVelocity() {
        return velocityTimeSeries.getValue();
    }

    public double getPosition() {
        return positionTimeSeries.getValue();
    }

    public double getAcceleration() {
        return accelerationTimeSeries.getValue();
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public double getRecentTime( int numPrevSamples ) {
        return timeTimeSeries.getRecentData( numPrevSamples ).getValue();
    }

    public void setVelocity( double v ) {
        velocityTimeSeries.setValue( v );
    }

    public void setAcceleration( double v ) {
        accelerationTimeSeries.setValue( v );
    }

    public TimeData getLastPosition() {
        return positionTimeSeries.getRecentData( 0 );
    }

    public TimeData getLastVelocity() {
        return velocityTimeSeries.getRecentData( 0 );
    }

    public TimeData getLastAcceleration() {
        return accelerationTimeSeries.getRecentData( 0 );
    }

    //todo this is a workaround to temporarily accommodate RotationApplication 
    public MotionModelState getCurrentState() {
        MotionModelState modelState = new MotionModelState();
        modelState.setAcceleration( getAcceleration() );
        modelState.setVelocity( getVelocity() );
        modelState.setPosition( getPosition() );
        modelState.setTime( time );
        return modelState;
    }

    public void addPositionData( double position, double time ) {
        positionTimeSeries.addValue( position, time );
    }

    public void addVelocityData( double velocity, double time ) {
        velocityTimeSeries.addValue( velocity, time );
    }

    public void addAccelerationData( double accel, double time ) {
        accelerationTimeSeries.addValue( accel, time );
    }

    public TimeData getVelocity( int index ) {
        return velocityTimeSeries.getData( index );
    }

    public TimeData getMaxVelocity() {
        return velocityTimeSeries.getMax();
    }

    public TimeData getMaxAcceleration() {
        return accelerationTimeSeries.getMax();
    }

    public TimeData getMinAcceleration() {
        return accelerationTimeSeries.getMin();
    }

    public ISimulationVariable getXVariable() {
        return positionTimeSeries;
    }

    public ISimulationVariable getVVariable() {
        return velocityTimeSeries;
    }

    public ISimulationVariable getAVariable() {
        return accelerationTimeSeries;
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
        return time;
    }

}
