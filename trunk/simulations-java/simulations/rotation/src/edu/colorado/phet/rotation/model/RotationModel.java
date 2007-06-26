package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.DefaultSimulationVariable;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.rotation.view.RotationBodyNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

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
    private ArrayList rotationBodies = new ArrayList();
    private RotationPlatform rotationPlatform;

    public RotationModel( IClock clock ) {
        super( clock );
        addRotationBody( new RotationBody() );
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
        RotationPlatform platform = (RotationPlatform)getMotionBody();//todo: strong typing
        if( platform.containsPosition( loc ) ) {
            rotationBody.setOnPlatform( platform );
        }
        else {
            rotationBody.setOffPlatform();
        }
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
        return rotationPlatform;
    }

    private void addRotationBody( RotationBody rotationBody ) {
        rotationBodies.add( rotationBody );
    }

    public int getNumRotationBodies() {
        return rotationBodies.size();
    }

    public RotationBody getRotationBody( int i ) {
        return (RotationBody)rotationBodies.get( i );
    }

    public RotationBody getBody( int i ) {
        return getRotationBody( i );
    }

}
