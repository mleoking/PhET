// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;


import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;

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
        final Vector2D drain = model.getDrainFaucetMetrics().getInputPoint();
        for ( Particle particle : model.freeParticles ) {

            //Get the velocity for the particle
            Vector2D velocity = new Vector2D( particle.getPosition(), drain ).getInstanceOfMagnitude( FREE_PARTICLE_SPEED ).times( getRelativeSpeed( drain, particle ) );

            particle.setUpdateStrategy( new FlowToDrainStrategy( model, velocity, true ) );
        }
    }

    //Gets the relative speed at which a particle should move toward the drain.  This is a function that moves nearby particles closer to the drain faster
    private double getRelativeSpeed( Vector2D drain, Particle particle ) {

        //Only use this heuristic when further than 25% of beaker width away from the drain, otherwise particles close to the drain move too fast and end up waiting at the drain
        double numberBeakerWidthsToDrain = Math.max( 0.25, particle.getPosition().getDistance( drain ) / model.beaker.getWidth() );

        return 1 / numberBeakerWidthsToDrain / numberBeakerWidthsToDrain * model.outputFlowRate.get() / 2;
    }
}