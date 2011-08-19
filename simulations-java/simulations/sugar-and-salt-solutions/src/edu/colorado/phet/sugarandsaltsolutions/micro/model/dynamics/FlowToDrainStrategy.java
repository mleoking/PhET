// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * This strategy moves particles toward the drain at the indicated velocity.  When they reach the drain, they flow out through the drain faucet.
 *
 * @author Sam Reid
 */
public class FlowToDrainStrategy extends UpdateStrategy {

    //The velocity at which the particle should flow toward the drain
    private ImmutableVector2D velocity;

    //Flag to indicate whether the particle should also use some randomness as it moves toward the drain.  Particles that are closest to the drain should move directly toward the drain
    //So they can reach it in the desired amount of time to keep the concentration as steady as possible
    private boolean randomWalk;

    public FlowToDrainStrategy( MicroModel model, ImmutableVector2D velocity, boolean randomWalk ) {
        super( model );
        this.velocity = velocity;
        this.randomWalk = randomWalk;
    }

    @Override public void stepInTime( Particle particle, double dt ) {

        //If the user released the drain slider, then switch back to purely random motion
        if ( model.outputFlowRate.get() == 0 ) {
            particle.setUpdateStrategy( new FreeParticleStrategy( model ) );
        }

        //Otherwise, move the particle with the pre-specified velocity and possible some random walk mixed in
        else {

            //If not closest to the drain, follow some random walk motion to look more natural, but still move toward the drain a little bit
            //If closest to the drain, move directly toward the drain so it can reach it in the desired amount of time to keep the concentration as steady as possible
            if ( randomWalk ) {
                double initVelocity = particle.velocity.get().getMagnitude();

                //Mix in more of the original velocity to keep more of the random walk component
                ImmutableVector2D newVelocity = particle.velocity.get().times( 3 ).plus( velocity ).getInstanceOfMagnitude( initVelocity );
                particle.velocity.set( newVelocity );
                new FreeParticleStrategy( model ).randomWalk( particle, dt );
            }
            else {
                particle.velocity.set( velocity );
                particle.stepInTime( ZERO, dt );
            }

            if ( !model.solution.shape.get().getBounds2D().contains( particle.getShape().getBounds2D() ) ) {
                model.preventFromLeavingBeaker( particle );
            }

            //If the particle reached the drain, change its update strategy and move it into the list of model drained particles
            double dist = particle.getPosition().getDistance( model.getDrainFaucetMetrics().getInputPoint() );
            if ( dist <= velocity.getMagnitude() * dt || dist <= 1E-11 ) {
                particle.setUpdateStrategy( new FlowOutOfDrainStrategy( model ) );

                //Move it from the list of free particles to the list of drained particles so it won't be counted for drain scheduling or for concentration
                model.freeParticles.remove( particle );
                model.drainedParticles.add( particle );

                //Okay to reschedule now since one particle just left, so there will be no phase problem
                particle.setPosition( model.getDrainFaucetMetrics().outputPoint );
                particle.velocity.set( new ImmutableVector2D( 0, -FREE_PARTICLE_SPEED / 2 ) );
            }
        }
    }
}