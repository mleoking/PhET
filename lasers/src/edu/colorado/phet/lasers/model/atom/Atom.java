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

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.lasers.EventRegistry;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.common.model.BaseModel;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 */
public class Atom extends SphericalBody {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    static private int s_radius = 15;
    static private int s_mass = 1000;
    private StateLifetimeManager stateLifetimeManager;
    private BaseModel model;

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Events and listeners
    public void addListener( EventListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( EventListener listener ) {
        eventRegistry.removeListener( listener );
    }

    public class StateChangeEvent extends EventObject {
        public StateChangeEvent() {
            super( Atom.this );
        }

        public AtomicState getState() {
            return Atom.this.getState();
        }
    }

    public interface StateChangeListener extends EventListener {
        void stateChangeOccurred( StateChangeEvent event );
    }

    public class RemovalEvent extends EventObject {
        public RemovalEvent() {
            super( Atom.this );
        }
    }

    public interface RemovalListener extends EventListener {
        void removalOccurred( RemovalEvent event );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private AtomicState state;
    private EventRegistry eventRegistry = new EventRegistry();

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
//        if( newState instanceof SpontaneouslyEmittingState ) {
            boolean emitPhotonOnLeavingState = false;
            if( newState instanceof MiddleEnergyState ) {
                emitPhotonOnLeavingState = true;
            }
            this.stateLifetimeManager = new StateLifetimeManager( this, emitPhotonOnLeavingState, model );
//        }

        eventRegistry.fireEvent( new StateChangeEvent() );
    }

    public void stepInTime( double dt ) {

        super.stepInTime( dt );
    }

    /**
     *
     */
    void emitPhoton( final Photon emittedPhoton ) {
        eventRegistry.fireEvent( new PhotonEmittedEvent( this, emittedPhoton ) );
        //        eventRegistry.fireEvent( new PhotonEmissionEvent( emittedPhoton ) );
    }

    public void removeFromSystem() {
        eventRegistry.fireEvent( new RemovalEvent() );
        state.decrementNumInState();
    }
}
