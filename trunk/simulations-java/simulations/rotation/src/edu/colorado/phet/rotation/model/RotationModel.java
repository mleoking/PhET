package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.MotionModelState;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.SimulationVariable;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.rotation.view.RotationBodyNode;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * May 22, 2007, 11:37:56 PM
 */
public class RotationModel extends MotionModel implements RotationBodyNode.RotationBodyEnvironment {
    private SimulationVariable xPositionVariable = new SimulationVariable();
    private SimulationVariable yPositionVariable = new SimulationVariable();
    private SimulationVariable speedVariable = new SimulationVariable();
    private SimulationVariable xVelocityVariable = new SimulationVariable();
    private SimulationVariable yVelocityVariable = new SimulationVariable();
    private SimulationVariable centripetalAcceleration = new SimulationVariable();

    public RotationModel( IClock clock ) {
        super( clock );
        addRotationBody( new RotationBody() );
        updateSimulationVariables();
    }

    protected void doStepInTime( double dt ) {
        super.doStepInTime( dt );
        updateSimulationVariables();
    }

    private void updateSimulationVariables() {
        xPositionVariable.setValue( getRotationBody( 0 ).getX() );
        yPositionVariable.setValue( getRotationBody( 0 ).getY() );
        speedVariable.setValue( getRotationBody( 0 ).getVelocity().getMagnitude() );
        xVelocityVariable.setValue( getRotationBody( 0 ).getX() );
        yVelocityVariable.setValue( getRotationBody( 0 ).getY() );
        centripetalAcceleration.setValue( getRotationBody( 0 ).getAcceleration().getMagnitude() );
    }

    public void dropBody( RotationBody rotationBody ) {
        Point2D loc = rotationBody.getPosition();
        RotationPlatform platform = (RotationPlatform)getCurrentState().getMotionBody();//todo: strong typing
        if( platform.containsPosition( loc ) ) {
            rotationBody.setOnPlatform( platform );
        }
        else {
            rotationBody.setOffPlatform();
        }
    }

    protected MotionModelState createModelState() {
        RotationMotionModelState modelState = new RotationMotionModelState();
        modelState.getMotionBody().addListener( new RotationPlatform.Listener() {//todo: memory leak
            public void angleChanged( double dtheta ) {
                getXPositionVariable().setValue( getCurrentState().getMotionBody().getPosition() );
            }
        } );
        return modelState;
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

    public RotationPlatform getRotationPlatform() {
        return (RotationPlatform)getCurrentState().getMotionBody();
    }

    private void addRotationBody( RotationBody rotationBody ) {
        getCurrentRotationModelState().addRotationBody( rotationBody );
    }

    private RotationMotionModelState getCurrentRotationModelState() {
        return (RotationMotionModelState)getCurrentState();
    }

    public int getNumRotationBodies() {
        return getCurrentRotationModelState().getNumRotationBodies();
    }

    public RotationBody getRotationBody( int i ) {
        return getCurrentRotationModelState().getRotationBody( i );
    }

    public RotationBody getBody( int i ) {
        return getCurrentRotationModelState().getRotationBody( i );
    }

    //Todo: handle derivative offsets for rotation-specific variables
    public double getTime( SimulationVariable simulationVariable ) {
        return super.getTime( simulationVariable );
    }
}
