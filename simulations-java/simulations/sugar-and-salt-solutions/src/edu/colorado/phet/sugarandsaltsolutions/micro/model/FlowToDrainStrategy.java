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

                //Rescheduling all the time creates a zeno paradox situation
//                model.rescheduleDrainParticles();
            }
            if ( particle.getPosition().getDistance( model.getDrainFaucetMetrics().inputPoint ) <= velocity.getMagnitude() * dt ) {
                particle.setUpdateStrategy( new Motionless() );
                model.freeParticles.remove( particle );
                model.sphericalParticles.remove( (SphericalParticle) particle );

                //Okay to reschedule now since one particle just left, so there will be no phase problem
                model.rescheduleDrainParticles();
                //        //Handle particles that entered the drain by moving them to the drainedParticles list
//        for ( Particle particle : toDrain ) {
//            freeParticles.remove( particle );
//            particle.setPosition( getDrainFaucetMetrics().outputPoint );
//            particle.velocity.set( new ImmutableVector2D( 0, -UpdateStrategy.FREE_PARTICLE_SPEED / 2 ) );
//            drainedParticles.add( particle );
//        }
            }
        }
    }
}