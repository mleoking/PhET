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
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;
import edu.colorado.phet.lasers.model.photon.Photon;

/**
 *
 */
public class Atom extends SphericalBody {

    static private int s_radius = 15;
    static private int s_mass = 1000;

    static public void setHighEnergySpontaneousEmissionTime( double time ) {
        HighEnergyState.setSpontaneousEmmisionHalfLife( time );
    }

    static public void setMiddleEnergySpontaneousEmissionTime( double time ) {
        MiddleEnergyState.setSpontaneousEmmisionHalfLife( time );
    }

    static public int getNumGroundStateAtoms() {
        return GroundState.getNumInstances();
    }

    static public int getNumMiddleStateAtoms() {
        return MiddleEnergyState.getNumInstances();
    }

    static public int getNumHighStateAtoms() {
        return HighEnergyState.getNumInstances();
    }


    private AtomicState state;
    private SubscriptionService subscriptionService = new SubscriptionService();
    public interface Listener {
        void photonEmitted( Atom atom, Photon photon );
        void leftSystem( Atom atom );
        void stateChanged( Atom atom, AtomicState oldState, AtomicState newState );
    }

    public Atom() {
        super( s_radius );
        setMass( s_mass );
        setState( new GroundState( this ) );
    }

    public void addListener( Listener listner ) {
        subscriptionService.addListener( listner );
    }

    public void removeListener( Listener listener ) {
        subscriptionService.removeListener( listener );
    }

    public void collideWithPhoton( Photon photon ) {
        state.collideWithPhoton( photon );
    }

    public AtomicState getState() {
        return state;
    }

    public void setState( final AtomicState newState ) {
        final AtomicState oldState = this.state;
        this.state = newState;
        notifyObservers();
        this.subscriptionService.notifyListeners( new SubscriptionService.Notifier() {
            public void doNotify( Object obj ) {
                ((Listener)obj).stateChanged( Atom.this, oldState, newState );
            }
        } );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    /**
     *
     */
    void emitPhoton( final Photon emittedPhoton ) {
        emittedPhoton.collideWithAtom( this );
        subscriptionService.notifyListeners( new SubscriptionService.Notifier() {
            public void doNotify( Object obj ) {
                ((Listener)obj).photonEmitted( Atom.this, emittedPhoton );
            }
        } );
    }

    public void removeFromSystem() {
        subscriptionService.notifyListeners( new SubscriptionService.Notifier() {
            public void doNotify( Object obj ) {
                ((Listener)obj).leftSystem( Atom.this );
            }
        } );
        state.decrementNumInState();
    }
}
