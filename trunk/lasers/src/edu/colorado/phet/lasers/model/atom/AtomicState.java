/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.EventObject;

public abstract class AtomicState {

    //
    // Class
    //
    // Determines how often a photon contacting an atom will result in a collision
    static private double PLANCK = 6.626E-34;
    static public final double minWavelength = 350;
    static public final double maxWavelength = 800;
    static public final double minEnergy = wavelengthToEnergy( maxWavelength );
    static public final double maxEnergy = wavelengthToEnergy( minWavelength );
    static protected double s_collisionLikelihood = 1;
    //        static protected double s_collisionLikelihood = 0.2;

    public static double energyToWavelength( double energy ) {
        return PLANCK / energy;
    }

    public static double wavelengthToEnergy( double wavelength ) {
        return PLANCK / wavelength;
    }

    //
    // Inner classes
    //

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    protected static class MaxEnergyState extends AtomicState {
        private static MaxEnergyState instance = new MaxEnergyState();

        public static MaxEnergyState instance() {
            return instance;
        }

        private MaxEnergyState() {
            setEnergyLevel( maxEnergy );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }
    }

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    protected static class MinEnergyState extends AtomicState {
        private static MinEnergyState instance = new MinEnergyState();

        public static MinEnergyState instance() {
            return instance;
        }

        private MinEnergyState() {
            setEnergyLevel( minEnergy );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }
    }


    //
    // Instance
    //
    private double energyLevel;
    private double wavelength;
    private int numAtomsInState;
    private EventListenerList listeners = new EventListenerList();
    private double meanLifetime = LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME / 1000;


    abstract public void collideWithPhoton( Atom atom, Photon photon );


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

    void incrNumInState() {
        numAtomsInState++;
    }

    void decrementNumInState() {
        numAtomsInState--;
    }

    int getNumAtomsInState() {
        return numAtomsInState;
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
    public double getMeanLifeTime() {
        return meanLifetime;
        // Note: the hard-coded figure here is just a holding value
        //        return energyLevel == 0 ? Double.POSITIVE_INFINITY : 10000 / energyLevel;
    }

    public void setMeanLifetime( double lifetime ) {
        this.meanLifetime = lifetime;
    }

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        this.wavelength = energyToWavelength( energyLevel );
        fireEnergyLevelChangeEvent( new EnergyLevelChangeEvent( this ) );
    }

    public double getWavelength() {
        return wavelength;
    }

    protected void setEmittedPhotonWavelength( int wavelength ) {
        this.wavelength = wavelength;
        this.energyLevel = wavelengthToEnergy( wavelength );
    }

    protected double getEmittedPhotonWavelength() {
        return wavelength;
    }

    abstract public AtomicState getNextLowerEnergyState();

    abstract public AtomicState getNextHigherEnergyState();

}
