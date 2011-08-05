// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Move the particles about with a random walk, but making sure they remain within the solution (if they started within it)
 *
 * @author Sam Reid
 */
public class FreeParticleStrategy extends UpdateStrategy {

    //Randomness for random walks
    private final Random random = new Random();

    public FreeParticleStrategy( MicroModel model ) {
        super( model );
    }

    //Updates the particle in time, moving it randomly or changing its motion strategy based on user input
    public void stepInTime( Particle particle, double dt ) {

        //Switch strategies if necessary
        //Note, this check prevents random motion during draining since the strategy is switched before random walk can take place
        if ( model.outputFlowRate.get() > 0 ) {
            particle.setUpdateStrategy( new FlowToDrainStrategy( model, new ImmutableVector2D(), false ) );
            particle.stepInTime( dt );
        }
        else {
            randomWalk( particle, dt );
        }
    }

    //Apply a random walk update to the particle.  This is also reused in FlowToDrainStrategy so that the particle will have somewhat random motion as it progresses toward the drain
    public void randomWalk( Particle particle, double dt ) {
        boolean initiallyUnderwater = solution.shape.get().contains( particle.getShape().getBounds2D() );

        //If the crystal has ever gone underwater, set a flag so that it can be kept from leaving the top of the water
        if ( solution.shape.get().contains( particle.getShape().getBounds2D() ) ) {
            particle.setSubmerged();
        }

        ImmutableVector2D initialPosition = particle.getPosition();
        ImmutableVector2D initialVelocity = particle.velocity.get();

        //If the particle is underwater and there is any water, move the particle about at the free particle speed
        if ( particle.hasSubmerged() && waterVolume.get() > 0 ) {

            //If the particle velocity was set to zero (from a zero water volume, restore it to non-zero so it can be scaled
            if ( particle.velocity.get().getMagnitude() == 0 ) {
                particle.velocity.set( parseAngleAndMagnitude( 1, randomAngle() ) );
            }
            particle.velocity.set( particle.velocity.get().getInstanceOfMagnitude( FREE_PARTICLE_SPEED ) );
        }

        //If the particle was stopped by the water completely evaporating, start it moving again
        //Must be done before particle.stepInTime so that the particle doesn't pick up a small velocity in that method, since this assumes particle velocity of zero implies evaporated to the bottom
        if ( particle.velocity.get().getMagnitude() == 0 ) {
            model.collideWithWater( particle );
        }

        //Accelerate the particle due to gravity and perform an euler integration step
        particle.stepInTime( model.getExternalForce( model.isAnyPartUnderwater( particle ) ).times( 1.0 / PARTICLE_MASS ), dt );

        boolean underwater = solution.shape.get().contains( particle.getShape().getBounds2D() );

        //If the particle entered the water on this step, slow it down to simulate hitting the water
        if ( !initiallyUnderwater && underwater && particle.getPosition().getY() > model.beaker.getHeightForVolume( waterVolume.get() ) / 2 ) {
            model.collideWithWater( particle );
        }

        //Random Walk, implementation taken from edu.colorado.phet.solublesalts.model.RandomWalk
        if ( underwater ) {
            double theta = random.nextDouble() * Math.toRadians( 30.0 ) * MathUtil.nextRandomSign();
            particle.velocity.set( particle.velocity.get().getRotatedInstance( theta ).times( 2 ) );
        }

        //Prevent the particles from leaving the solution, but only if they started in the solution
        //And randomize the velocity so it will hopefully move away from the wall soon, and won't get stuck in the corner
        if ( initiallyUnderwater && !underwater ) {
            particle.setPosition( initialPosition );
            particle.velocity.set( parseAngleAndMagnitude( initialVelocity.getMagnitude(), randomAngle() ) );
        }

        //If the particle is on the floor of the beaker, and only partly submerged due to a very low water level, then make sure its velocity gets randomized too
        //Without this fix, it would just move constantly until hitting a wall and stopping
        boolean shapeIntersectsWater = particle.getShape().getBounds2D().intersects( model.solution.shape.get().getBounds2D() );
        boolean partiallySubmerged = particle.getShape().getBounds2D().getMinY() < model.solution.shape.get().getBounds2D().getMaxY();
        if ( !initiallyUnderwater && !underwater && shapeIntersectsWater && partiallySubmerged ) {
            particle.velocity.set( parseAngleAndMagnitude( initialVelocity.getMagnitude(), randomAngle() ) );
        }

        //Stop the particle completely if there is no water to move within
        if ( waterVolume.get() <= 0 ) {
            particle.velocity.set( new ImmutableVector2D( 0, 0 ) );
        }

        //Keep the particle within the beaker solution bounds
        model.preventFromLeavingBeaker( particle );
    }
}