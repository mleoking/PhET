package edu.colorado.phet.rotation.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class RotationModel {
    private ArrayList rotationModelStates = new ArrayList();
    private UpdateStrategy updateStrategy = new PositionDriven( 0.0 );

    private PositionDriven positionDriven = new PositionDriven( 0.0 );
    private VelocityDriven velocityDriven = new VelocityDriven( 0.0 );
    private AccelerationDriven accelDriven = new AccelerationDriven( 0.0 );

    private SimulationVariable xVariable;
    private SimulationVariable vVariable;
    private SimulationVariable aVariable;

    private ArrayList listeners = new ArrayList();

    public RotationModel() {
        rotationModelStates.add( new RotationModelState() );

        xVariable = new SimulationVariable( getLastState().getAngle() );
        vVariable = new SimulationVariable( getLastState().getAngularVelocity() );
        aVariable = new SimulationVariable( getLastState().getAngularAcceleration() );

        xVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                positionDriven.setPosition( xVariable.getValue() );
            }
        } );
        vVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                velocityDriven.setVelocity( vVariable.getValue() );
            }
        } );
        aVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                accelDriven.setAcceleration( aVariable.getValue() );
            }
        } );
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

    public RotationModelState getLastState() {
        return (RotationModelState)rotationModelStates.get( rotationModelStates.size() - 1 );
    }

    public void stepInTime( double dt ) {
        RotationModelState state = updateStrategy.update( this, dt );
        rotationModelStates.add( state );

        xVariable.setValue( getLastState().getAngle() );
        vVariable.setValue( getLastState().getAngularVelocity() );
        aVariable.setValue( getLastState().getAngularAcceleration() );

        notifySteppedInTime();
    }

    public RotationModelState getStateFromEnd( int i ) {
        return getState( rotationModelStates.size() - 1 - i );
    }

    private RotationModelState getState( int index ) {
        return (RotationModelState)rotationModelStates.get( index );
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
            RotationModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngularAcceleration(), state.getTime() );
        }
        return td;
    }

    public TimeData[] getVelocityTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            RotationModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngularVelocity(), state.getTime() );
        }
        return td;
    }

    public TimeData[] getPositionTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            RotationModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngle(), state.getTime() );
        }
        return td;
    }

    private int getStateCount() {
        return rotationModelStates.size();
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
}
