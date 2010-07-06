/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.model.PhotonEmitter.Listener;

/**
 * Primary model for the Photon Absorption tab.  This models photons being
 * absorbed (or often NOT absorbed) by various molecules.  The scale for this
 * model is picometers (10E-12 meters).
 * 
 * @author John Blanco
 */
public class PhotonAbsorptionModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Constant that controls the point in model space where photons are
    // emitted.
    private static final Point2D PHOTON_EMISSION_LOCATION = new Point2D.Double(-50, 20);
    
    // Velocity of emitted photons.  Since they are emitted horizontally, only
    // one value is needed.
    private static final float PHOTON_VELOCITY_X = 0.2f;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private EventListenerList listeners = new EventListenerList();
    private ArrayList<Photon> photons = new ArrayList<Photon>();
    private double photonWavelength = GreenhouseConfig.sunlightWavelength;
   
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    public PhotonAbsorptionModel(GreenhouseClock clock){
        clock.addClockListener(new ClockAdapter(){
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime(clockEvent.getSimulationTimeChange());
            }
        });
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    public void stepInTime(double dt){
        for (Photon photon : photons){
            photon.stepInTime( dt );
        }
        // TODO: Need to delete the photons.
    }
    
    public Point2D getPhotonEmissionLocation(){
        return PHOTON_EMISSION_LOCATION;
    }
    
    /**
     * Cause a photon to be emitted from the emission point.
     */
    public void emitPhoton(){
        
        Photon photon = new Photon( photonWavelength, null );
        photon.setLocation( PHOTON_EMISSION_LOCATION.getX(), PHOTON_EMISSION_LOCATION.getY() );
        photons.add( photon );
        photon.setVelocity( PHOTON_VELOCITY_X, 0 );
        notifyPhotonAdded( photon );
    }
    
    public void setPhotonWavelength(double freq){
        if (this.photonWavelength != freq){
            this.photonWavelength = freq;
            notifyPhotonWavelengthChanged();
        }
    }
    
    public double getPhotonWavelength(){
        return photonWavelength;
    }
    
    public void addListener(Listener listener){
        listeners.add(Listener.class, listener);
    }
    
    public void removeListener(Listener listener){
        listeners.remove(Listener.class, listener);
    }
    
    private void notifyPhotonAdded(Photon photon){
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonAdded(photon);
        }
    }
    
    private void notifyPhotonRemoved(Photon photon){
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonRemoved(photon);
        }
    }
    
    private void notifyPhotonWavelengthChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonWavelengthChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    public interface Listener extends EventListener {
        void photonAdded(Photon photon);
        void photonRemoved(Photon photon);
        void photonWavelengthChanged();
    }
    
    public static class Adapter implements Listener {
        public void photonAdded( Photon photon ) {}
        public void photonWavelengthChanged() {}
        public void photonRemoved( Photon photon ) {}
    }
}
