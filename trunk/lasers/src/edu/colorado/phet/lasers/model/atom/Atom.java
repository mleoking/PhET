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

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 *
 */
public class Atom extends SolidSphere {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    static private int s_radius = 15;
    static private int s_mass = 1000;

    public static int getS_radius() {
        return s_radius;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private StateLifetimeManager stateLifetimeManager;
    private BaseModel model;
    private AtomicState currState;
    private AtomicState[] states;

    public Atom( LaserModel model, int numStates ) {
        super( s_radius );
        this.model = model;
        setMass( s_mass );
        setCurrState( model.getGroundState() );
        setNumEnergyLevels( numStates, model );
    }

    public void collideWithPhoton( Photon photon ) {
        currState.collideWithPhoton( this, photon );
    }

    public AtomicState getCurrState() {
        return currState;
    }

    public AtomicState[] getStates() {
        return states;
    }

    /**
     * Sets the energy currState of the atom. If this is a currState from which the atom can spontanteously
     * change, a StateLifetimeManager is instatiated to control the change.
     *
     * @param newState
     */
    public void setCurrState( final AtomicState newState ) {
        final AtomicState oldState = this.currState;

        if( oldState != null ) {
            oldState.decrementNumInState();
        }
        if( this.stateLifetimeManager != null ) {
            stateLifetimeManager.kill();
        }
        newState.incrNumInState();
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

    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        super.setPosition( position );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    /**
     *
     */
    void emitPhoton( final Photon emittedPhoton ) {
        photonEmittedListenerProxy.photonEmittedEventOccurred( new PhotonEmittedEvent( this, emittedPhoton ) );
    }

    public void removeFromSystem() {
        currState.decrementNumInState();
        leftSystemListenerProxy.leftSystem( new LeftSystemEvent( this ) );
        changeListenerProxy.stateChanged( new ChangeEvent( this, null, this.getCurrState() ) );
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
    }

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
