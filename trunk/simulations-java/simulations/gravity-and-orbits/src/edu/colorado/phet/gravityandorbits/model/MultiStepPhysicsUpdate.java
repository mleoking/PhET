package edu.colorado.phet.gravityandorbits.model;

/**
 * @author Sam Reid
 */
public class MultiStepPhysicsUpdate implements PhysicsUpdate {
    private final int numSteps;
    private final PhysicsUpdate physicsUpdate;

    public MultiStepPhysicsUpdate( int numSteps, PhysicsUpdate physicsUpdate ) {
        this.numSteps = numSteps;
        this.physicsUpdate = physicsUpdate;
    }

    public ModelState getNextState( ModelState state, double dt ) {
        for ( int i = 0; i < numSteps; i++ ) {
            state = physicsUpdate.getNextState( state, dt / numSteps );
        }
        return state;
    }
}
