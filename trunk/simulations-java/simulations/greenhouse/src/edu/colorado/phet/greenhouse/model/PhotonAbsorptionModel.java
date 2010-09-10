/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
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
    
    // Constants that controls where and how photons are emitted.
    private static final Point2D PHOTON_EMISSION_LOCATION = new Point2D.Double(-1300, 0);
    private static final double PHOTON_EMISSION_ANGLE_RANGE = Math.PI/2;
    
    // Location used when a single molecule is sitting in the area where the
    // photons pass through.
    private static final Point2D SINGLE_MOLECULE_LOCATION = new Point2D.Double(0, 0);
    
    // Velocity of emitted photons.  Since they are emitted horizontally, only
    // one value is needed.
    private static final float PHOTON_VELOCITY = 2.0f;
    
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
        
    // Minimum and defaults for photon emission periods.  Note that the max is
    // assumed to be infinity.
    public static final double MIN_PHOTON_EMISSION_PERIOD_SINGLE_TARGET = 200;
    private static final double DEFAULT_PHOTON_EMISSION_PERIOD = Double.POSITIVE_INFINITY; // Milliseconds of sim time.
    public static final double MIN_PHOTON_EMISSION_PERIOD_MULTIPLE_TARGET = 100;
    
    // Default values for various parameters that weren't already covered.
    private static final PhotonTarget DEFAULT_PHOTON_TARGET = PhotonTarget.SINGLE_CH4_MOLECULE;
    private static final double DEFAULT_EMITTED_PHOTON_WAVELENGTH = GreenhouseConfig.irWavelength;
    
    // Initial and max values for the numbers of molecules in the configurable
    // atmosphere.
    static final HashMap< MoleculeID , Integer> INITIAL_ATMOSPHERE_CONCENTRATIONS = new HashMap< MoleculeID, Integer>() {{ 
        put(MoleculeID.N2, 0);
        put(MoleculeID.O2, 0);
        put(MoleculeID.CO2, 0);
        put(MoleculeID.CH4, 0);
        put(MoleculeID.H2O, 0);
    }}; 
    static final HashMap< MoleculeID , Integer> MAX_ATMOSPHERE_CONCENTRATIONS = new HashMap< MoleculeID, Integer>() {{ 
        put(MoleculeID.N2, 15); 
        put(MoleculeID.O2, 15); 
        put(MoleculeID.CO2, 15); 
        put(MoleculeID.CH4, 15); 
        put(MoleculeID.H2O, 15); 
    }};
    
    // Random number generator.
    private static final Random RAND = new Random();
    
    // Create a grid-based set of possible locations for molecules in the
    // configurable atmosphere.
    private static final ArrayList<Point2D> GRID_POINTS = new ArrayList<Point2D>();
    static {
        int numGridlinesX = 8;
        double gridLineSpacingX = CONTAINMENT_AREA_WIDTH / (double)(numGridlinesX + 1);
        int numGridlinesY = 8;
        double gridLineSpacingY = CONTAINMENT_AREA_WIDTH / (double)(numGridlinesY + 1);
        for (int i = 1; i <= numGridlinesX; i++){
            for (int j = 1; j <= numGridlinesY; j++){
                GRID_POINTS.add( new Point2D.Double(i * gridLineSpacingX + CONTAINMENT_AREA_RECT.getMinX(),
                        j * gridLineSpacingY + CONTAINMENT_AREA_RECT.getMinY()) );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private EventListenerList listeners = new EventListenerList();
    private ArrayList<Photon> photons = new ArrayList<Photon>();
    private double photonWavelength = GreenhouseConfig.sunlightWavelength;
    private final ArrayList<Molecule> activeMolecules = new ArrayList<Molecule>();
    private PhotonTarget photonTarget = null;
    
    // Variables that control periodic photon emission.
    private double photonEmissionCountdownTimer = Double.POSITIVE_INFINITY;
    private double photonEmissionPeriodTarget = DEFAULT_PHOTON_EMISSION_PERIOD;
    private double previousEmissionAngle = 0;
    
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

        // Set default values.
        setPhotonTarget( DEFAULT_PHOTON_TARGET );
        setEmittedPhotonWavelength( DEFAULT_EMITTED_PHOTON_WAVELENGTH );
        
        resetConfigurableAtmosphere();
    }

    public void stepInTime(double dt){
        
        // Check if it is time to emit any photons.
        if (photonEmissionCountdownTimer != Double.POSITIVE_INFINITY){
            photonEmissionCountdownTimer -= dt;
            if (photonEmissionCountdownTimer <= 0){
                // Time to emit.
                emitPhoton();
                photonEmissionCountdownTimer = photonEmissionPeriodTarget;
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

    // TODO: Part of an experiment, keep if needed.
    private ImmutableVector2D getNetForce(Molecule molecule) {
        ImmutableVector2D netForce= new Vector2D();
        for (Molecule activeMolecule : activeMolecules) {
            if (activeMolecule!=molecule){
            netForce = netForce.getAddedInstance(getForce(activeMolecule,molecule));
            }
        }

        //Left Wall
        double dxLeft = Math.abs(getContainmentAreaRect().getMinX() - molecule.getCenterOfGravityPos().getX());
        double dxRight = Math.abs(getContainmentAreaRect().getMaxX() - molecule.getCenterOfGravityPos().getX());
        double dyTop = Math.abs(getContainmentAreaRect().getMinY() - molecule.getCenterOfGravityPos().getY());
        double dyBottom = Math.abs(getContainmentAreaRect().getMaxY() - molecule.getCenterOfGravityPos().getY());
        netForce = netForce.getAddedInstance(new Vector2D(1.0 / dxLeft / dxLeft, 0));
        netForce = netForce.getAddedInstance(new Vector2D(-1.0 / dxRight/ dxRight, 0));
        netForce = netForce.getAddedInstance(new Vector2D(0,-1.0 / dyBottom/ dyBottom));
        netForce = netForce.getAddedInstance(new Vector2D(0,1.0 / dyTop/ dyTop));

        double dragForce = 0.01;
        netForce = netForce.getAddedInstance(molecule.getVelocity().getScaledInstance(-1*dragForce));

        return netForce;
    }

    private ImmutableVector2D getForce(Molecule source, Molecule target) {
        double distance = source.getCenterOfGravityPos().distance(target.getCenterOfGravityPos());
        Vector2D distanceVector = new Vector2D(source.getCenterOfGravityPos(), target.getCenterOfGravityPos()).normalize();
        return distanceVector.getScaledInstance(1.0 / distance / distance);
    }

    public void setPhotonTarget( PhotonTarget photonTarget ){
        if (this.photonTarget != photonTarget){

            // Update to the new value.
            this.photonTarget = photonTarget;
            
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
                break;
            default:
                System.err.println(getClass().getName() + " - Error: Unhandled photon target.");
                break;
            }
            
            // We use a higher emission speed for
            
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
        return photonEmissionPeriodTarget;
    }
    
    /**
     * Set the emission period, i.e. the time between photons.
     * 
     * @param photonEmissionPeriod - Period between photons in milliseconds.
     */
    public void setPhotonEmissionPeriod( double photonEmissionPeriod ) {
        assert photonEmissionPeriod >= 0;
        if (this.photonEmissionPeriodTarget != photonEmissionPeriod){
            this.photonEmissionPeriodTarget = photonEmissionPeriod;
            notifyPhotonEmissionPeriodChanged();
            // Handle the case where the new value is smaller than the current countdown.
            if (photonEmissionPeriod < photonEmissionCountdownTimer){
                photonEmissionCountdownTimer = photonEmissionPeriod;
            }
            // If the new value for the period is greater than the existing
            // countdown, we don't reset the counter so that the last one can
            // go ahead an finish UNLESS the new value is infinity, in which
            // case we also set the countdown to infinity, which essentially
            // turns off emissions.
            if (photonEmissionPeriod == Double.POSITIVE_INFINITY){
                photonEmissionCountdownTimer = photonEmissionPeriod; // Turn off emissions.
            }
        }
    }
    
    /**
     * Cause a photon to be emitted from the emission point.
     */
    public void emitPhoton(){
        
        Photon photon = new Photon( photonWavelength, null );
        photon.setLocation( PHOTON_EMISSION_LOCATION.getX(), PHOTON_EMISSION_LOCATION.getY() );
        double emissionAngle = 0; // Straight to the right.
        if (photonTarget == PhotonTarget.CONFIGURABLE_ATMOSPHERE){
            // Photons can be emitted at an angle.  In order to get a more
            // even spread, we alternate emitting with an up or down angle.
            emissionAngle = RAND.nextDouble() * PHOTON_EMISSION_ANGLE_RANGE / 2;
            if (previousEmissionAngle > 0){
                emissionAngle = -emissionAngle;
            }
            previousEmissionAngle = emissionAngle;
        }
        photon.setVelocity( (float)(PHOTON_VELOCITY * Math.cos( emissionAngle ) ),
                (float)(PHOTON_VELOCITY * Math.sin( emissionAngle ) ) );
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

    /**
     * Get the number of the specified molecule in the configurable atmosphere.
     * 
     * @param moleculeID
     * @return
     */
    public int getConfigurableAtmosphereGasLevel(MoleculeID moleculeID){
        int moleculeCount = 0;
        for (Molecule molecule : configurableAtmosphereMolecules){
            if (molecule.getMoleculeID() == moleculeID){
                moleculeCount++;
            }
        }
        return moleculeCount;
    }
    
    /**
     * Set the level of the specified gas in the configurable atmosphere.
     * 
     * @param moleculeID
     * @param targetQuantity
     */
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
                moleculeToAdd.setCenterOfGravityPos( findLocationInAtmosphereForMolecule2( moleculeToAdd ) );
                configurableAtmosphereMolecules.add( moleculeToAdd );
                moleculeToAdd.addListener( moleculePhotonEmissionListener );
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
            if (targetQuantity != 0){
                System.err.println(getClass().getName() + " - Warning: Ignoring call to set molecule levels to current level.");
            }
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
     * Get the number of the specified molecule in the configurable atmosphere.
     * 
     * @param moleculeID
     * @return
     */
    public int getConfigurableAtmosphereMaxLevel(MoleculeID moleculeID){
        if ( MAX_ATMOSPHERE_CONCENTRATIONS.containsKey( moleculeID )){
            return MAX_ATMOSPHERE_CONCENTRATIONS.get( moleculeID );
        }
        else{
            return 0;
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
        
        for (Molecule molecule : configurableAtmosphereMolecules){
            if (!activeMolecules.contains( molecule )){
                // This molecule is not on the active list, so it should be
                // added to it.
                activeMolecules.add( molecule );
                notifyMoleculeAdded( molecule );
            }
        }
        
        ArrayList<Molecule> moleculesToRemoveFromActiveList = new ArrayList<Molecule>();
        for (Molecule molecule : activeMolecules){
            if (!configurableAtmosphereMolecules.contains( molecule )){
                // This molecule is on the active list but NOT in the
                // configurable atmosphere, so it should be removed.
                moleculesToRemoveFromActiveList.add( molecule );
            }
        }
        activeMolecules.removeAll( moleculesToRemoveFromActiveList );
        for (Molecule molecule : moleculesToRemoveFromActiveList){
            notifyMoleculeRemoved( molecule );
        }
    }
    
    public void addListener(Listener listener){
        listeners.add(Listener.class, listener);
    }
    
    public void removeListener(Listener listener){
        listeners.remove(Listener.class, listener);
    }
    
    // Constants used when trying to find an open location in the atmosphere.
    private static final double MIN_DIST_FROM_WALL_X = 20; // In picometers.
    private static final double MIN_DIST_FROM_WALL_Y = 20; // In picometers.
    private static final double MOLECULE_POS_MIN_X = CONTAINMENT_AREA_RECT.getMinX() + MIN_DIST_FROM_WALL_X;
    private static final double MOLECULE_POS_RANGE_X = CONTAINMENT_AREA_WIDTH - 2 * MIN_DIST_FROM_WALL_X;
    private static final double MOLECULE_POS_MIN_Y = CONTAINMENT_AREA_RECT.getMinY() + MIN_DIST_FROM_WALL_Y;
    private static final double MOLECULE_POS_RANGE_Y = CONTAINMENT_AREA_HEIGHT - 2 * MIN_DIST_FROM_WALL_Y;
    private static final double EMITTER_AVOIDANCE_COMP_X = 300;
    private static final double EMITTER_AVOIDANCE_COMP_Y = 800;
    
    /**
     * Find a location in the atmosphere that has a minimal amount of overlap
     * with other molecules.  This is assumed to be used only when multiple
     * molecules are being shown.
     * 
     * @return - A Point2D that is relatively free of other molecules.
     */
    private Point2D findLocationInAtmosphereForMolecule( Molecule molecule ){
        
        // Generate a set of random location.
        ArrayList<Point2D> possibleLocations = new ArrayList<Point2D>();
        
        for (int i = 0; i < 100; i++){
            // Randomly generate a position.
            double proposedYPos = MOLECULE_POS_MIN_Y + RAND.nextDouble() * MOLECULE_POS_RANGE_Y;
            double minXPos = MOLECULE_POS_MIN_X;
            double xRange = MOLECULE_POS_RANGE_X;
            if (Math.abs( proposedYPos - getContainmentAreaRect().getCenterY() ) < EMITTER_AVOIDANCE_COMP_Y / 2){
                // Compensate in the X direction so that this position is not
                // too close to the photon emitter.
                minXPos = MOLECULE_POS_MIN_X + EMITTER_AVOIDANCE_COMP_X;
                xRange = MOLECULE_POS_RANGE_X - EMITTER_AVOIDANCE_COMP_X;
            }
            double proposedXPos = minXPos + RAND.nextDouble() * xRange;
            possibleLocations.add( new Point2D.Double(proposedXPos, proposedYPos ) );
        }
        
        // Figure out which point is furthest from all others.
        Collections.sort( possibleLocations, new Comparator<Point2D>() {
            public int compare( Point2D p1, Point2D p2 ) {
                return Double.compare( getMinDistanceToOtherMolecules(p1), getMinDistanceToOtherMolecules(p2) );
            }
        });
        
        return possibleLocations.get( possibleLocations.size() - 1 );
    }
    
    /**
     * Find a location in the atmosphere that has a minimal amount of overlap
     * with other molecules.  This is assumed to be used only when multiple
     * molecules are being shown.
     * 
     * IMPORTANT: This assumes that the molecule in question is not already on
     * the list of molecules, and may return weird results if it is.
     * 
     * @return - A Point2D that is relatively free of other molecules.
     */
    private Point2D findLocationInAtmosphereForMolecule2( Molecule molecule ){
        
        // Generate a set of random location.
        ArrayList<Point2D> possibleLocations = new ArrayList<Point2D>();
        
        double minDistWallToMolCenterX = MIN_DIST_FROM_WALL_X + molecule.getBoundingRect().getWidth() / 2;
        double minXPos = CONTAINMENT_AREA_RECT.getMinX() + minDistWallToMolCenterX;
        double xRange = CONTAINMENT_AREA_RECT.getWidth() - 2 * minDistWallToMolCenterX;
        double minDistWallToMolCenterY = MIN_DIST_FROM_WALL_Y + molecule.getBoundingRect().getHeight() / 2;
        double minYPos = CONTAINMENT_AREA_RECT.getMinY() + minDistWallToMolCenterY;
        double yRange = CONTAINMENT_AREA_RECT.getHeight() - 2 * minDistWallToMolCenterY;
        
        for (int i = 0; i < 20; i++){
            // Randomly generate a position.
            double proposedYPos = minYPos + RAND.nextDouble() * yRange;
            double proposedXPos;
            if (Math.abs( proposedYPos - getContainmentAreaRect().getCenterY() ) < EMITTER_AVOIDANCE_COMP_Y / 2){
                // Compensate in the X direction so that this position is not
                // too close to the photon emitter.
                proposedXPos = minXPos + EMITTER_AVOIDANCE_COMP_X + RAND.nextDouble() * (xRange - EMITTER_AVOIDANCE_COMP_X);
            }
            else{
                proposedXPos = minXPos + RAND.nextDouble() * xRange;
            }
            possibleLocations.add( new Point2D.Double(proposedXPos, proposedYPos ) );
        }
        
        final double molRectWidth = molecule.getBoundingRect().getWidth();
        final double molRectHeight = molecule.getBoundingRect().getHeight();
        
        // Figure out which point would position the molecule such that it had
        // the least overlap with other molecules.
        Collections.sort( possibleLocations, new Comparator<Point2D>() {
            public int compare( Point2D p1, Point2D p2 ) {
                return Double.compare( getOverlapWithOtherMolecules(p1, molRectWidth, molRectHeight),
                        getOverlapWithOtherMolecules(p2, molRectWidth, molRectHeight) );
            }
        });
        
        Point2D pt = possibleLocations.get( 0 );
        if (pt.getX() + molRectWidth / 2 > CONTAINMENT_AREA_RECT.getMaxX()){
            System.out.println("Whoa! " + pt);
        }
        
        return possibleLocations.get( 0 );
    }
    
    /**
     * Find a location in the atmosphere that has a minimal amount of overlap
     * with other molecules.  This is assumed to be used only when multiple
     * molecules are being shown.
     * 
     * IMPORTANT: This assumes that the molecule in question is not already on
     * the list of molecules, and may return weird results if it is.
     * 
     * @return - A Point2D that is relatively free of other molecules.
     */
    private Point2D findLocationInAtmosphereForMolecule3( Molecule molecule ){
        
        ArrayList<Point2D> possibleLocations = new ArrayList<Point2D>();
        
        double minDistWallToMolCenterX = MIN_DIST_FROM_WALL_X + molecule.getBoundingRect().getWidth() / 2;
        double minXPos = CONTAINMENT_AREA_RECT.getMinX() + minDistWallToMolCenterX;
        double xRange = CONTAINMENT_AREA_RECT.getWidth() - 2 * minDistWallToMolCenterX;
        double minDistWallToMolCenterY = MIN_DIST_FROM_WALL_Y + molecule.getBoundingRect().getHeight() / 2;
        double minYPos = CONTAINMENT_AREA_RECT.getMinY() + minDistWallToMolCenterY;
        double yRange = CONTAINMENT_AREA_RECT.getHeight() - 2 * minDistWallToMolCenterY;
        
        for (int i = 0; i < 20; i++){
            // Randomly generate a position.
            double proposedYPos = minYPos + RAND.nextDouble() * yRange;
            double proposedXPos;
            if (Math.abs( proposedYPos - getContainmentAreaRect().getCenterY() ) < EMITTER_AVOIDANCE_COMP_Y / 2){
                // Compensate in the X direction so that this position is not
                // too close to the photon emitter.
                proposedXPos = minXPos + EMITTER_AVOIDANCE_COMP_X + RAND.nextDouble() * (xRange - EMITTER_AVOIDANCE_COMP_X);
            }
            else{
                proposedXPos = minXPos + RAND.nextDouble() * xRange;
            }
            possibleLocations.add( new Point2D.Double(proposedXPos, proposedYPos ) );
        }

        // Add some pre-computed points to this list of possibilities in case
        // none of the random points are workable.
        possibleLocations.addAll( GRID_POINTS );
        
        // Figure out which point would position the molecule such that it had
        // the least overlap with other molecules.
        final double molRectWidth = molecule.getBoundingRect().getWidth();
        final double molRectHeight = molecule.getBoundingRect().getHeight();
        Collections.sort( possibleLocations, new Comparator<Point2D>() {
            public int compare( Point2D p1, Point2D p2 ) {
                return Double.compare( getOverlapWithOtherMolecules(p1, molRectWidth, molRectHeight),
                        getOverlapWithOtherMolecules(p2, molRectWidth, molRectHeight) );
            }
        });
        
        Point2D pt = possibleLocations.get( 0 );
        if (pt.getX() + molRectWidth / 2 > CONTAINMENT_AREA_RECT.getMaxX()){
            System.out.println("Whoa! " + pt);
        }
        
        return possibleLocations.get( 0 );
    }
    
    /**
     * Convenience method for creating a rectangle from a center point.
     * 
     * @param pt
     * @param width
     * @param height
     * @return
     */
    private Rectangle2D createRectangleFromPoint(Point2D pt, double width, double height){
       return new Rectangle2D.Double(pt.getX() - width / 2, pt.getY() - height / 2, width, height); 
    }
    
    private double getMinDistanceToOtherMolecules( Point2D o1 ) {
        double minDistance = Double.POSITIVE_INFINITY;
        for (Molecule molecule : configurableAtmosphereMolecules){
            if (molecule.getCenterOfGravityPos().distance( o1 ) < minDistance){
                minDistance = molecule.getCenterOfGravityPos().distance( o1 );
            }
        }
        return minDistance;
    }
    
    private double getOverlapWithOtherMolecules( Point2D pt, double width, double height ){
        double overlap = 0;
        Rectangle2D testRect = createRectangleFromPoint( pt, width, height );
        for (Molecule molecule : configurableAtmosphereMolecules){
            // Add in the overlap for each molecule.  There may well be no
            // overlap.
            overlap += Math.max( molecule.getBoundingRect().createIntersection( testRect ).getWidth(), 0 ) * 
                Math.max(molecule.getBoundingRect().createIntersection( testRect ).getHeight(), 0 );
        }
        if (overlap == 0){
            // This point has no overlap.  Add some "bonus points" for the
            // amount of distance from all other points.  The reason that this
            // is negative is that 0 is the least overlap that can occur, so
            // it is even better if it is a long way from any other molecules.
            overlap = -getMinDistanceToOtherMolecules( pt );
        }
        return overlap;
    }
    
    private void setConfigurableAtmosphereInitialLevel(MoleculeID moleculeID){
        if ( INITIAL_ATMOSPHERE_CONCENTRATIONS.containsKey( moleculeID )){
            setConfigurableAtmosphereGasLevel( moleculeID, INITIAL_ATMOSPHERE_CONCENTRATIONS.get( moleculeID  ) );
        }
    }
    
    /**
     * Reset the configurable atmosphere by adding the initial levels of all
     * gasses.
     */
    private void resetConfigurableAtmosphere(){
        
        assert photonTarget != PhotonTarget.CONFIGURABLE_ATMOSPHERE; // See method header comment if this assertion is hit.
        
        setConfigurableAtmosphereInitialLevel( MoleculeID.N2);
        setConfigurableAtmosphereInitialLevel( MoleculeID.O2);
        setConfigurableAtmosphereInitialLevel( MoleculeID.CO2);
        setConfigurableAtmosphereInitialLevel( MoleculeID.H2O);
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

    private void notifyPhotonEmissionPeriodChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonEmissionPeriodChanged();
        }
    }

    private void notifyPhotonTargetChanged() {
        for (Listener listener : listeners.getListeners(Listener.class)){
            listener.photonTargetChanged();
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
    
    private class Segment1D {
        private double min, max;

        public Segment1D( double min, double max ) {
            assert max >= min;
            this.min = min;
            this.max = max;
        }

        /**
         * @return
         */
        public double getLength() {
            return max - min;
        }

        public double getMin() {
            return min;
        }
        
        public double getMax() {
            return max;
        }
        
        public void setSize(double min, double max) {
            assert max >= min;
            this.min = min;
            this.max = max;
        }
        
        /**
         * Subtract the supplied segment from this segment.  In general, this
         * can have four different outcomes:
         * 1. If there is no overlap between this segment and the passed-in
         * segment, this segment will remain unchanged.
         * 2. If the passed-in segment overlaps with one end of this segment,
         * this segment will be reduced in length.
         * 3. If the passed-in segment is larger than and completely overlaps
         * this segment, the size of this segment will be reduced to zero.
         * 4. If the passed-in segment is smaller than this segment and is
         * completely contained within it, this segment will essentially be
         * split into two smaller segments.  In this case, one of the
         * segments will be returned and this segment will take on the size
         * of the other.  This is the only case where the return value is non-
         * null.
         *
         * @param segmentToSubtract
         * @return
         */
        public Segment1D subtractSegment(Segment1D segmentToSubtract){
            
            Segment1D returnSegment = null;
            
            double overlapMin = Math.max( this.min, segmentToSubtract.getMin() );
            double overlapMax = Math.min( this.max, segmentToSubtract.getMax() );

            if (overlapMax > overlapMin){
                if (overlapMin == this.min && overlapMax == this.max){
                    // This segment is completely contained within the passed-
                    // in segment, so subtracting the passed-in segment
                    // essentially obliterates this one.
                    setSize( 0, 0 );
                }
                else if (overlapMin == segmentToSubtract.getMin() && overlapMax == segmentToSubtract.getMax()){
                    // The passed-in segment is completely contained within
                    // this segment.  In this case, this segment must be split
                    // in two, and one of the resulting segments is returned.
                    returnSegment = new Segment1D( this.min, overlapMin );
                    this.min = overlapMax;
                }
                else{
                    // The passed in segment partially overlaps this segment.
                    if (this.min < overlapMin){
                        this.max = overlapMin;
                    }
                    else{
                        this.min = overlapMin;
                    }
                }
            }
            else{
                // No overlap, so no change to this segment and no split-off
                // portion to return.
            }
            
            return returnSegment;
            
        }
    }
    
    public interface Listener extends EventListener {
        void photonAdded(Photon photon);
        void photonRemoved(Photon photon);
        void moleculeAdded(Molecule molecule);
        void moleculeRemoved(Molecule molecule);
        void emittedPhotonWavelengthChanged();
        void photonTargetChanged();
        void photonEmissionPeriodChanged();
        void configurableAtmosphereCompositionChanged();
    }
    
    public static class Adapter implements Listener {
        public void photonAdded( Photon photon ) {}
        public void emittedPhotonWavelengthChanged() {}
        public void photonRemoved( Photon photon ) {}
        public void photonTargetChanged() {}
        public void moleculeAdded( Molecule molecule ) {}
        public void moleculeRemoved( Molecule molecule ) {}
        public void configurableAtmosphereCompositionChanged() {}
        public void photonEmissionPeriodChanged() {}
    }
}
