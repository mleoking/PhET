package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.MotionModelState;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 22, 2007, 11:49:32 PM
 */
public class RotationMotionModelState extends MotionModelState {
    private ArrayList rotationBodies = new ArrayList();

    public RotationMotionModelState() {
        setMotionBody( new RotationPlatform() );
    }

    public void setState( MotionModelState state ) {
        super.setState( state );
        if( !( state instanceof RotationMotionModelState ) ) {
            throw new IllegalArgumentException( "state should have been " + RotationMotionModelState.class.getName() + ", instead was: " + state.getClass().getName() );
        }
        RotationMotionModelState rotationState = (RotationMotionModelState)state;
        if( rotationBodies.size() != rotationState.rotationBodies.size() ) {
            throw new IllegalArgumentException( "Different number of bodies not supported: orig=" + rotationBodies.size() + ", new=" + rotationState.rotationBodies.size() );
        }
        for( int i = 0; i < rotationBodies.size(); i++ ) {
            RotationBody rotationBody = (RotationBody)rotationBodies.get( i );
            rotationBody.setState( (RotationBody)rotationState.rotationBodies.get( i ) );
        }
    }

    public RotationBody getRotationBody( int i ) {
        return (RotationBody)rotationBodies.get( i );
    }

    public int getNumRotationBodies() {
        return rotationBodies.size();
    }

    public void addRotationBody( RotationBody rotationBody ) {
        rotationBodies.add( rotationBody );
    }
}
