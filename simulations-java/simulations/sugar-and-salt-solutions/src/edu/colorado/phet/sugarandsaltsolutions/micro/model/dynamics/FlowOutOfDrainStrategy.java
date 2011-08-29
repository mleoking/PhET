// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

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
        particle.stepInTime( model.getExternalForce( false ).times( 1.0 / PARTICLE_MASS ), dt );

        //If the particle has fallen too far (say 3 beaker heights), remove it from the model completely
        if ( particle.getPosition().getY() < -3 * model.beaker.getHeight() ) {

            //Remove the graphics from the model
            if ( particle instanceof Compound<?> ) {
                model.removeComponents( (Compound<?>) particle );
            }
            else if ( particle instanceof SphericalParticle ) {
                model.sphericalParticles.remove( particle );
            }
            else {
                new RuntimeException( "No match found" ).printStackTrace();
            }

            //Remove the reference from the list of drained particles
            model.drainedParticles.remove( particle );
        }
    }
}