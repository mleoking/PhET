// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * The precipitate that forms on the bottom of the beaker.
 * Manages the creation and deletion of precipitate particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Precipitate {

    public interface ParticlesChangeListener {
        // A particle has been added to the precipitate.
        public void particleAdded( PrecipitateParticle particle );

        // A particle has been removed from the precipitate.
        public void particleRemoved( PrecipitateParticle particle );
    }

    private final ConcentrationSolution solution;
    private final Beaker beaker;
    private final ArrayList<PrecipitateParticle> particles;
    private final ArrayList<ParticlesChangeListener> listeners;

    public Precipitate( ConcentrationSolution solution, Beaker beaker ) {

        this.solution = solution;
        this.beaker = beaker;
        this.particles = new ArrayList<PrecipitateParticle>();
        this.listeners = new ArrayList<ParticlesChangeListener>();

        // when the saturation changes, update the number of precipitate particles
        solution.precipitateAmount.addObserver( new SimpleObserver() {
            public void update() {
                updateParticles();
            }
        } );

        // when the solute changes, remove all particles and create new particles for the solute
        solution.solute.addObserver( new SimpleObserver() {
            public void update() {
                removeAllParticles();
                updateParticles();
            }
        } );
    }

    public void addParticlesChangeListener( ParticlesChangeListener listener ) {
        listeners.add( listener );
    }

    // Adds/removes particles to match the model
    private void updateParticles() {
        int numberOfParticles = solution.getNumberOfPrecipitateParticles();
        if ( numberOfParticles == 0 ) {
            removeAllParticles();
        }
        else if ( numberOfParticles > particles.size() ) {
            // add particles
            while ( numberOfParticles > particles.size() ) {
                addParticle( new PrecipitateParticle( solution.solute.get(), getRandomOffset(), getRandomOrientation() ) );
            }
        }
        else {
            // remove particles
            while ( numberOfParticles < particles.size() ) {
                removeParticle( particles.get( particles.size() - 1 ) );
            }
        }
    }

    private void addParticle( PrecipitateParticle particle ) {
        particles.add( particle );
        fireParticleAdded( particle );
    }

    private void removeParticle( PrecipitateParticle particle ) {
        particles.remove( particle );
        fireParticleRemoved( particle );
    }

    private void removeAllParticles() {
        for ( PrecipitateParticle particle : new ArrayList<PrecipitateParticle>( particles ) ) {
            removeParticle( particle );
        }
    }

    private void fireParticleAdded( PrecipitateParticle particle ) {
        for ( ParticlesChangeListener listener : new ArrayList<ParticlesChangeListener>( listeners ) ) {
            listener.particleAdded( particle );
        }
    }

    private void fireParticleRemoved( PrecipitateParticle particle ) {
        for ( ParticlesChangeListener listener : new ArrayList<ParticlesChangeListener>( listeners ) ) {
            listener.particleRemoved( particle );
        }
    }

    // Gets a random location, relative to the coordinate frame of the beaker.
    private Vector2D getRandomOffset() {
        final double particleSize = solution.solute.get().particleSize;
        // x offset
        double xMargin = particleSize;
        double width = beaker.size.getWidth() - particleSize - ( 2 * xMargin );
        double x = xMargin + ( Math.random() * width ) - ( beaker.size.getWidth() / 2 );
        // y offset
        double yMargin = particleSize;
        double y = -yMargin;
        // offset
        return new Vector2D( x, y );
    }

    // Gets a random orientation, in radians.
    private double getRandomOrientation() {
        return Math.random() * 2 * Math.PI;
    }
}
