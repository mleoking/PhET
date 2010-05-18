/* Copyright 2003-2004, University of Colorado */

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
import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.quantum.QuantumConfig;

/**
 * A representation of the energy state of an atom
 */
public class AtomicState {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    static public final double minWavelength = edu.colorado.phet.common.quantum.model.Photon.BLUE - 20;
    static public final double maxWavelength = edu.colorado.phet.common.quantum.model.Photon.GRAY;
    static public final double minEnergy = PhysicsUtil.wavelengthToEnergy( maxWavelength );
    static public final double maxEnergy = PhysicsUtil.wavelengthToEnergy( minWavelength );
    static protected double STIMULATION_LIKELIHOOD = QuantumConfig.STIMULATION_LIKELIHOOD;
    static protected final double wavelengthTolerance = 10;

    static public void setStimulationLikelihood( double p ) {
        STIMULATION_LIKELIHOOD = p;
    }

    public static double getStimulationLikelihood() {
        return STIMULATION_LIKELIHOOD;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private double energyLevel;
    private double meanLifetime = Double.POSITIVE_INFINITY;
    private AtomicState nextHigherState;
    private AtomicState nextLowerState;

    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------

    public AtomicState() {
    }

    public AtomicState( AtomicState stateToClone ) {
        this.energyLevel = stateToClone.getEnergyLevel();
        this.meanLifetime = stateToClone.getMeanLifeTime();
        this.nextHigherState = stateToClone.getNextHigherEnergyState();
        this.nextLowerState = stateToClone.getNextLowerEnergyState();
    }

    /**
     *
     */
    public void enterState( Atom atom ) {
        // noop
    }

    public void leaveState( Atom atom ) {
        // noop
    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Returns the lifetime of the state. This is based on the energy level.
     * The higher the energy, the shorter the lifetime.
     *
     * @return mean life time
     */
    public double getMeanLifeTime() {
        return meanLifetime;
    }

    /**
     * @param lifetime
     */
    public void setMeanLifetime( double lifetime ) {
        this.meanLifetime = lifetime;
        listenerProxy.meanLifetimechanged( new Event( this ) );
    }

    /**
     * @param energyLevel
     */
    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        listenerProxy.energyLevelChanged( new Event( this ) );
    }

    public double getWavelength() {
        return PhysicsUtil.energyToWavelength( energyLevel );
    }

    /**
     * Determines the wavelength of a photon that would be emitted if the atom dropped to a
     * specified state
     *
     * @param nextState
     * @return wavelength of emitted photon
     */
    public double determineEmittedPhotonWavelength( AtomicState nextState ) {
        double energy1 = this.getEnergyLevel();
        double energy2 = nextState.getEnergyLevel();
        double emittedWavelength = Math.min( PhysicsUtil.energyToWavelength( energy1 - energy2 ),
                                             AtomicState.maxWavelength );
        return emittedWavelength;
    }

    /**
     * Returns the wavelength of a photon that would be emitted if the atom dropped to the next
     * lower energy state
     *
     * @return wavelength of emitted photon
     */
    public double determineEmittedPhotonWavelength() {
        double energy1 = PhysicsUtil.wavelengthToEnergy( this.getWavelength() );
        double energy2 = PhysicsUtil.wavelengthToEnergy( this.getNextLowerEnergyState().getWavelength() );
        double emittedWavelength = Math.min( PhysicsUtil.energyToWavelength( energy1 - energy2 ),
                                             AtomicState.maxWavelength );
        return emittedWavelength;
    }

    /**
     * Tells if a photon will be emitted from this state if the atom is struck by a specified photon.
     * This is true if the energy of the specified photon is equal, within a tolerance, of the difference
     * in energy between this state and the next lowest energy state.
     *
     * @param photon
     * @return true if the photon makes the atom go to a higher state
     */
    protected boolean isStimulatedBy( Photon photon, Atom atom ) {
        boolean result = false;
        AtomicState[] states = atom.getStates();
        if ( QuantumConfig.ENABLE_ALL_STIMULATED_EMISSIONS ) {
            for ( int i = 0; i < states.length && states[i] != this && result == false; i++ ) {
                AtomicState state = states[i];
                if ( state.getEnergyLevel() < this.getEnergyLevel() ) {
                    double stimulatedPhotonEnergy = this.getEnergyLevel() - state.getEnergyLevel();
                    result = ( Math.abs( photon.getEnergy() - stimulatedPhotonEnergy ) <= QuantumConfig.ENERGY_TOLERANCE
                               && Math.random() < STIMULATION_LIKELIHOOD );
                }
            }
        }
        else {
            double stimulatedPhotonEnergy = this.getEnergyLevel() - this.getNextLowerEnergyState().getEnergyLevel();
            result = Math.abs( photon.getEnergy() - stimulatedPhotonEnergy ) <= QuantumConfig.ENERGY_TOLERANCE
                     && Math.random() < STIMULATION_LIKELIHOOD;
        }
        return result;
    }

    /**
     * @param atom
     * @param photon
     */
    public void collideWithPhoton( Atom atom, Photon photon ) {

        // See if the photon knocks the atom to a higher state
        AtomicState newState = getElevatedState( atom, photon, this.getEnergyLevel() );
        if ( newState != null ) {
            photon.removeFromSystem();
            atom.setCurrState( newState );
            return;
        }

        // If the photon has the same energy as the difference
        // between this level and a lower state, then emit
        // a photon of that energy
        if ( isStimulatedBy( photon, atom ) ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() );
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = StimulatedPhoton.createStimulated( photon, position, atom );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setCurrState( atom.getLowestEnergyState() );
        }
    }

    /**
     * Searches through the states of a specified atom for one whose energy differential between it and
     * a specified energy matches the energy in a specified photon. The reason the energy needs to be
     * specified as a parameter is that the GroundState has to pretend it has energy of 0 for the colors
     * and such to work right, but other states can use their actual energies.
     *
     * @param atom
     * @param photon
     * @param energy
     * @return the state that the atom can be in that is the specified energy above its current state
     */
    public AtomicState getElevatedState( Atom atom, Photon photon, double energy ) {
        AtomicState result = null;
        AtomicState[] states = atom.getStates();
        for ( int stateIdx = states.length - 1;
              stateIdx >= 0 && states[stateIdx] != this && result == null;
              stateIdx-- ) {
            double de = photon.getEnergy() - ( states[stateIdx].getEnergyLevel() - energy );
            if ( Math.abs( de ) <= QuantumConfig.ENERGY_TOLERANCE ) {
                result = states[stateIdx];
            }
        }
        return result;
    }

    public AtomicState getNextLowerEnergyState() {
        return nextLowerState;
    }

    public void setNextLowerEnergyState( AtomicState nextLowerState ) {
        this.nextLowerState = nextLowerState;
    }

    public AtomicState getNextHigherEnergyState() {
        return nextHigherState;
    }

    public void setNextHigherEnergyState( AtomicState state ) {
        nextHigherState = state;
    }

    public int hashCode() {
        return (int) ( Double.doubleToLongBits( energyLevel ) + Double.doubleToLongBits( meanLifetime ) );
    }

    /**
     * Tests only the energy level and wavelength. Cannot test the nextHigherState and nextLowerState
     * because that results in stack overflows.
     *
     * @param obj
     * @return true if equal
     */
    public boolean equals( Object obj ) {
        boolean result = false;
        if ( obj instanceof AtomicState && obj != null ) {
            AtomicState that = (AtomicState) obj;
            result = this.energyLevel == that.energyLevel;
        }
        return result;
    }

    /**
     * Sets the next-higher and next-lower attributes for an array of AtomicStates
     *
     * @param states
     */
    public static void linkStates( AtomicState[] states ) {
        for ( int i = 1; i < states.length; i++ ) {
            states[i].setNextLowerEnergyState( states[i - 1] );
            states[i - 1].setNextHigherEnergyState( states[i] );
        }
        states[states.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    public static class MaxEnergyState extends AtomicState {
        private static MaxEnergyState instance = new MaxEnergyState();

        public static MaxEnergyState instance() {
            return instance;
        }

        private MaxEnergyState() {
            setEnergyLevel( getEnergyLevel() );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }

        public double getWavelength() {
            // The hard-coded number here is a hack so the energy level graphic can be adjusted up to the top of
            // the window. This is not great programming
            return minWavelength - 80;
        }
    }

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    public static class MinEnergyState extends AtomicState {
        private static MinEnergyState instance = new MinEnergyState();

        public static MinEnergyState instance() {
            return instance;
        }

        private MinEnergyState() {
            setEnergyLevel( minEnergy );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }
    }

    /**
     * @deprecated
     */
    public interface MeanLifetimeChangeListener extends EventListener {
        public void meanLifetimeChanged( MeanLifetimeChangeEvent event );
    }

    /**
     * @deprecated
     */
    public static class MeanLifetimeChangeEvent extends EventObject {
    	private AtomicState state;
        public MeanLifetimeChangeEvent(AtomicState state) {
            super( state);
            this.state = state;
        }

        public double getMeanLifetime() {
            return state.getMeanLifeTime();
        }
    }

    //-------------------------------------------------------------------
    // Events and event handling
    //-------------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener) listenerChannel.getListenerProxy();

    public static class Event extends EventObject {
        public Event( AtomicState source ) {
            super( source );
        }

        public double getEnergy() {
            return ( (AtomicState) getSource() ).getEnergyLevel();
        }

        public AtomicState getAtomicState() {
            return (AtomicState) getSource();
        }

        public double getMeanLifetime() {
            return getAtomicState().getMeanLifeTime();
        }
    }

    public interface Listener extends EventListener {
        void energyLevelChanged( Event event );

        void meanLifetimechanged( Event event );
    }

    static public class ChangeListenerAdapter implements Listener {
        public void energyLevelChanged( Event event ) {
        }

        public void meanLifetimechanged( Event event ) {
        }
    }

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }
}
