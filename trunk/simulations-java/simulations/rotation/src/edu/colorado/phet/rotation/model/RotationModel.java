package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.view.RotationBodyNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 22, 2007, 11:37:56 PM
 */
public class RotationModel extends MotionModel implements RotationBodyNode.RotationBodyEnvironment, IPositionDriven {
    private RotationPlatform rotationPlatform;
    private ArrayList rotationBodies = new ArrayList();

    public RotationModel( ConstantDtClock clock ) {
        super( clock );
        rotationPlatform = new RotationPlatform();
        addRotationBody( new RotationBody("ladybug.gif") );
        addRotationBody( new RotationBody("beetle.gif") );
    }

    protected void setTime( double time ) {
        super.setTime( time );
        rotationPlatform.setTime( time );
        for( int i = 0; i < rotationBodies.size(); i++ ) {
            RotationBody rotationBody = (RotationBody)rotationBodies.get( i );
            rotationBody.setTime(time);
        }
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        rotationPlatform.stepInTime( getTime(), dt );
        for( int i = 0; i < rotationBodies.size(); i++ ) {
            RotationBody rotationBody = (RotationBody)rotationBodies.get( i );
            rotationBody.stepInTime( getTime(), dt );
        }
    }

    public void clear() {
        super.clear();
        rotationPlatform.clear();
    }

    public void dropBody( RotationBody rotationBody ) {
        Point2D loc = rotationBody.getPosition();
//        RotationPlatform platform = (RotationPlatform)getMotionBodyState();//todo: strong typing
        if( rotationPlatform.containsPosition( loc ) ) {
            rotationBody.setOnPlatform( rotationPlatform );
        }
        else {
            rotationBody.setOffPlatform();
        }
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

    public void setPositionDriven() {
        rotationPlatform.setPositionDriven();
    }

    public PositionDriven getPositionDriven() {
        return rotationPlatform.getPositionDriven();
    }

    public ISimulationVariable getPlatformAngleVariable() {
        return rotationPlatform.getXVariable();
    }

    public ITimeSeries getPlatformAngleTimeSeries() {
        return rotationPlatform.getXTimeSeries();
    }

    public ISimulationVariable getPlatformVelocityVariable() {
        return rotationPlatform.getVVariable();
    }

    public ITimeSeries getPlatformVelocityTimeSeries() {
        return rotationPlatform.getVTimeSeries();
    }

    public UpdateStrategy getVelocityDriven() {
        return rotationPlatform.getVelocityDriven();
    }

    public ISimulationVariable getPlatformAccelVariable() {
        return rotationPlatform.getAVariable();
    }

    public ITimeSeries getPlatformAccelTimeSeries() {
        return rotationPlatform.getATimeSeries();
    }

    public UpdateStrategy getAccelDriven() {
        return rotationPlatform.getAccelDriven();
    }

}
