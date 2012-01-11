// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * The precipitate that forms on the bottom of the beaker.
 * Manages the creation and deletion of precipitate particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Precipitate {

    public interface PrecipitateListener {
        // A particle has been added to the precipitate.
        public void particleAdded( PrecipitateParticle particle );

        // A particle has been removed from the precipitate.
        public void particleRemoved( PrecipitateParticle particle );
    }

    private final Solution solution;
    private final Beaker beaker;
    private final ArrayList<PrecipitateParticle> particles;
    private final ArrayList<PrecipitateListener> listeners;

    public Precipitate( Solution solution, Beaker beaker ) {

        this.solution = solution;
        this.beaker = beaker;
        this.particles = new ArrayList<PrecipitateParticle>();
        this.listeners = new ArrayList<PrecipitateListener>();

        // when the saturation changes, update the number of precipitate particles
        solution.addPrecipitateAmountObserver( new SimpleObserver() {
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

    public void addListener( PrecipitateListener listener ) {
        listeners.add( listener );
    }

    private void updateParticles() {
        int numberOfParticles = solution.getNumberOfPrecipitateParticles();
        if ( numberOfParticles == 0 ) {
            removeAllParticles();
        }
        else if ( numberOfParticles > particles.size() ) {
            // add particles
            while ( numberOfParticles > particles.size() ) {
                addParticle( new PrecipitateParticle( getRandomOffset(), getRandomOrientation(), solution.solute.get().precipitateColor, solution.solute.get().precipitateSize ) );
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
        for ( PrecipitateListener listener : new ArrayList<PrecipitateListener>( listeners ) ) {
            listener.particleAdded( particle );
        }
    }

    private void fireParticleRemoved( PrecipitateParticle particle ) {
        for ( PrecipitateListener listener : new ArrayList<PrecipitateListener>( listeners ) ) {
            listener.particleRemoved( particle );
        }
    }

    // Gets a random location, relative to the coordinate frame of the beaker.
    private Point2D getRandomOffset() {
        final double particleSize = solution.solute.get().precipitateSize;
        // x offset
        double xMargin = particleSize;
        double width = beaker.getWidth() - particleSize - ( 2 * xMargin );
        double x = xMargin + ( Math.random() * width ) - ( beaker.getWidth() / 2 );
        // y offset
        double yMargin = particleSize;
        double y = -yMargin;
        // offset
        return new Point2D.Double( x, y );
    }

    // Gets a random orientation, in radians.
    private double getRandomOrientation() {
        return Math.random() * 2 * Math.PI;
    }
}
