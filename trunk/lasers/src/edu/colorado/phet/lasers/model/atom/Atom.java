/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 21, 2003
 * Time: 12:10:01 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.collision.SphericalBody;

import java.util.LinkedList;

/**
 *
 */
public class Atom extends SphericalBody {

    private AtomicState state;
    private LinkedList listeners = new LinkedList();

    public interface Listener {
        void photonEmitted( Atom atom, Photon photon );
        void leftSystem( Atom atom );
    }
    /**
     *
     */
    public Atom() {
        super( s_radius );
        setMass( s_mass );
        setState( new GroundState( this ) );
    }

    public void addListener( Listener listner ) {
        listeners.add( listner );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void collideWithPhoton( Photon photon ) {
        state.collideWithPhoton( photon );
    }

    public AtomicState getState() {
        return state;
    }

    public void setState( AtomicState newState ) {
        this.state = newState;
//        setChanged();
        notifyObservers();
    }

    public void stepInTime( float dt ) {
        super.stepInTime( dt );
        state.stepInTime( dt );
    }

    /**
     *
     */
    void emitPhoton( Photon emittedPhoton ) {
//        emittedPhoton.setPosition( getPosition().getX(),
//                                   getPosition().getY() );
        emittedPhoton.collideWithAtom( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.photonEmitted( this, emittedPhoton );
        }
//        new AddParticleCmd( emittedPhoton ).doIt();
    }

    public void removeFromSystem() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.leftSystem( this );
        }
//        super.removeFromSystem();
        state.decrementNumInState();
    }

    //
    // Static fields and methods
    //
    static private int s_radius = 15;
    static private int s_mass = 1000;

    static public void setHighEnergySpontaneousEmissionTime( float time ) {
        HighEnergyState.setSpontaneousEmmisionHalfLife( time );
    }

    static public void setMiddleEnergySpontaneousEmissionTime( float time ) {
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
}
