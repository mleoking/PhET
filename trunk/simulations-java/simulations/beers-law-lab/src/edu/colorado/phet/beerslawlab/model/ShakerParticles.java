// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.util.ArrayList;

/**
 * Manages the creation and deletion of shaker particles in the model,
 * and their contribution to the amount of solute in solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShakerParticles {

    public interface ParticlesChangeListener {

        // A particle has been added to the model.
        public void particleAdded( ShakerParticle particle );

        // A particle has been removed from the model.
        public void particleRemoved( ShakerParticle particle );
    }

    private final Shaker shaker;
    private final Solution solution;
    private final ArrayList<ParticlesChangeListener> listeners;
    private final ArrayList<ShakerParticle> particles;

    public ShakerParticles( Shaker shaker, Solution solution ) {
        this.shaker = shaker;
        this.solution = solution;
        this.listeners = new ArrayList<ParticlesChangeListener>();
        this.particles = new ArrayList<ShakerParticle>();
    }

    public void stepInTime( double deltaSeconds ) {

        // propagate existing particles
        for ( ShakerParticle particle : new ArrayList<ShakerParticle>( particles ) ) {

            particle.stepInTime( deltaSeconds );

            //TODO this entire block is bogus, just to get something working
            // remove?
            if ( particle.getLocation().getY() > 500 ) {
                removeParticle( particle );
                solution.soluteAmount.set( solution.soluteAmount.get() + .01 );
            }
        }

        // create new particles
        if ( shaker.getDispensingRate() > 0 ) {
            int numberOfParticles = 1; //TODO number of particles created is a function of shaker.getDispensingRate
            for ( int i = 0; i < numberOfParticles; i++ ) {
                addParticle( new ShakerParticle( solution.solute.get(), shaker.location.get(), 0 ) );
            }
        }
    }

    public void addParticlesChangeListener( ParticlesChangeListener listener ) {
        listeners.add( listener );
    }

    private void addParticle( ShakerParticle particle ) {
        particles.add( particle );
        fireParticleAdded( particle );
    }

    private void removeParticle( ShakerParticle particle ) {
        particles.remove( particle );
        fireParticleRemoved( particle );
    }

    public void removeAllParticles() {
        for ( ShakerParticle particle : new ArrayList<ShakerParticle>( particles ) ) {
            removeParticle( particle );
        }
    }

    private void fireParticleAdded( ShakerParticle particle ) {
        for ( ParticlesChangeListener listener : new ArrayList<ParticlesChangeListener>( listeners ) ) {
            listener.particleAdded( particle );
        }
    }

    private void fireParticleRemoved( ShakerParticle particle ) {
        for ( ParticlesChangeListener listener : new ArrayList<ParticlesChangeListener>( listeners ) ) {
            listener.particleRemoved( particle );
        }
    }
}
