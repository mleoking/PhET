// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * Moves the particles toward the drain when the user drains the water out, constraining the number of formula units for each solute type to be integral
 *
 * @author Sam Reid
 */
public class Draining {

    private MicroModel model;

    //The Draining algorithm keeps track of which formula unit each particle is assigned to so that a particle is not double counted
    //It has to be cleared in each iteration since groupings are reassigned at each sim step
    private ArrayList<Particle> usedParticles = new ArrayList<Particle>();

    public Draining( MicroModel model ) {
        this.model = model;
    }

    //Reset assignments to each group so they can be reassigned in each clock tick based on distance from the drain
    public void clearParticleGroupings() {
        usedParticles.clear();
    }

    public double getTimeToError( DrainData drainData, double dt ) {
        //flow rate in volume / time
        double currentDrainFlowRate_VolumePerSecond = model.outputFlowRate.get() * model.faucetFlowRate;

        // number of free formula units
        int freeFormulaUnitCount = model.countFreeFormulaUnits( drainData.formula );

        //Determine the current concentration in particles per meter cubed
        double currentConcentration = freeFormulaUnitCount / model.solution.volume.get();

        //Determine the concentration at which we would consider it to be too erroneous, at half a particle over the target concentration
        //Half a particle is used so the solution will center on the target concentration (rather than upper or lower bounded)
        double errorConcentration = ( drainData.initialNumberFormulaUnits + 0.5 ) / drainData.initialVolume;

        //Determine the concentration in the next time step, and subsequently how much it is changing over time and how long until the next error occurs
        double nextConcentration = freeFormulaUnitCount / ( model.solution.volume.get() - currentDrainFlowRate_VolumePerSecond * dt );
        double deltaConcentration = ( nextConcentration - currentConcentration );
        double numberDeltasToError = ( errorConcentration - currentConcentration ) / deltaConcentration;

        //Assuming a constant rate of drain flow, compute how long until we would be in the previously determined error scenario
        //We will speed up the nearest particle so that it flows out in this time
        return numberDeltasToError * dt;
    }

    //Move the particles toward the drain and try to keep a constant concentration
    //all particles should exit when fluid is gone, move nearby particles
    //For simplicity and regularity (to minimize deviation from the target concentration level), plan to have particles exit at regular intervals
    public void updateParticlesFlowingToDrain( DrainData drainData, double dt ) {

        //Nothing to do if no formula units for this formula
        if ( model.countFreeFormulaUnits( drainData.formula ) == 0 ) {
            return;
        }

        //Assuming a constant rate of drain flow, compute how long until we would be in the previously determined error scenario
        //We will speed up the nearest particle so that it flows out in this time
        double timeToError = getTimeToError( drainData, dt );

        //Sanity check on the number of deltas to reach a problem, if this is negative it could indicate some unexpected change in initial concentration
        //In any case, shouldn't propagate toward the drain with a negative delta, because that causes a negative speed and motion away from the drain
        if ( timeToError < 0 ) {
            System.out.println( getClass().getName() + ": timeToError = " + timeToError + ", recomputing initial concentration and postponing drain" );
            model.checkStartDrain( drainData );
            return;
        }

        //The closest particle is the most important, since its exit will be the next action that causes concentration to drop
        //Time it so the particle gets there exactly at the right time to make the concentration value exact again.
        System.out.println( drainData.formula );

        //Pre-compute the drain faucet input point since it is used throughout this method, and many times in the sort method
        final ImmutableVector2D drain = model.getDrainFaucetMetrics().getInputPoint();

        //Find the closest particles and move them toward the drain at a rate so they will reach at the same time
        ItemList<Particle> closestFormulaUnit = getParticlesToDrain( drainData.formula );
        for ( Particle particle : closestFormulaUnit ) {
            System.out.println( particle.getClass() + " #" + particle.hashCode() + " x: " + particle.getPosition().getX() );

            //Compute the target time, distance, speed and velocity, and apply to the particle so they will reach the drain at evenly spaced temporal intervals
            double distanceToTarget = particle.getPosition().getDistance( drain );

            // compute the speed/velocity to make this particle arrive at the drain at the same time as the other particles in the formula unit
            double speed = distanceToTarget / timeToError;
            ImmutableVector2D velocity = new ImmutableVector2D( particle.getPosition(), drain ).getInstanceOfMagnitude( speed );

            //Set the update strategy of the particle, it will be updated when the strategies are invoked in MicroModel
            particle.setUpdateStrategy( new FlowToDrainStrategy( model, velocity, false ) );

            if ( MicroModel.debugDraining ) {
                System.out.println( "i = " + 0 + ", target time = " + model.getTime() + ", velocity = " + speed + " nominal velocity = " + UpdateStrategy.FREE_PARTICLE_SPEED );
            }
        }

        if ( !closestFormulaUnit.isEmpty() ) {
            Particle particle = closestFormulaUnit.get( 0 );

            //If the particle reached the drain, change its update strategy and move it into the list of model drained particles
            //Account for the particle width when checking whether it is close enough since wide particles like sucrose won't be able to match their centroid with the
            //center of the drain because they are prevented from leaving the bounds of the water
            double dist = particle.getPosition().getDistance( model.getDrainFaucetMetrics().getInputPoint() );
            if ( dist <= particle.velocity.get().getMagnitude() * dt + particle.getShape().getBounds2D().getWidth() / 2 ) {

                // drain out all of the particles within the formula unit
                for ( Particle unitParticle : closestFormulaUnit ) {
                    unitParticle.setUpdateStrategy( new FlowOutOfDrainStrategy( model ) );

                    //Move it from the list of free particles to the list of drained particles so it won't be counted for drain scheduling or for concentration
                    model.freeParticles.remove( unitParticle );
                    model.drainedParticles.add( unitParticle );

                    //Okay to reschedule now since one particle just left, so there will be no phase problem
                    unitParticle.setPosition( model.getDrainFaucetMetrics().outputPoint );
                    unitParticle.velocity.set( new ImmutableVector2D( 0, -UpdateStrategy.FREE_PARTICLE_SPEED / 2 ) );
                }
            }
        }
    }

    //Determine which particles to drain, accounting for the groups to which particles have been assigned
    public ItemList<Particle> getParticlesToDrain( Formula formula ) {
        final ArrayList<Particle> list = new ArrayList<Particle>();
        for ( Class<? extends Particle> type : formula.getFormulaUnit() ) {

            //Find the closest particle that hasn't already been assigned to another formula
            final Particle closestUnused = getClosestUnused( type );

            //If no particle as requested then bail out, roll back changes to the used particle list and return an empty list to signify nothing to drain
            if ( closestUnused == null ) {
                break;
            }
            else {
                usedParticles.add( closestUnused );
                list.add( closestUnused );
            }
        }

        //If we couldn't find a particle for every element in the formula unit, then roll back changes to the used particle list and signify nothing to drain
        if ( list.size() != formula.getFormulaUnit().size() ) {
            for ( Particle particle : list ) {
                usedParticles.remove( particle );
            }
            return new ItemList<Particle>();
        }

        return new ItemList<Particle>( list );
    }

    //Find the particle that is closest to the drain that hasn't already been assigned to another formula group
    private Particle getClosestUnused( Class<? extends Particle> type ) {
        ArrayList<Particle> list = model.freeParticles.filter( type ).filter( new Function1<Particle, Boolean>() {
            public Boolean apply( Particle particle ) {
                return !usedParticles.contains( particle );
            }
        } ).toList();
        final ImmutableVector2D drain = model.getDrainFaucetMetrics().getInputPoint();
        Collections.sort( list, new Comparator<Particle>() {
            public int compare( Particle o1, Particle o2 ) {
                return Double.compare( o1.getPosition().getDistance( drain ), o2.getPosition().getDistance( drain ) );
            }
        } );
        if ( list.size() > 0 ) {
            return list.get( 0 );
        }
        else {
            return null;
        }
    }
}