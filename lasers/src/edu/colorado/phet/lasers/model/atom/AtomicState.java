/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.EventRegistry;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.util.EventObject;

public abstract class AtomicState {

    //
    // Class
    //
    static protected double s_collisionLikelihood = 1;
    //    static protected double s_collisionLikelihood = 0.2;


    //
    // Instance
    //
    private double energyLevel;

    public interface EnergyLevelListener {
        void energyLevelChangeOccurred( EnergyLevelChange event );
    }

    public class EnergyLevelChange extends EventObject {
        public EnergyLevelChange( Object source ) {
            super( source );
        }
    }

    public interface Listener {
        void numInStateChanged( int num );
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
    public double getLifeTime() {
        // Note: the hard-coded figure here is just a holding value
        return energyLevel == 0 ? Double.POSITIVE_INFINITY : 10000 / energyLevel;
    }

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        EventRegistry.instance.fireEvent( new EnergyLevelChange( this ) );
    }

    abstract public void collideWithPhoton( Atom atom, Photon photon );

    abstract void incrNumInState();

    abstract void decrementNumInState();
}
