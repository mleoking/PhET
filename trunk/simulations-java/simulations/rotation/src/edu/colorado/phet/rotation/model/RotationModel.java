package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.MotionModelState;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.DefaultSimulationVariable;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.rotation.view.RotationBodyNode;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * May 22, 2007, 11:37:56 PM
 */
public class RotationModel extends MotionModel implements RotationBodyNode.RotationBodyEnvironment {
    private DefaultSimulationVariable xPositionVariable = new DefaultSimulationVariable();
    private DefaultSimulationVariable yPositionVariable = new DefaultSimulationVariable();
    private DefaultSimulationVariable speedVariable = new DefaultSimulationVariable();
    private DefaultSimulationVariable xVelocityVariable = new DefaultSimulationVariable();
    private DefaultSimulationVariable yVelocityVariable = new DefaultSimulationVariable();
    private DefaultSimulationVariable centripetalAcceleration = new DefaultSimulationVariable();

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

    public ISimulationVariable getXPositionVariable() {
        return xPositionVariable;
    }

    public ISimulationVariable getYPositionVariable() {
        return yPositionVariable;
    }

    public ISimulationVariable getSpeedVariable() {
        return speedVariable;
    }

    public ISimulationVariable getXVelocityVariable() {
        return xVelocityVariable;
    }

    public ISimulationVariable getYVelocityVariable() {
        return yVelocityVariable;
    }

    public ISimulationVariable getCentripetalAcceleration() {
        return centripetalAcceleration;
    }

    public RotationPlatform getRotationPlatform() {
        return (RotationPlatform)getCurrentState().getMotionBody();
    }

    private void addRotationBody( RotationBody rotationBody ) {
        getCurrentRotationModelState().addRotationBody( rotationBody );
    }

    private RotationMotionModelState getCurrentRotationModelState() {
        return (RotationMotionModelState)super.getCurrentState();
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

}
