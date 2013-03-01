// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.quantum.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * An object that manages the lifetime of an AtomicEnergyState.
 * <p/>
 * When it is created, it sets a time of death for an atom's current state, and when
 * that time comes, causes a photon to be emitted and the atom to change to the next
 * appropriate energy state.
 */
class StateLifetimeManager implements ModelElement {
    private Atom atom;
    private boolean emitOnStateChange;
    private double lifeTime;
    private double deathTime;
    private AtomicState state;
    private QuantumModel model;
    private EmissionDirectionStrategy emissionDirectionStrategy =
            new SometimesHorizontalButMostlyRandomEmissionDirectionStrategy( 0.10 );

    /**
     * @param atom              The atom whose state's lifetime we are to manage
     * @param emitOnStateChange Does the atom emit a photon when it changes state?
     * @param model             The model
     */
    public StateLifetimeManager( Atom atom, boolean emitOnStateChange, QuantumModel model ) {
        this.atom = atom;
        this.emitOnStateChange = emitOnStateChange;
        this.model = model;
        model.addModelElement( this );

        double temp = 0;
        while ( temp == 0 ) {
            temp = Math.random();
        }
        state = atom.getCurrState();

        // Get the lifetime for this state
        if ( atom.isStateLifetimeFixed() ) {
            // This line gives a fixed death time
            deathTime = state.getMeanLifeTime();
        }
        else {
            // Assign a deathtime based on an exponential distribution
            // The square root pushes the distribution toward 1.
            deathTime = Math.pow( -Math.log( temp ), 0.5 ) * state.getMeanLifeTime();
        }

        // Initialize the field that tracks the state's lifetime
        lifeTime = 0;
    }

    /**
     * Changes the state of the associated atom if the state's lifetime has been exceeded.
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        lifeTime += dt;
        if ( lifeTime >= deathTime ) {

            AtomicState nextState = atom.getEnergyStateAfterEmission();
            if ( emitOnStateChange ) {
                double speed = Photon.DEFAULT_SPEED * model.getPhotonSpeedScale();
                double theta = emissionDirectionStrategy.getEmissionDirection();
                double x = speed * Math.cos( theta );
                double y = speed * Math.sin( theta );

                if ( nextState != atom.getCurrState() ) {
                    Photon emittedPhoton = new Photon( state.determineEmittedPhotonWavelength( nextState ),
                                                       new Point2D.Double( atom.getPosition().getX(), atom.getPosition().getY() ),
                                                       new MutableVector2D( x, y ) );

                    // Place the replacement photon beyond the atom, so it doesn't collide again
                    // right away
                    MutableVector2D vHat = new MutableVector2D( emittedPhoton.getVelocity() ).normalize();
                    MutableVector2D position = new MutableVector2D( atom.getPosition() );
                    position.add( vHat.scale( atom.getRadius() + 10 ) );
                    emittedPhoton.setPosition( position.getX(), position.getY() );
                    atom.emitPhoton( emittedPhoton );
                }
            }

            // Change state
            atom.setCurrState( nextState );

            // Remove us from the model
            kill();
        }
    }

    /**
     *
     */
    public void kill() {
        model.removeModelElement( this );
    }

    //----------------------------------------------------------------
    // Strategies for determining the direction that photons will be emitted
    //----------------------------------------------------------------

    public static interface EmissionDirectionStrategy {
        /**
         * The direction the photon is supposed to go, in radians
         *
         * @return The direction the photon is supposed to go
         */
        double getEmissionDirection();
    }

    /**
     * Direction is totaly random
     */
    public static class RandomEmissionDirectionStrategy implements EmissionDirectionStrategy {
        private static Random random = new Random( System.currentTimeMillis() );

        public double getEmissionDirection() {
            return random.nextDouble() * Math.PI * 2;
        }
    }

    /**
     * A specified percentage of the time the direction will be horizontal, radomized left and
     * right. The rest of the time it will be totally random
     */
    public static class SometimesHorizontalButMostlyRandomEmissionDirectionStrategy
            extends RandomEmissionDirectionStrategy {
        private static Random random = new Random( System.currentTimeMillis() );
        private double horizontalLikelihood = 0.4;

        public SometimesHorizontalButMostlyRandomEmissionDirectionStrategy( double horizontalLikelihood ) {
            this.horizontalLikelihood = horizontalLikelihood;
        }

        public double getEmissionDirection() {
            if ( random.nextDouble() <= horizontalLikelihood ) {
                return Math.PI * ( random.nextBoolean() ? 1 : 0 );
            }
            else {
                return super.getEmissionDirection();
            }
        }
    }

}