// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Update the particles that flowed out the drain
 *
 * @author Sam Reid
 */
public class FlowOutOfDrainStrategy extends UpdateStrategy {
    public FlowOutOfDrainStrategy( MicroModel model ) {
        super( model );
    }

    public void stepInTime( Particle particle, double dt ) {

        //Accelerate the particle due to gravity and perform an euler integration step
        particle.stepInTime( model.getExternalForce( false ).times( 1.0 / UpdateStrategy.PARTICLE_MASS ), dt );

        //If the particle has fallen too far (say 3 beaker heights), remove it from the model completely
        if ( particle.getPosition().getY() < -3 * model.beaker.getHeight() ) {

            //Remove the graphics from the model
            //TODO: one method that handles both leaves and non-leaves?
            if ( particle instanceof Compound<?> ) {
                model.removeComponents( (Compound<?>) particle );
            }
            else if ( particle instanceof SphericalParticle ) {
                model.sphericalParticles.remove( (SphericalParticle) particle );
            }
            else {
                new RuntimeException( "No match found" ).printStackTrace();
            }

            //Remove the reference from the list of drained particles
            model.drainedParticles.remove( particle );
        }
    }
}