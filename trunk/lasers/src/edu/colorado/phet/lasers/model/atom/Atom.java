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
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 */
public class Atom extends SphericalBody {

    static private int s_radius = 15;
    static private int s_mass = 1000;
    private SpontaneouslyEmittingState.StateLifetimeManager stateLifetimeManager;

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


    private AtomicState state;
    private SubscriptionService subscriptionService = new SubscriptionService();
    private EventRegistry eventRegistry = new EventRegistry();

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

    public class PhotonEmissionEvent extends EventObject {
        private Photon photon;

        public PhotonEmissionEvent( Photon photon ) {
            super( Atom.this );
            this.photon = photon;
        }

        public Photon getPhoton() {
            return photon;
        }
    }

    public interface PhotonEmissionListener extends EventListener {
        void photonEmissionOccurred( PhotonEmissionEvent event );
    }

    public class RemovalEvent extends EventObject {
        public RemovalEvent() {
            super( Atom.this );
        }
    }

    public interface RemovalListener extends EventListener {
        void removalOccurred( RemovalEvent event );
    }


    //    public interface Listener {
    //        void photonEmitted( Atom atom, Photon photon );
    //        void leftSystem( Atom atom );
    //        void stateChanged( Atom atom, AtomicState oldState, AtomicState newState );
    //    }

    public Atom() {
        super( s_radius );
        setMass( s_mass );
        setState( GroundState.instance() );
    }

    //    public void addListener( Listener listner ) {
    //        subscriptionService.addListener( listner );
    //    }
    //
    //    public void removeListener( Listener listener ) {
    //        subscriptionService.removeListener( listener );
    //    }

    public void collideWithPhoton( Photon photon ) {
        state.collideWithPhoton( this, photon );
    }

    public AtomicState getState() {
        return state;
    }

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
        if( newState instanceof SpontaneouslyEmittingState ) {
            this.stateLifetimeManager = new SpontaneouslyEmittingState.StateLifetimeManager( this );
        }

        eventRegistry.fireEvent( new StateChangeEvent() );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    /**
     *
     */
    void emitPhoton( final Photon emittedPhoton ) {
        eventRegistry.fireEvent( new PhotonEmissionEvent( emittedPhoton ) );
    }

    public void removeFromSystem() {
        state.decrementNumInState();
    }
}
