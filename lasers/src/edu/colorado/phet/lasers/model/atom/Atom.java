/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 21, 2003
 * Time: 12:10:01 PM
 * To change this template use Options | File Templates.
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.collision.SolidSphere;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.EventChannel;
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

    static public void setHighEnergySpontaneousEmissionTime( double time ) {
        HighEnergyState.instance().setMeanLifetime( time );
    }

    static public void setMiddleEnergySpontaneousEmissionTime( double time ) {
        MiddleEnergyState.instance().setMeanLifetime( time );
    }

    static public int getNumGroundStateAtoms() {
        return GroundState.instance().getNumAtomsInState();
    }

    static public int getNumMiddleStateAtoms() {
        return MiddleEnergyState.instance().getNumAtomsInState();
    }

    static public int getNumHighStateAtoms() {
        return HighEnergyState.instance().getNumAtomsInState();
    }

    public static int getS_radius() {
        return s_radius;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private StateLifetimeManager stateLifetimeManager;
    private BaseModel model;
    private AtomicState state;

    public Atom( BaseModel model ) {
        super( s_radius );
        this.model = model;
        setMass( s_mass );
        setState( GroundState.instance() );
    }

    public void collideWithPhoton( Photon photon ) {
        state.collideWithPhoton( this, photon );
    }

    public AtomicState getState() {
        return state;
    }

    /**
     * Sets the energy state of the atom. If this is a state from which the atom can spontanteously
     * change, a StateLifetimeManager is instatiated to control the change.
     *
     * @param newState
     */

    public void setState( final AtomicState newState ) {
        final AtomicState oldState = this.state;

        if( oldState != null ) {
            oldState.decrementNumInState();
        }
        if( this.stateLifetimeManager != null ) {
            stateLifetimeManager.kill();
        }
        newState.incrNumInState();
        this.state = newState;
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
        state.decrementNumInState();
        leftSystemListenerProxy.leftSystem( new LeftSystemEvent( this ) );
        changeListenerProxy.stateChanged( new ChangeEvent( this, null, this.getState() ) );
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
            return getAtom().getState();
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
