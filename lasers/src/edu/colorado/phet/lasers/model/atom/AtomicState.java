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

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        EventRegistry.instance.fireEvent( new EnergyLevelChange( this ));
    }

    abstract public void collideWithPhoton( Atom atom, Photon photon );
    abstract void incrNumInState();
    abstract void decrementNumInState();
}
