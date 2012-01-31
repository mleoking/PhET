// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Manages the lifetime of shaker particles, from creation when they exit the shaker,
 * to deletion when they are delivered to the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShakerParticles {

    // Units for speed and acceleration are not meaningful here, adjust these so that it looks good.
    private static final double INITIAL_SPEED = 100;
    private static final double GRAVITATIONAL_ACCELERATION_MAGNITUDE = 150;

    // These offsets determine where a salt particle originates, relative to the shaker's location.
    private static final int MAX_X_OFFSET = 20;
    private static final int MAX_Y_OFFSET = 5;

    public interface ParticlesChangeListener {

        // A particle has been added to the model.
        public void particleAdded( ShakerParticle particle );

        // A particle has been removed from the model.
        public void particleRemoved( ShakerParticle particle );
    }

    private final Shaker shaker;
    private final Solution solution;
    private final Beaker beaker;
    private final ArrayList<ParticlesChangeListener> listeners;
    private final ArrayList<ShakerParticle> particles;
    private final Random randomLocation = new Random();

    public ShakerParticles( Shaker shaker, Solution solution, Beaker beaker ) {

        this.shaker = shaker;
        this.solution = solution;
        this.beaker = beaker;
        this.listeners = new ArrayList<ParticlesChangeListener>();
        this.particles = new ArrayList<ShakerParticle>();

        // when the solute changes, remove all particles
        solution.solute.addObserver( new SimpleObserver() {
            public void update() {
                removeAllParticles();
            }
        } );
    }

    // Particle animation and delivery to the solution, called when the simulation clock ticks.
    public void stepInTime( double deltaSeconds ) {

        // propagate existing particles
        for ( ShakerParticle particle : new ArrayList<ShakerParticle>( particles ) ) {

            particle.stepInTime( deltaSeconds, beaker );

            // If the particle hits the solution surface or bottom of the beaker, delete it, and add a corresponding amount of solute to the solution.
            double percentFull = solution.volume.get() / beaker.getVolume();
            double solutionSurfaceY = beaker.getY() - ( percentFull * beaker.getHeight() ) - solution.solute.get().particleSize;
            if ( particle.getLocation().getY() > solutionSurfaceY ) {
                removeParticle( particle );
                solution.soluteAmount.set( solution.soluteAmount.get() + ( 1d / solution.solute.get().particlesPerMole ) );
            }
        }

        // create new particles
        if ( shaker.getDispensingRate() > 0 ) {
            int numberOfParticles = 1; //TODO number of particles created is a function of shaker.getDispensingRate
            //TODO shouldn't dispense more than the max amount of solute
            for ( int i = 0; i < numberOfParticles; i++ ) {
                addParticle( new ShakerParticle( solution.solute.get(), getRandomLocation(), getRandomOrientation(), getInitialVelocity(), getGravitationalAcceleration() ) );
            }
        }
    }

    // Computes an initial velocity for the particle.
    protected ImmutableVector2D getInitialVelocity() {
        return ImmutableVector2D.createPolar( INITIAL_SPEED, shaker.getOrientation() ); // in the direction the shaker is pointing
    }

    // Gravitational acceleration is in the downward direction.
    private ImmutableVector2D getGravitationalAcceleration() {
        return new ImmutableVector2D( 0, GRAVITATIONAL_ACCELERATION_MAGNITUDE );
    }

    private double getRandomOrientation() {
        return Math.random() * Math.PI;
    }

    private ImmutableVector2D getRandomLocation() {
        double xOffset = randomLocation.nextInt( MAX_X_OFFSET + MAX_X_OFFSET ) - MAX_X_OFFSET; // positive or negative
        double yOffset = randomLocation.nextInt( MAX_Y_OFFSET ); // positive only
        return new ImmutableVector2D( shaker.getX() + xOffset, shaker.getY() + yOffset );
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
