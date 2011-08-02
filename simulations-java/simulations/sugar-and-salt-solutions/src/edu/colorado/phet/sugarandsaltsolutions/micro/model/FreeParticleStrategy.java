// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;
import static java.lang.Math.PI;

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

    public void stepInTime( Particle particle, double dt ) {

        //Switch strategies if necessary
        if ( model.outputFlowRate.get() > 0 ) {
            particle.setUpdateStrategy( new FlowToDrainStrategy( model, new ImmutableVector2D() ) );
            particle.stepInTime( dt );
            return;
        }
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
            particle.velocity.set( particle.velocity.get().getInstanceOfMagnitude( UpdateStrategy.FREE_PARTICLE_SPEED ) );
        }

        //If the particle was stopped by the water completely evaporating, start it moving again
        //Must be done before particle.stepInTime so that the particle doesn't pick up a small velocity in that method, since this assumes particle velocity of zero implies evaporated to the bottom
        if ( particle.velocity.get().getMagnitude() == 0 ) {
            model.collideWithWater( particle );
        }

        //Accelerate the particle due to gravity and perform an euler integration step
        particle.stepInTime( model.getExternalForce( model.isAnyPartUnderwater( particle ) ).times( 1.0 / UpdateStrategy.PARTICLE_MASS ), dt );

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
        if ( initiallyUnderwater && !underwater ) {
            ImmutableVector2D delta = particle.getPosition().minus( initialPosition );
            particle.setPosition( initialPosition );

            //If the particle hit the wall, point its velocity in the opposite direction so it will move away from the wall
            particle.velocity.set( parseAngleAndMagnitude( initialVelocity.getMagnitude(), delta.getAngle() + PI ) );
        }

        //Stop the particle completely if there is no water to move within
        if ( waterVolume.get() <= 0 ) {
            particle.velocity.set( new ImmutableVector2D( 0, 0 ) );
        }

        //Keep the particle within the beaker solution bounds
        model.preventFromLeavingBeaker( particle );
    }
}