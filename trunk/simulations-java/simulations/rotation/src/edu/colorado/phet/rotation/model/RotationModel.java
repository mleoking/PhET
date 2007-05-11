package edu.colorado.phet.rotation.model;

import edu.colorado.phet.rotation.RotationBody;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
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

    private SimulationVariable xPositionVariable;
    private SimulationVariable yPositionVariable;
    private SimulationVariable speedVariable;
    private SimulationVariable xVelocityVariable;
    private SimulationVariable yVelocityVariable;
    private SimulationVariable centripetalAcceleration;

    private ArrayList listeners = new ArrayList();
    private ArrayList rotationBodies = new ArrayList();

    public RotationModel() {
        addRotationBody( new RotationBody() );
        rotationModelStates.add( new RotationModelState() );

        xVariable = new SimulationVariable( getLastState().getAngle() );
        vVariable = new SimulationVariable( getLastState().getAngularVelocity() );
        aVariable = new SimulationVariable( getLastState().getAngularAcceleration() );

        xPositionVariable = new SimulationVariable( getLastState().getBody( 0 ).getX( getLastState() ) );
        yPositionVariable = new SimulationVariable( getLastState().getBody( 0 ).getY( getLastState() ) );
        speedVariable = new SimulationVariable( getLastState().getBody( 0 ).getVelocity( getLastState() ).getMagnitude() );
        xVelocityVariable = new SimulationVariable( getLastState().getBody( 0 ).getVelocity( getLastState() ).getX() );
        yVelocityVariable = new SimulationVariable( getLastState().getBody( 0 ).getVelocity( getLastState() ).getY() );
        centripetalAcceleration = new SimulationVariable( getLastState().getBody( 0 ).getAcceleration( getLastState() ).getMagnitude() );

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

    private void addRotationBody( RotationBody rotationBody ) {
        rotationBodies.add( rotationBody );
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

        xPositionVariable.setValue( getLastState().getBody( 0 ).getX( getLastState() ) );
        yPositionVariable.setValue( getLastState().getBody( 0 ).getY( getLastState() ) );
        speedVariable.setValue( getLastState().getBody( 0 ).getVelocity( getLastState() ).getMagnitude() );
        xVelocityVariable.setValue( getLastState().getBody( 0 ).getX( getLastState() ) );
        yVelocityVariable.setValue( getLastState().getBody( 0 ).getY( getLastState() ) );
        centripetalAcceleration.setValue( getLastState().getBody( 0 ).getAcceleration( getLastState() ).getMagnitude() );

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

    public SimulationVariable getXPositionVariable() {
        return xPositionVariable;
    }

    public SimulationVariable getYPositionVariable() {
        return yPositionVariable;
    }

    public SimulationVariable getSpeedVariable() {
        return speedVariable;
    }

    public SimulationVariable getXVelocityVariable() {
        return xVelocityVariable;
    }

    public SimulationVariable getYVelocityVariable() {
        return yVelocityVariable;
    }

    public SimulationVariable getCentripetalAcceleration() {
        return centripetalAcceleration;
    }

    public void clear() {
        RotationModelState state = getLastState().copy();
        state.setTime( 0.0 );
        rotationModelStates.clear();
        rotationModelStates.add( state );
        notifySteppedInTime();
    }

    public void setAngle( double angle ) {
        getXVariable().setValue( angle );
    }

    public int getNumRotationBodies() {
        return rotationBodies.size();
    }

    public RotationBody getRotationBody( int i ) {
        return (RotationBody)rotationBodies.get( i );
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
