/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.greenhouse.GreenhouseConfig;

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
    private static final Point2D PHOTON_EMISSION_LOCATION = new Point2D.Double(-1300, 0);
    
    // Location used when a single molecule is sitting in the area where the
    // photons pass through.
    private static final Point2D SINGLE_MOLECULE_LOCATION = new Point2D.Double(0, 0);
    
    // Velocity of emitted photons.  Since they are emitted horizontally, only
    // one value is needed.
    private static final float PHOTON_VELOCITY_X = 4.0f;
    
    // Distance for a photon to travel before being removed from the model.
    // This value is essentially arbitrary, and needs to be set such that the
    // photons only disappear after they have traveled beyond the bounds of
    // the play area.
    private static final double MAX_PHOTON_DISTANCE = 3000;
    
    // Constants that define the size of the containment area, which is the
    // rectangle that surrounds the molecule(s).
    private static final double CONTAINMENT_AREA_WIDTH = 3100;   // In picometers.
    private static final double CONTAINMENT_AREA_HEIGHT = 3000;  // In picometers.
    private static final Point2D CONTAINMENT_AREA_CENTER = new Point2D.Double(0,0);
    private static final Rectangle2D CONTAINMENT_AREA_RECT = new Rectangle2D.Double(
            CONTAINMENT_AREA_CENTER.getX() - CONTAINMENT_AREA_WIDTH / 2,
            CONTAINMENT_AREA_CENTER.getY() - CONTAINMENT_AREA_HEIGHT / 2,
            CONTAINMENT_AREA_WIDTH,
            CONTAINMENT_AREA_HEIGHT
            );
    
    // Choices of targets for the photon emission.
    public enum PhotonTarget { CO2, H2O, CH4, N2O, N2, O2, EARTH_AIR, VENUS_AIR };
    
    // Defaults.
    private static final PhotonTarget DEFAULT_PHOTON_TARGET = PhotonTarget.CO2;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private EventListenerList listeners = new EventListenerList();
    private ArrayList<Photon> photons = new ArrayList<Photon>();
    private double photonWavelength = GreenhouseConfig.sunlightWavelength;
    private final ArrayList<Molecule> molecules = new ArrayList<Molecule>();
    private PhotonTarget photonTarget = null;
    
    private Molecule.Adapter moleculePhotonEmissionListener = new Molecule.Adapter(){
        public void photonEmitted(Photon photon) {
            photons.add( photon );
            notifyPhotonAdded( photon );
        }
    };
   
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    public PhotonAbsorptionModel(GreenhouseClock clock){
        
        // Listen to the clock in order to step this model.
        clock.addClockListener(new ClockAdapter(){
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime(clockEvent.getSimulationTimeChange());
            }
        });
        
        // Note: It is expected that this model will be reset as part of the
        // initialization sequence, so additional initialization is performed
        // there.
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Reset the model to its initial state. 
     */
    public void reset() {
        setPhotonTarget( DEFAULT_PHOTON_TARGET );
    }

    public void stepInTime(double dt){
        ArrayList<Photon> photonsToRemove = new ArrayList<Photon>();
        for (Photon photon : photons){
            photon.stepInTime( dt );
            if ( photon.getLocation().getX() - PHOTON_EMISSION_LOCATION.getX() > MAX_PHOTON_DISTANCE ){
                photonsToRemove.add( photon );
            }
            else{
                // See if any of the molecules wish to absorb this photon.
                for (Molecule molecule : molecules){
                    if (molecule.absorbPhoton( photon )){
                        photonsToRemove.add(  photon );
                    }
                }
            }
        }
        // Remove any photons that have finished traveling as far as they
        // will go.
        for (Photon photon : photonsToRemove){
            photons.remove( photon );
            notifyPhotonRemoved( photon );
        }
        
        // Step the molecules.
        for (Molecule molecule : molecules){
            molecule.stepInTime( dt );
        }
    }
    
    public void setPhotonTarget( PhotonTarget photonTarget ){
        if (this.photonTarget != photonTarget){
            this.photonTarget = photonTarget;
            
            // Remove the old photon target(s).
            ArrayList<Molecule> copyOfMolecules = new ArrayList<Molecule>( molecules );
            molecules.clear();
            for (Molecule molecule : copyOfMolecules){
                notifyMoleculeRemoved( molecule );
            }
            
            // Add the new photon target(s).
            Molecule molecule;
            switch (photonTarget){
            case CO2:
                molecule = new CarbonDioxide(SINGLE_MOLECULE_LOCATION);
                molecule.addListener( moleculePhotonEmissionListener );
                molecules.add( molecule );
                notifyMoleculeAdded( molecule );
                break;
            case H2O:
                molecule = new H2O(SINGLE_MOLECULE_LOCATION);
                molecule.addListener( moleculePhotonEmissionListener );
                molecules.add( molecule );
                notifyMoleculeAdded( molecule );
                break;
            default:
                System.err.println(getClass().getName() + " - Error: Unhandled molecule type.");
                break;
            }
            
            // Send out general notification about the change.
            notifyPhotonTargetChanged();
        }
    }
    
    public PhotonTarget getPhotonTarget(){
        return photonTarget;
    }
    
    public Point2D getPhotonEmissionLocation(){
        return PHOTON_EMISSION_LOCATION;
    }
    
    public Rectangle2D getContainmentAreaRect(){
        return CONTAINMENT_AREA_RECT;
    }
    
    public ArrayList<Molecule> getMolecules(){
        return new ArrayList<Molecule>(molecules);
    }
    
    /**
     * Cause a photon to be emitted from the emission point.
     */
    public void emitPhoton(){
        
        Photon photon = new Photon( photonWavelength, null );
        photon.setLocation( PHOTON_EMISSION_LOCATION.getX(), PHOTON_EMISSION_LOCATION.getY() );
        photon.setVelocity( PHOTON_VELOCITY_X, 0 );
        photons.add( photon );
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
    
    private void notifyMoleculeAdded(Molecule molecule){
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.moleculeAdded( molecule );
        }
    }
    
    private void notifyMoleculeRemoved(Molecule molecule){
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.moleculeRemoved( molecule );
        }
    }
    
    private void notifyPhotonWavelengthChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonWavelengthChanged();
        }
    }

    private void notifyPhotonTargetChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonTargetChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    public interface Listener extends EventListener {
        void photonAdded(Photon photon);
        void photonRemoved(Photon photon);
        void moleculeAdded(Molecule molecule);
        void moleculeRemoved(Molecule molecule);
        void photonWavelengthChanged();
        void photonTargetChanged();
    }
    
    public static class Adapter implements Listener {
        public void photonAdded( Photon photon ) {}
        public void photonWavelengthChanged() {}
        public void photonRemoved( Photon photon ) {}
        public void photonTargetChanged() {}
        public void moleculeAdded( Molecule molecule ) {}
        public void moleculeRemoved( Molecule molecule ) {}
    }
}
