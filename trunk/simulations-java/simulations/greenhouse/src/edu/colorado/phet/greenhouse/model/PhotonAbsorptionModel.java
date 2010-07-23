/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
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
    private static final float PHOTON_VELOCITY_X = 2.0f;
    
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
    
    // Choices of targets for the photons.
    public enum PhotonTarget { SINGLE_CO2_MOLECULE, SINGLE_H2O_MOLECULE, SINGLE_CH4_MOLECULE, SINGLE_N2O_MOLECULE,
        SINGLE_N2_MOLECULE, SINGLE_O2_MOLECULE, CONFIGURABLE_ATMOSPHERE };
    
    // Default values for various parameters.
    private static final PhotonTarget DEFAULT_PHOTON_TARGET = PhotonTarget.SINGLE_CH4_MOLECULE;
    private static final double DEFAULT_EMITTED_PHOTON_WAVELENGTH = GreenhouseConfig.irWavelength;
    private static final double DEFAULT_PHOTON_EMISSION_PERIOD = 2000; // Milliseconds of sim time.
    
    // Initial and max values for the numbers of molecules in the configurable
    // atmosphere.
    static final HashMap< MoleculeID , Integer> INITIAL_ATMOSPHERE_CONCENTRATIONS = new HashMap< MoleculeID, Integer>() {{ 
        put(MoleculeID.N2, 10); 
        put(MoleculeID.O2, 10); 
        put(MoleculeID.CO2, 5); 
        put(MoleculeID.H2O, 5); 
    }}; 
    static final HashMap< MoleculeID , Integer> MAX_ATMOSPHERE_CONCENTRATIONS = new HashMap< MoleculeID, Integer>() {{ 
        put(MoleculeID.N2, 20); 
        put(MoleculeID.O2, 20); 
        put(MoleculeID.CO2, 20); 
        put(MoleculeID.H2O, 20); 
    }}; 
    
    // Random number generator.
    private static final Random RAND = new Random();

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private EventListenerList listeners = new EventListenerList();
    private ArrayList<Photon> photons = new ArrayList<Photon>();
    private double photonWavelength = GreenhouseConfig.sunlightWavelength;
    private final ArrayList<Molecule> activeMolecules = new ArrayList<Molecule>();
    private PhotonTarget photonTarget = null;
    
    // Variables that control periodic photon emission.
    private boolean periodicPhotonEmissionEnabled;
    private double photonEmissionCountdownTimer;
    private double photonEmissionPeriod = DEFAULT_PHOTON_EMISSION_PERIOD;
    
    // Collection that contains the molecules that comprise the configurable
    // atmosphere.
    private ArrayList<Molecule> configurableAtmosphereMolecules = new ArrayList<Molecule>();

    // Object that listens to molecules to see when they emit photons.
    private Molecule.Adapter moleculePhotonEmissionListener = new Molecule.Adapter(){
        public void photonEmitted(Photon photon) {
            photons.add( photon );
            notifyPhotonAdded( photon );
        }
    };
   
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    public PhotonAbsorptionModel(ConstantDtClock clock){
        
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
        
        // Remove any existing photons.
        ArrayList<Photon> copyOfPhotons = new ArrayList<Photon>(photons);
        photons.clear();
        for (Photon photon : copyOfPhotons){
            photons.remove( photon );
            notifyPhotonRemoved( photon );
        }
        
        // Reset all molecules, which will stop any oscillations.
        for (Molecule molecule : activeMolecules){
            molecule.reset();
        }

        setPhotonTarget( DEFAULT_PHOTON_TARGET );
        setEmittedPhotonWavelength( DEFAULT_EMITTED_PHOTON_WAVELENGTH );
        setPeriodicPhotonEmissionEnabled( false );
        
        resetConfigurableAtmosphere();
    }

    public void stepInTime(double dt){
        
        // Check if it is time to emit any photons.
        if (periodicPhotonEmissionEnabled){
            photonEmissionCountdownTimer -= dt;
            if (photonEmissionCountdownTimer <= 0){
                // Time to emit.
                emitPhoton();
                photonEmissionCountdownTimer = photonEmissionPeriod;
            }
        }
        
        // Step the photons, marking any that have moved beyond the model
        // bounds for removal.
        ArrayList<Photon> photonsToRemove = new ArrayList<Photon>();
        for (Photon photon : photons){
            photon.stepInTime( dt );
            if ( photon.getLocation().getX() - PHOTON_EMISSION_LOCATION.getX() > MAX_PHOTON_DISTANCE ){
                photonsToRemove.add( photon );
            }
            else{
                // See if any of the molecules wish to absorb this photon.
                for (Molecule molecule : activeMolecules){
                    if (molecule.queryAbsorbPhoton( photon )){
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
        for (Molecule molecule : activeMolecules){
            molecule.stepInTime( dt );
        }
    }

    /**
     * Turn on periodic emission of photons.
     * 
     * @param periodicPhotonEmissionEnabled
     */
    public void setPeriodicPhotonEmissionEnabled( boolean periodicPhotonEmissionEnabled ) {
        if (this.periodicPhotonEmissionEnabled != periodicPhotonEmissionEnabled){
            this.periodicPhotonEmissionEnabled = periodicPhotonEmissionEnabled;
            notifyPeriodicPhotonEmissionEnabledChanged();
            if (periodicPhotonEmissionEnabled == true){
                // Set the emission counter to zero so that one photon is
                // emitted right away.
                photonEmissionCountdownTimer = 0;
            }
        }
    }
    
    public boolean isPeriodicPhotonEmissionEnabled() {
        return periodicPhotonEmissionEnabled;
    }
    
    public void setPhotonTarget( PhotonTarget photonTarget ){
        if (this.photonTarget != photonTarget){
            
            // Remove the old photon target(s).
            ArrayList<Molecule> copyOfMolecules = new ArrayList<Molecule>( activeMolecules );
            activeMolecules.clear();
            for (Molecule molecule : copyOfMolecules){
                notifyMoleculeRemoved( molecule );
            }
            
            // Add the new photon target(s).
            Molecule newMolecule;
            switch (photonTarget){
            case SINGLE_CO2_MOLECULE:
                newMolecule = new CO2(SINGLE_MOLECULE_LOCATION);
                activeMolecules.add( newMolecule );
                break;
            case SINGLE_H2O_MOLECULE:
                newMolecule = new H2O(SINGLE_MOLECULE_LOCATION);
                activeMolecules.add( newMolecule );
                break;
            case SINGLE_CH4_MOLECULE:
                newMolecule = new CH4(SINGLE_MOLECULE_LOCATION);
                activeMolecules.add( newMolecule );
                break;
            case SINGLE_N2O_MOLECULE:
                newMolecule = new N2O(SINGLE_MOLECULE_LOCATION);
                activeMolecules.add( newMolecule );
                break;
            case SINGLE_N2_MOLECULE:
                newMolecule = new N2(SINGLE_MOLECULE_LOCATION);
                activeMolecules.add( newMolecule );
                break;
            case SINGLE_O2_MOLECULE:
                newMolecule = new O2(SINGLE_MOLECULE_LOCATION);
                activeMolecules.add( newMolecule );
                break;
            case CONFIGURABLE_ATMOSPHERE:
                // Add references for all the molecules in the configurable
                // atmosphere to the "active molecules" list.
                activeMolecules.addAll( configurableAtmosphereMolecules );
            default:
                System.err.println(getClass().getName() + " - Error: Unhandled molecule type.");
                break;
            }
            
            // Send out notifications about the new molecule(s);
            for (Molecule molecule : activeMolecules){
                molecule.addListener( moleculePhotonEmissionListener );
                notifyMoleculeAdded( molecule );
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
        return new ArrayList<Molecule>(activeMolecules);
    }
    
    /**
     * 
     * @return - Period between photons in milliseconds.
     */
    public double getPhotonEmissionPeriod() {
        return photonEmissionPeriod;
    }
    
    /**
     * Set the emission period, i.e. the time between photons.
     * 
     * @param photonEmissionPeriod - Period between photons in milliseconds.
     */
    public void setPhotonEmissionPeriod( double photonEmissionPeriod ) {
        this.photonEmissionPeriod = photonEmissionPeriod;
        if (isPeriodicPhotonEmissionEnabled() && photonEmissionCountdownTimer > photonEmissionPeriod){
            photonEmissionCountdownTimer = photonEmissionPeriod;
        }
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
    
    public void setEmittedPhotonWavelength(double freq){
        if (this.photonWavelength != freq){
            this.photonWavelength = freq;
            notifyEmittedPhotonWavelengthChanged();
        }
    }
    
    public double getEmittedPhotonWavelength(){
        return photonWavelength;
    }
    
    public void setConfigurableAtmosphereGasLevel(MoleculeID moleculeID, int targetQuantity){

        // Bounds checking.
        assert targetQuantity >= 0;
        if (targetQuantity < 0){
            System.err.println(getClass().getName() + " - Error: Invalid target quantity for gas level.");
            return;
        }
        else if (targetQuantity > MAX_ATMOSPHERE_CONCENTRATIONS.get( moleculeID )){
            System.err.println(getClass().getName() + " - Error: Target quantity of " + targetQuantity + 
                    "is out of range, limiting to " + MAX_ATMOSPHERE_CONCENTRATIONS.get( moleculeID ));
            targetQuantity = MAX_ATMOSPHERE_CONCENTRATIONS.get( moleculeID );
        }

        // Count the number of the specified type that currently exists. 
        int numMoleculesOfSpecifiedType = 0;
        for (Molecule molecule : configurableAtmosphereMolecules){
            if (molecule.getMoleculeID() == moleculeID){
                numMoleculesOfSpecifiedType++;
            }
        }
        
        // Calculate the difference.
        int numMoleculesToAdd = targetQuantity - numMoleculesOfSpecifiedType;
        
        // Make the changes.
        if (numMoleculesToAdd > 0){
            // Add the necessary number of the specified molecule.
            for (int i = 0; i < numMoleculesToAdd; i++){
                Molecule moleculeToAdd = Molecule.createMolecule( moleculeID );
                moleculeToAdd.setCenterOfGravityPos( findOpenLocationInAtmosphere() );
                configurableAtmosphereMolecules.add( moleculeToAdd );
            }
        }
        else if (numMoleculesToAdd < 0){
            // Remove the necessary number of the specified molecule.
            ArrayList<Molecule> moleculesToRemove = new ArrayList<Molecule>();
            for (Molecule molecule : configurableAtmosphereMolecules){
                if (molecule.getMoleculeID() == moleculeID){
                    moleculesToRemove.add( molecule );
                    if (moleculesToRemove.size() >= Math.abs( numMoleculesToAdd ) ){
                        break;
                    }
                }
            }
            configurableAtmosphereMolecules.removeAll( moleculesToRemove );
        }
        else{
            System.err.println(getClass().getName() + " - Warning: Ignoring call to set molecule levels to current level.");
        }
        
        // Send notifications of the change.
        if (numMoleculesToAdd != 0){
            notifyConfigurableAtmospherCompositionChanged();
        }
        
        // If the configurable atmosphere is the currently selected target,
        // then these changes must be synchronized with the active molecules.
        if (photonTarget == PhotonTarget.CONFIGURABLE_ATMOSPHERE){
            syncConfigAtmosphereToActiveMolecules();
        }
    }
    
    /**
     * Set the active molecules to match the list of molecules in the
     * configurable atmosphere list.  This is generally done when switching
     * the photon target to be the atmosphere or when the concentration of the
     * gases in the atmosphere changes while the configurable atmosphere is
     * the selected photon target.
     * 
     * The direction of data flow is from the config atmosphere to the active
     * molecules, not the reverse.
     * 
     * This routine takes care of sending out notifications of molecules
     * coming and/or going.
     */
    public void syncConfigAtmosphereToActiveMolecules(){
       // TODO. 
    }
    
    public void addListener(Listener listener){
        listeners.add(Listener.class, listener);
    }
    
    public void removeListener(Listener listener){
        listeners.remove(Listener.class, listener);
    }
    
    // Constants used when trying to find an open location in the atmosphere.
    private static final double MIN_DIST_FROM_WALL = 100; // In picometers.
    private static final double MIN_DIST_FROM_OTHER_MOLECULES = 100; // In picometers.
    private static final double MOLECULE_POS_MIN_X = CONTAINMENT_AREA_RECT.getMinX() + MIN_DIST_FROM_WALL;
    private static final double MOLECULE_POS_RANGE_X = CONTAINMENT_AREA_WIDTH - 2 * MIN_DIST_FROM_WALL;
    private static final double MOLECULE_POS_MIN_Y = CONTAINMENT_AREA_RECT.getMinY() + MIN_DIST_FROM_WALL;
    private static final double MOLECULE_POS_RANGE_Y = CONTAINMENT_AREA_HEIGHT - 2 * MIN_DIST_FROM_WALL;
    
    /**
     * Find an open location for a molecule.  This is assumed to be used only
     * when multiple molecules are being shown.
     * 
     * @return - A Point2D that is relatively free of other molecules.
     */
    private Point2D findOpenLocationInAtmosphere(){
        
        boolean openPosFound = false;
        Point2D openPosition = null;
        
        // Try to find an open position by random means.
        for (int i = 0; i < 100; i++){
            double proposedXPos = MOLECULE_POS_MIN_X + RAND.nextDouble() * MOLECULE_POS_RANGE_X;
            double proposedYPos = MOLECULE_POS_MIN_Y + RAND.nextDouble() * MOLECULE_POS_RANGE_Y;
            
            // TODO: Go with anything for now, finish later.
            openPosition = new Point2D.Double(proposedXPos, proposedYPos);
        }
        
        return openPosition;
    }
    
    /**
     * Reset the configurable atmosphere by adding the initial levels of all
     * gasses.
     * 
     * WARNING: This method is intended to be called during initialization of
     * the model and during resets.  It should NOT be called when the
     * configurable atmosphere is the selected photon target, or
     * inconsistencies between the model and view could result.
     */
    private void resetConfigurableAtmosphere(){
        
        assert photonTarget != PhotonTarget.CONFIGURABLE_ATMOSPHERE; // See method header comment if this assertion is hit.
        
        // Remove all existing molecules.
        configurableAtmosphereMolecules.clear();
    
        // Add the molecules.
        for (int i = 0; i < INITIAL_ATMOSPHERE_CONCENTRATIONS.get( MoleculeID.N2 ); i++){
            configurableAtmosphereMolecules.add( new N2(findOpenLocationInAtmosphere()) );
        }
        for (int i = 0; i < INITIAL_ATMOSPHERE_CONCENTRATIONS.get( MoleculeID.O2 ); i++){
            configurableAtmosphereMolecules.add( new O2(findOpenLocationInAtmosphere()) );
        }
        for (int i = 0; i < INITIAL_ATMOSPHERE_CONCENTRATIONS.get( MoleculeID.H2O ); i++){
            configurableAtmosphereMolecules.add( new H2O(findOpenLocationInAtmosphere()) );
        }
        for (int i = 0; i < INITIAL_ATMOSPHERE_CONCENTRATIONS.get( MoleculeID.CO2 ); i++){
            configurableAtmosphereMolecules.add( new CO2(findOpenLocationInAtmosphere()) );
        }
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
    
    private void notifyEmittedPhotonWavelengthChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.emittedPhotonWavelengthChanged();
        }
    }

    private void notifyPhotonTargetChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonTargetChanged();
        }
    }

    private void notifyPeriodicPhotonEmissionEnabledChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.periodicPhotonEmissionEnabledChanged();
        }
    }

    private void notifyConfigurableAtmospherCompositionChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.configurableAtmosphereCompositionChanged();
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
        void emittedPhotonWavelengthChanged();
        void photonTargetChanged();
        void periodicPhotonEmissionEnabledChanged();
        void configurableAtmosphereCompositionChanged();
    }
    
    public static class Adapter implements Listener {
        public void photonAdded( Photon photon ) {}
        public void emittedPhotonWavelengthChanged() {}
        public void photonRemoved( Photon photon ) {}
        public void photonTargetChanged() {}
        public void moleculeAdded( Molecule molecule ) {}
        public void moleculeRemoved( Molecule molecule ) {}
        public void periodicPhotonEmissionEnabledChanged() {}
        public void configurableAtmosphereCompositionChanged() {}
    }
}
