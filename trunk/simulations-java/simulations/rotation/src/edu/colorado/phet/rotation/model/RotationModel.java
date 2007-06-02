package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.ModelState;
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
    private SimulationVariable xPositionVariable;
    private SimulationVariable yPositionVariable;
    private SimulationVariable speedVariable;
    private SimulationVariable xVelocityVariable;
    private SimulationVariable yVelocityVariable;
    private SimulationVariable centripetalAcceleration;

    public RotationModel( IClock clock) {
        super(clock );
        addRotationBody( new RotationBody() );
        xPositionVariable = new SimulationVariable( getRotationBody( 0 ).getX() );
        yPositionVariable = new SimulationVariable( getRotationBody( 0 ).getY() );
        speedVariable = new SimulationVariable( getRotationBody( 0 ).getVelocity().getMagnitude() );
        xVelocityVariable = new SimulationVariable( getRotationBody( 0 ).getVelocity().getX() );
        yVelocityVariable = new SimulationVariable( getRotationBody( 0 ).getVelocity().getY() );
        centripetalAcceleration = new SimulationVariable( getRotationBody( 0 ).getAcceleration().getMagnitude() );
    }

    protected void doStepInTime( double dt ) {
        super.doStepInTime( dt );
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

    protected ModelState createModelState() {
        RotationModelState modelState = new RotationModelState();
        modelState.getMotionBody().addListener( new RotationPlatform.Listener() {
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

    private RotationModelState getCurrentRotationModelState() {
        return (RotationModelState)getCurrentState();
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
