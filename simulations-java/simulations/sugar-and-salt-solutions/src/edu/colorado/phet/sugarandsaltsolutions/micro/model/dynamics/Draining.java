// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.DrainData;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static java.util.Collections.sort;

/**
 * Moves the particles toward the drain when the user drains the water out, constraining the number of formula units for each solute type to be integral
 *
 * @author Sam Reid
 */
public class Draining {

    private MicroModel model;

    public Draining( MicroModel model ) {
        this.model = model;
    }

    //TODO: implement me
    public void updateFormulaUnitsFlowingToDrain( double dt ) {
        for ( Formula formula : model.getKit().getFormulae() ) {

        }
    }

    //Move the particles toward the drain and try to keep a constant concentration
    //all particles should exit when fluid is gone, move nearby particles
    //For simplicity and regularity (to minimize deviation from the target concentration level), plan to have particles exit at regular intervals
    public void updateParticlesFlowingToDrain( DrainData drainData, double dt ) {

        ItemList<Particle> particles = model.freeParticles.filter( drainData.type );

        //Pre-compute the drain faucet input point since it is used throughout this method, and many times in the sort method
        final ImmutableVector2D drain = model.getDrainFaucetMetrics().getInputPoint();

        //Sort particles by distance and set their speeds so that they will leave at the proper rate
        sort( particles, new Comparator<Particle>() {
            public int compare( Particle o1, Particle o2 ) {
                return Double.compare( o1.getPosition().getDistance( drain ), o2.getPosition().getDistance( drain ) );
            }
        } );

        //flow rate in volume / time
        double currentDrainFlowRate_VolumePerSecond = model.outputFlowRate.get() * model.faucetFlowRate;

        //Determine the current concentration in particles per meter cubed
        double currentConcentration = model.freeParticles.count( drainData.type ) / model.solution.volume.get();

        //Determine the concentration at which we would consider it to be too erroneous, at half a particle over the target concentration
        //Half a particle is used so the solution will center on the target concentration (rather than upper or lower bounded)
        double errorConcentration = ( drainData.initialNumberSolutes + 0.5 ) / drainData.initialVolume;

        //Determine the concentration in the next time step, and subsequently how much it is changing over time and how long until the next error occurs
        double nextConcentration = model.freeParticles.count( drainData.type ) / ( model.solution.volume.get() - currentDrainFlowRate_VolumePerSecond * dt );
        double deltaConcentration = ( nextConcentration - currentConcentration );
        double numberDeltasToError = ( errorConcentration - currentConcentration ) / deltaConcentration;

        //Sanity check on the number of deltas to reach a problem, if this is negative it could indicate some unexpected change in initial concentration
        //In any case, shouldn't propagate toward the drain with a negative delta, because that causes a negative speed and motion away from the drain
        if ( numberDeltasToError < 0 ) {
            System.out.println( getClass().getName() + ": numberDeltasToError = " + numberDeltasToError + ", recomputing initial concentration and postponing drain" );
            model.checkStartDrain( drainData );
            return;
        }

        //Assuming a constant rate of drain flow, compute how long until we would be in the previously determined error scenario
        //We will speed up the nearest particle so that it flows out in this time
        double timeToError = numberDeltasToError * dt;

        //The closest particle is the most important, since its exit will be the next action that causes concentration to drop
        //Time it so the particle gets there exactly at the right time to make the concentration value exact again.
        double mainParticleSpeed = 0;
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = particles.get( i );

            //Compute the target time, distance, speed and velocity, and apply to the particle so they will reach the drain at evenly spaced temporal intervals
            double distanceToTarget = particle.getPosition().getDistance( drain );
            double speed = distanceToTarget / timeToError;

            //Store the primary speed that the leaving particle is moving at
            if ( i == 0 ) {
                mainParticleSpeed = speed;
            }
            else {

                //For secondary particles, move with a speed close to that of the closest particle, but slower if further away
                //This rule seems to work well, I also experimented with rules like v=alpha / d but it exhibited undesirable quirky behavior
                speed = mainParticleSpeed / ( i + 1 );
            }
            ImmutableVector2D velocity = new ImmutableVector2D( particle.getPosition(), drain ).getInstanceOfMagnitude( speed );
            final boolean randomWalk = i != 0;
            particle.setUpdateStrategy( new FlowToDrainStrategy( model, velocity, randomWalk ) );

            if ( MicroModel.debugDraining ) {
                System.out.println( "i = " + 0 + ", target time = " + model.getTime() + ", velocity = " + speed + " nominal velocity = " + UpdateStrategy.FREE_PARTICLE_SPEED );
//                System.out.println( "flowing to drain = " + drain.getX() + ", velocity = " + velocity.getX() + ", speed = " + speed );
            }
        }
    }
}