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

import edu.colorado.phet.collision.SolidSphere;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;

import java.util.EventListener;
import java.util.EventObject;
import java.awt.geom.Point2D;

/**
 *
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
    private BaseModel model;
    private AtomicState currState;
    private AtomicState[] states;
    private boolean isStateLifetimeFixed = false;

    /**
     * @param model
     * @param numStates
     */
    public Atom( LaserModel model, int numStates ) {
        this( model, numStates, false );
    }

    /**
     * @param model
     * @param numStates
     * @param isStateLifetimeFixed
     */
    public Atom( LaserModel model, int numStates, boolean isStateLifetimeFixed ) {
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
        double minEnergy = Double.MAX_VALUE;
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            double energy = state.getEnergyLevel();
            if( energy > maxEnergy ) {
                maxEnergy = energy;
                highestEnergyState = state;
            }
            if( energy < minEnergy ) {
                minEnergy = energy;
                groundState = state;
            }
        }
        setCurrState( groundState );
    }

    /**
     * Populates the list of AtomicStates that this atom can be in, based on the
     * specified number of energy levels it can occupy
     *
     * @param numEnergyLevels
     */
    public void setNumEnergyLevels( int numEnergyLevels, LaserModel model ) {
        states = new AtomicState[numEnergyLevels];
        states[0] = model.getGroundState();
        states[1] = model.getMiddleEnergyState();
        if( numEnergyLevels == 3 ) {
            states[2] = model.getHighEnergyState();
        }
        groundState = states[0];
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
        super.setPosition( x, y );
        if( getPosition().getX() != getPositionPrev().getX() || getPosition().getY() != getPositionPrev().getY() ) {
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
        if( getPosition().getX() != getPositionPrev().getX() || getPosition().getY() != getPositionPrev().getY() ) {
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
        boolean emitPhotonOnLeavingState = false;
        if( newState instanceof MiddleEnergyState ) {
            emitPhotonOnLeavingState = true;
        }

        // DEBUG.
        emitPhotonOnLeavingState = true;

        this.stateLifetimeManager = new StateLifetimeManager( this, emitPhotonOnLeavingState, model );
        changeListenerProxy.stateChanged( new ChangeEvent( this, newState, oldState ) );
    }

    /**
     *
     */
    void emitPhoton( final Photon emittedPhoton ) {
        photonEmittedListenerProxy.photonEmittedEventOccurred( new PhotonEmittedEvent( this, emittedPhoton ) );
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
     * ChangeEvent and associated code
     */
    public class ChangeEvent extends EventObject {
        private AtomicState currState;
        private AtomicState prevState;

        public ChangeEvent( Atom source, AtomicState currState, AtomicState prevState ) {
            super( source );
            this.prevState = prevState;
            this.currState = currState;
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
     * LeftSystemEvent and associated code
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

    private EventChannel photonEventChannel = new EventChannel( PhotonEmittedListener.class );
    PhotonEmittedListener photonEmittedListenerProxy = (PhotonEmittedListener)photonEventChannel.getListenerProxy();

    public void addPhotonEmittedListener( PhotonEmittedListener listener ) {
        photonEventChannel.addListener( listener );
    }

    public void removePhotonEmittedListener( PhotonEmittedListener listener ) {
        photonEventChannel.removeListener( listener );
    }
}
