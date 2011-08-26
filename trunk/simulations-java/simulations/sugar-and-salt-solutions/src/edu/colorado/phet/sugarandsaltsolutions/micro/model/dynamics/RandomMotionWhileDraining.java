// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;


import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.UpdateStrategy.FREE_PARTICLE_SPEED;

/**
 * Move all particles toward the drain with a random walk, with nearer particles moving faster
 *
 * @author Sam Reid
 */
public class RandomMotionWhileDraining {
    private final MicroModel model;

    public RandomMotionWhileDraining( MicroModel model ) {
        this.model = model;
    }

    public void apply() {
        final ImmutableVector2D drain = model.getDrainFaucetMetrics().getInputPoint();
        for ( Particle particle : model.freeParticles ) {

            //Get the velocity for the particle
            ImmutableVector2D velocity = new ImmutableVector2D( particle.getPosition(), drain ).getInstanceOfMagnitude( FREE_PARTICLE_SPEED ).times( getRelativeSpeed( drain, particle ) );

            particle.setUpdateStrategy( new FlowToDrainStrategy( model, velocity, true ) );
        }
    }

    //Gets the relative speed at which a particle should move toward the drain.  This is a function that moves nearby particles closer to the drain faster
    private double getRelativeSpeed( ImmutableVector2D drain, Particle particle ) {

        //Only use this heuristic when further than 25% of beaker width away from the drain, otherwise particles close to the drain move too fast and end up waiting at the drain
        double numberBeakerWidthsToDrain = Math.max( 0.25, particle.getPosition().getDistance( drain ) / model.beaker.getWidth() );

        System.out.println( "model.outputFlowRate.get() = " + model.outputFlowRate.get() );
        return 1 / numberBeakerWidthsToDrain / numberBeakerWidthsToDrain * model.outputFlowRate.get() / 2;
    }
}