// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * This strategy moves particles toward the drain at the indicated velocity.  When they reach the drain, they flow out through the drain faucet.
 *
 * @author Sam Reid
 */
public class FlowToDrainStrategy extends UpdateStrategy {
    private ImmutableVector2D velocity;

    public FlowToDrainStrategy( MicroModel model, ImmutableVector2D velocity ) {
        super( model );
        this.velocity = velocity;
    }

    @Override public void stepInTime( Particle particle, double dt ) {
        if ( model.outputFlowRate.get() == 0 ) {
            particle.setUpdateStrategy( new FreeParticleStrategy( model ) );
        }
        else {

            //Move the particle with the pre-specified velocity
            particle.velocity.set( velocity );
            particle.stepInTime( ImmutableVector2D.ZERO, dt );

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
                particle.velocity.set( new ImmutableVector2D( 0, -UpdateStrategy.FREE_PARTICLE_SPEED / 2 ) );
            }
        }
    }
}