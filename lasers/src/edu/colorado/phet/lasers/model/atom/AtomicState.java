/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

public abstract class AtomicState {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //

    static public final double minWavelength = Photon.BLUE - 20;
//    static public final double minWavelength = Photon.BLUE - 80;
    static public final double maxWavelength = Photon.GRAY;
//    static public final double minEnergy = 0;
    static public final double minEnergy = Photon.wavelengthToEnergy( maxWavelength );
    static public final double maxEnergy = Photon.wavelengthToEnergy( minWavelength );
    static protected double s_collisionLikelihood = 1;
    static protected final double wavelengthTolerance = 10;
    //        static protected double s_collisionLikelihood = 0.2;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private double energyLevel;
    private double wavelength;
    private int numAtomsInState;
    private double meanLifetime = Double.POSITIVE_INFINITY;
    private AtomicState nextHigherState;
    private AtomicState nextLowerState;

    void incrNumInState() {
        numAtomsInState++;
    }

    void decrementNumInState() {
        numAtomsInState--;
    }

    public int getNumAtomsInState() {
        return numAtomsInState;
    }

    public void clearNumInState() {
        numAtomsInState = 0;
    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Returns the lifetime of the state. This is based on the energy level.
     * The higher the energy, the shorter the lifetime.
     *
     * @return
     */
    public double getMeanLifeTime() {
        return meanLifetime;
    }

    public void setMeanLifetime( double lifetime ) {
        this.meanLifetime = lifetime;
        listenerProxy.meanLifetimechanged( new Event( this ) );
    }

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        this.wavelength = Photon.energyToWavelength( energyLevel );
        listenerProxy.energyLevelChanged( new Event( this ) );
    }

    public double getWavelength() {
        return wavelength;
    }

    public double determineEmittedPhotonWavelength() {
        double energy1 = Photon.wavelengthToEnergy( this.getWavelength() );
        double energy2 = Photon.wavelengthToEnergy( this.getNextLowerEnergyState().getWavelength() );

        // todo: this isn't right. It doesn't work for upper to middle transitions
        double emittedWavelength = Math.min( Photon.energyToWavelength( energy1 - energy2 + AtomicState.minEnergy ),
                                             AtomicState.maxWavelength );
        return emittedWavelength;
    }

    protected boolean isStimulatedBy( Photon photon ) {
        return ( Math.abs( photon.getWavelength() - this.getWavelength() ) <= wavelengthTolerance && Math.random() < s_collisionLikelihood );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( isStimulatedBy( photon ) ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() );
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = Photon.createStimulated( photon, position, atom );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setCurrState( atom.getLowestEnergyState() );
        }

        // Is the atom raised to a higher state?
//        else if( Math.random() < s_collisionLikelihood ) {
//            AtomicState newState = getStimulatedState( atom, photon, 0 );
//            if( newState != null ) {
//                photon.removeFromSystem();
//                atom.setCurrState( newState );
//            }
//        }
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
     * @return
     */
    public AtomicState getStimulatedState( Atom atom, Photon photon, double energy ) {
        AtomicState result = null;
        AtomicState[] states = atom.getStates();
        double energyThreshold = 0.5E-38;
        for( int stateIdx = states.length - 1;
             stateIdx >= 0 && states[stateIdx] != this && result == null;
             stateIdx-- ) {
            double de = photon.getEnergy() - ( states[stateIdx].getEnergyLevel() - energy );
            if( Math.abs( de ) < energyThreshold ) {
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

    //////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //

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

        public double getEnergyLevel() {
            return Photon.wavelengthToEnergy( getWavelength() );
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

    public interface MeanLifetimeChangeListener extends EventListener {
        public void meanLifetimeChanged( MeanLifetimeChangeEvent event );
    }

    public class MeanLifetimeChangeEvent extends EventObject {
        public MeanLifetimeChangeEvent() {
            super( AtomicState.this );
        }

        public double getMeanLifetime() {
            return AtomicState.this.getMeanLifeTime();
        }
    }

    //-------------------------------------------------------------------
    // Events and event handling
    //-------------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerChannel.getListenerProxy();

    public class Event extends EventObject {
        public Event( Object source ) {
            super( source );
        }

        public double getEnergy() {
            return ( (AtomicState)getSource() ).getEnergyLevel();
        }

        public AtomicState getAtomicState() {
            return (AtomicState)getSource();
        }

        public double getMeanLifetime() {
            return getAtomicState().getMeanLifeTime();
        }
    }

    public interface Listener extends EventListener {
        void energyLevelChanged( Event event );

        void meanLifetimechanged( Event event );
    }

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }
}
