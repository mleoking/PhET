/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.collision.SolidSphere;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Atom
 * <p>
 * An atom has a set of AtomicState instances, which are specifically its
 * possible energy levels. How an atom transitions between those states is
 * dtermined by other classes. The intention is that a Strategy pattern be
 * used for this purpose. The length of time that an atom spends in an
 * elevated energy state before falling to a less energetic one is, however,
 * partially determined by the atom itself, based on a flag that indicates
 * whether its lifetime is a state is fixed or determined by some other means,
 * e.g. probabilistically.
 * <p>
 * Two of the atom's states are distinguished: the groundState and highestEnergyState.
 * The groundState has an infinited lifetime, and the highestEnergyState can't be exceeded. 
 */
public class Atom extends SolidSphere {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    static private int s_radius = 15;
    static private int s_mass = 1000;
    private AtomicState groundState;
    private AtomicState highestEnergyState;

    public static int getS_radius() {
        return s_radius;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private StateLifetimeManager stateLifetimeManager;
    private QuantumModel model;
    private AtomicState currState;
    private AtomicState[] states;
    private boolean isStateLifetimeFixed = false;

    /**
     * @param model
     * @param numStates
     */
    public Atom( QuantumModel model, int numStates ) {
        this( model, numStates, false );
    }

    /**
     * @param model
     * @param numStates
     * @param isStateLifetimeFixed
     */
    public Atom( QuantumModel model, int numStates, boolean isStateLifetimeFixed ) {
        super( s_radius );
        this.model = model;
        this.isStateLifetimeFixed = isStateLifetimeFixed;
        setMass( s_mass );
        setCurrState( model.getGroundState() );
        setNumEnergyLevels( numStates, model );
    }

    public AtomicState getCurrState() {
        return currState;
    }

    public AtomicState[] getStates() {
        return states;
    }

    protected QuantumModel getModel() {
        return model;
    }

    /**
     * Sets the states that the atom can be in. Sets the atom's current state to the
     * ground state
     *
     * @param states
     */
    public void setStates( AtomicState[] states ) {
        this.states = states;
        // Find the minimum and maximum energy states
        double maxEnergy = -Double.MAX_VALUE;
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            double energy = state.getEnergyLevel();
            if( energy > maxEnergy ) {
                maxEnergy = energy;
                highestEnergyState = state;
            }
        }
        groundState = getLowestEnergyState();
        setCurrState( groundState );
    }

    /**
     * Populates the list of AtomicStates that this atom can be in, based on the
     * specified number of energy levels it can occupy
     *
     * @param numEnergyLevels
     */
    public void setNumEnergyLevels( int numEnergyLevels, QuantumModel model ) {
        states = new AtomicState[numEnergyLevels];
        states[0] = model.getGroundState();
        groundState = states[0];
        highestEnergyState = states[states.length - 1];
    }

    /**
     * Returns the atom's state with the lowest energy
     *
     * @return
     */
    public AtomicState getLowestEnergyState() {
        AtomicState lowestState = null;
        double lowestEnergy = Double.MAX_VALUE;
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            if( state.getEnergyLevel() < lowestEnergy ) {
                lowestEnergy = state.getEnergyLevel();
                lowestState = state;
            }
        }
        return lowestState;
    }

    /**
     * Returns the state the atom will be in if it emits a photon. By default, this is the
     * next lower energy state
     *
     * @return
     */
    public AtomicState getEnergyStateAfterEmission() {
        return currState.getNextLowerEnergyState();
    }

    /**
     * @return
     */
    public boolean isStateLifetimeFixed() {
        return isStateLifetimeFixed;
    }

    /**
     * Extend parent class behavior to add notification of listeners
     *
     * @param x
     * @param y
     */
    public void setPosition( double x, double y ) {
        boolean positionChanged = false;
        if( getPosition().getX() != x || getPosition().getY() != y ) {
            positionChanged = true;
        }
        super.setPosition( x, y );
        if( changeListenerProxy != null && positionChanged ) {
            changeListenerProxy.positionChanged( new ChangeEvent( this, null, null ) );
        }
    }

    /**
     * Extend parent class behavior to add notification of listeners
     *
     * @param position
     */
    public void setPosition( Point2D position ) {
        super.setPosition( position );
        if( changeListenerProxy != null ) {
            changeListenerProxy.positionChanged( new ChangeEvent( this, null, null ) );
        }
    }

    /**
     * Sets the energy currState of the atom. If this is a currState from which the atom can spontanteously
     * change, a StateLifetimeManager is instatiated to control the change.
     *
     * @param newState
     */
    public void setCurrState( final AtomicState newState ) {
        final AtomicState oldState = this.currState;

        if( this.stateLifetimeManager != null ) {
            stateLifetimeManager.kill();
        }
        this.currState = newState;
        if( oldState != null ) {
            oldState.leaveState( this );
        }
        if( newState != null ) {
            newState.enterState(this );
        }

        this.stateLifetimeManager = new StateLifetimeManager( this, true, model );
        changeListenerProxy.stateChanged( new ChangeEvent( this, newState, oldState ) );
    }

    /**
     *
     */
    void emitPhoton( final Photon emittedPhoton ) {
        photonEmissionListenerProxy.photonEmitted( new PhotonEmittedEvent( this, emittedPhoton ) );
    }

    /**
     *
     */
    public void removeFromSystem() {
        leftSystemListenerProxy.leftSystem( new LeftSystemEvent( this ) );
        changeListenerProxy.stateChanged( new ChangeEvent( this, null, this.getCurrState() ) );
    }

    /**
     * @param photon
     */
    public void collideWithPhoton( Photon photon ) {
        currState.collideWithPhoton( this, photon );
    }

    /**
     * Returns the number of the atom's current state. This is the index of the state in the
     * atom's array of state. The ground state is number 0
     *
     * @return
     */
    public int getCurrStateNumber() {
        for( int i = 0; i < states.length; i++ ) {
            if( getCurrState().equals( states[i] ) ) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the atom's ground state
     *
     * @return
     */
    public AtomicState getGroundState() {
        return groundState;
    }

    /**
     * Returns the atom's highest energy state
     *
     * @return
     */
    public AtomicState getHighestEnergyState() {
        return highestEnergyState;
    }

    //----------------------------------------------------------------
    // Events and event handling
    //----------------------------------------------------------------

    /**
     * Events and listeners for the Atom changing its "state", which in this case
     * specifically means its energy level, and changing its location
     */
    public class ChangeEvent extends EventObject {
        private AtomicState currState;
        private AtomicState prevState;

        public ChangeEvent( Atom source, AtomicState currState, AtomicState prevState ) {
            super( source );
            this.prevState = prevState;
            this.currState = currState;
        }

        public Atom getAtom() {
            return (Atom)getSource();
        }

        public AtomicState getCurrState() {
            return currState;
        }

        public AtomicState getPrevState() {
            return prevState;
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );

        void positionChanged( ChangeEvent event );
    }

    static public class ChangeListenerAdapter implements ChangeListener {
        public void stateChanged( ChangeEvent event ) {
        }

        public void positionChanged( ChangeEvent event ) {
        }
    }

    private EventChannel stateChangeChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)stateChangeChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        stateChangeChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        stateChangeChannel.removeListener( listener );
    }

    /**
     * Events and listeners for the Atom leaving the model
     */
    public class LeftSystemEvent extends EventObject {
        public LeftSystemEvent( Object source ) {
            super( source );
        }

        public Atom getAtom() {
            return (Atom)getSource();
        }

        public AtomicState getState() {
            return getAtom().getCurrState();
        }
    }

    public interface LeftSystemListener extends EventListener {
        void leftSystem( LeftSystemEvent leftSystemEvent );
    }

    private EventChannel listenerChannel = new EventChannel( LeftSystemListener.class );
    private LeftSystemListener leftSystemListenerProxy = (LeftSystemListener)listenerChannel.getListenerProxy();

    public void addLeftSystemListener( LeftSystemListener leftSystemListener ) {
        listenerChannel.addListener( leftSystemListener );
    }

    public void removeLeftSystemListener( LeftSystemListener leftSystemListener ) {
        listenerChannel.removeListener( leftSystemListener );
    }

    private EventChannel photonEventChannel = new EventChannel( PhotonEmissionListener.class );
    PhotonEmissionListener photonEmissionListenerProxy = (PhotonEmissionListener)photonEventChannel.getListenerProxy();

    public void addPhotonEmittedListener( PhotonEmissionListener listener ) {
        photonEventChannel.addListener( listener );
    }

    public void removePhotonEmittedListener( PhotonEmissionListener listener ) {
        photonEventChannel.removeListener( listener );
    }
}
