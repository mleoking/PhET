/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.EventObject;

public abstract class AtomicState {

    //
    // Class
    //
    // Determines how often a photon contacting an atom will result in a collision
    static protected double s_collisionLikelihood = 1;
    //    static protected double s_collisionLikelihood = 0.2;


    //
    // Instance
    //
    private double energyLevel;
    EventListenerList listeners = new EventListenerList();

    public interface EnergyLevelChangeListener extends EventListener {
        void energyLevelChangeOccurred( EnergyLevelChangeEvent event );
    }

    public class EnergyLevelChangeEvent extends EventObject {
        public EnergyLevelChangeEvent( Object source ) {
            super( source );
        }
    }

    public interface Listener {
        void numInStateChanged( int num );
    }

    public void addEnergyLevelChangeListener( EnergyLevelChangeListener listener ) {
        listeners.add( EnergyLevelChangeListener.class, listener );
    }

    public void removeEnergyLevelChangeListener( EnergyLevelChangeListener listener ) {
        listeners.remove( EnergyLevelChangeListener.class, listener );
    }

    void fireEnergyLevelChangeEvent( EnergyLevelChangeEvent event ) {
        Object[] listeners = this.listeners.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            Object o = listeners[i];
            if( o == EnergyLevelChangeListener.class ) {
                ( (EnergyLevelChangeListener)listeners[i + 1] ).energyLevelChangeOccurred( event );
            }
        }
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
        fireEnergyLevelChangeEvent( new EnergyLevelChangeEvent( this ) );
        //        EventRegistry.instance.fireEvent( new EnergyLevelChangeEvent( this ) );
    }

    abstract public void collideWithPhoton( Atom atom, Photon photon );

    abstract void incrNumInState();

    abstract void decrementNumInState();
}
