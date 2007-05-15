package edu.colorado.phet.rotation.model;

import edu.colorado.phet.rotation.view.PlatformNode;
import edu.colorado.phet.rotation.view.RotationBodyNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 */

public class RotationModel implements RotationBodyNode.RotationBodyEnvironment, PlatformNode.RotationPlatformEnvironment {
    private RotationModelState currentState;

    private ArrayList stateHistory = new ArrayList();

    private PositionDriven positionDriven = new PositionDriven();
    private VelocityDriven velocityDriven = new VelocityDriven();
    private AccelerationDriven accelDriven = new AccelerationDriven();

    private UpdateStrategy updateStrategy = positionDriven;

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

    public RotationModel() {
        currentState = new RotationModelState();
        currentState.getRotationPlatform().addListener( new RotationPlatform.Listener() {
            public void angleChanged( double dtheta ) {
                xVariable.setValue( currentState.getRotationPlatform().getAngle() );
            }
        } );
        addRotationBody( new RotationBody() );
        stateHistory.add( copyState() );

        xVariable = new SimulationVariable( getAngle() );
        vVariable = new SimulationVariable( getAngularVelocity() );
        aVariable = new SimulationVariable( getAngularAcceleration() );

        xPositionVariable = new SimulationVariable( getRotationBody( 0 ).getX() );
        yPositionVariable = new SimulationVariable( getRotationBody( 0 ).getY() );
        speedVariable = new SimulationVariable( getRotationBody( 0 ).getVelocity().getMagnitude() );
        xVelocityVariable = new SimulationVariable( getRotationBody( 0 ).getVelocity().getX() );
        yVelocityVariable = new SimulationVariable( getRotationBody( 0 ).getVelocity().getY() );
        centripetalAcceleration = new SimulationVariable( getRotationBody( 0 ).getAcceleration().getMagnitude() );

        xVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                currentState.getRotationPlatform().setAngle( xVariable.getValue() );
            }
        } );
        vVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                currentState.getRotationPlatform().setAngularVelocity( vVariable.getValue() );
            }
        } );
        aVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                currentState.getRotationPlatform().setAngularAcceleration( aVariable.getValue() );
            }
        } );
    }

    private RotationModelState copyState() {
        return currentState.copy();
    }

    private void addRotationBody( RotationBody rotationBody ) {
        currentState.addRotationBody( rotationBody );
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
        return (RotationModelState)stateHistory.get( stateHistory.size() - 1 );
    }

    public void stepInTime( double dt ) {
        stateHistory.add( copyState() );
        currentState.setAngle( currentState.getRotationPlatform().getAngle() );
        updateStrategy.update( this, dt );
        currentState.stepInTime( dt );

        xVariable.setValue( getAngle() );
        vVariable.setValue( getAngularVelocity() );
        aVariable.setValue( getAngularAcceleration() );

        xPositionVariable.setValue( getRotationBody( 0 ).getX() );
        yPositionVariable.setValue( getRotationBody( 0 ).getY() );
        speedVariable.setValue( getRotationBody( 0 ).getVelocity().getMagnitude() );
        xVelocityVariable.setValue( getRotationBody( 0 ).getX() );
        yVelocityVariable.setValue( getRotationBody( 0 ).getY() );
        centripetalAcceleration.setValue( getRotationBody( 0 ).getAcceleration().getMagnitude() );

        notifySteppedInTime();
    }

    public RotationModelState getStateFromEnd( int i ) {
        return getState( stateHistory.size() - 1 - i );
    }

    private RotationModelState getState( int index ) {
        return (RotationModelState)stateHistory.get( index );
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
        stateHistory.clear();
        stateHistory.add( state );
        notifySteppedInTime();//todo: this looks like a hack
    }

    public void setAngle( double angle ) {
        currentState.setAngle( angle );
    }

    public int getNumRotationBodies() {
        return currentState.getNumRotationBodies();
    }

    public RotationBody getRotationBody( int i ) {
        return currentState.getRotationBody( i );
    }

    public RotationPlatform getRotationPlatform() {
        return currentState.getRotationPlatform();
    }

    public void dropBody( RotationBody rotationBody ) {
        Point2D loc = rotationBody.getPosition();
        if( currentState.getRotationPlatform().containsPosition( loc ) ) {
            rotationBody.setOnPlatform( currentState.getRotationPlatform() );
        }
        else {
            rotationBody.setOffPlatform();
        }
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

    public RotationBody getBody( int i ) {
        return currentState.getRotationBody( i );
    }

}
