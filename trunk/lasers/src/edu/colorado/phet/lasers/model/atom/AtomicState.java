/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.EventRegistry;

import java.util.EventObject;

public abstract class AtomicState {

//    private Atom atom;
    private double energyLevel;

    public interface EnergyLevelListener {
        void energyLevelChangeOccurred( AtomicState atomicState );
    }

    public class EnergyLevelChange extends EventObject {
        public EnergyLevelChange( Object source ) {
            super( source );
        }
    }

    public interface Listener {
        void numInStateChanged( int num );
    }

//    protected AtomicState( Atom atom ) {
//        this.atom = atom;
//    }

//    protected Atom getAtom() {
//        return atom;
//    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        EventRegistry.instance.fireEvent( new EnergyLevelChange( this ));
    }

    // Used by subclasses to handle time-dependent behaviors, such as
    // spontaneous emission
//    abstract public void stepInTime( double dt );

    abstract public void collideWithPhoton( Atom atom, Photon photon );

    abstract void incrNumInState();
    abstract void decrementNumInState();

    //
    // Static fields and methods
    //
    static protected double s_collisionLikelihood = 1;
    //    static protected double s_collisionLikelihood = 0.2;

}
