package edu.colorado.phet.rotation.model;

import edu.colorado.phet.rotation.motion.ModelState;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 22, 2007, 11:49:32 PM
 */
public class RotationModelState extends ModelState {
    private ArrayList rotationBodies = new ArrayList();

    public RotationModelState() {
        setMotionBody( new RotationPlatform() );
    }

    public void setState( ModelState state ) {
        super.setState( state );
        if( !( state instanceof RotationModelState ) ) {
            throw new IllegalArgumentException( "state should have been " + RotationModelState.class.getName() + ", instead was: " + state.getClass().getName() );
        }
        RotationModelState rotationState = (RotationModelState)state;
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
