/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.engine.AbstractPhaseStateChanger;
import edu.colorado.phet.statesofmatter.model.engine.AtomPositionUpdater;
import edu.colorado.phet.statesofmatter.model.engine.EngineFacade;
import edu.colorado.phet.statesofmatter.model.engine.MoleculeForceAndMotionCalculator;
import edu.colorado.phet.statesofmatter.model.engine.MonatomicAtomPositionUpdater;
import edu.colorado.phet.statesofmatter.model.engine.MonatomicPhaseStateChanger;
import edu.colorado.phet.statesofmatter.model.engine.MonatomicVerletAlgorithm;
import edu.colorado.phet.statesofmatter.model.engine.PhaseStateChanger;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyAdjuster;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyCapper;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * This is the main class for the model portion of the "States of Matter"
 * simulation.  It maintains a set of data that represents a normalized model
 * in which all atoms are assumed to have a diameter of 1, since this allows
 * for very quick calculations, and also a set of data for particles that have
 * the actual diameter of the particles being simulated (e.g. Argon).
 * Throughout the comments and in the variable naming, I've tried to use the
 * terminology of "normalized data set" (or sometimes simply "normalized
 * set" for the former and "model data set" for the latter.  When the
 * simulation is running, the molecule data set is updated first, since that
 * is where the hardcore calculations are performed, and then the model data
 * set is synchronized with the molecule data.  It is the model data set that
 * is monitored by the view components that actually displays the molecule
 * positions to the user. 
 *
 * @author John Blanco
 */
public class MultipleParticleModel2 extends AbstractMultipleParticleModel {
    
	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
	// Minimum container height fraction.
    public static final double MIN_CONTAINER_HEIGHT_FRACTION = 0.1;

    // TODO: JPB TBD - These constants are here as a result of the first attempt
    // to integrate Paul Beale's IDL implementation of the Verlet algorithm.
    // Eventually some or all of them will be moved.
    public static final int     DEFAULT_MOLECULE = StatesOfMatterConstants.NEON;
    private static final double DISTANCE_BETWEEN_DIATOMIC_PAIRS = 0.8;  // In particle diameters.
    private static final double TIME_STEP = 0.020;  // Time per simulation clock tick, in seconds.
    private static final double INITIAL_TEMPERATURE = 0.2;
    public static final double  MAX_TEMPERATURE = 50.0;
    public static final double  MIN_TEMPERATURE = 0.0001;
    private static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    private static final double INITIAL_GRAVITATIONAL_ACCEL = 0.045;
    public static final double  MAX_GRAVITATIONAL_ACCEL = 0.4;
    private static final double MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT = 0.025;
    private static final int    TICKS_PER_TEMP_ADJUSTEMENT = 10;
    private static final double MIN_INJECTED_MOLECULE_VELOCITY = 0.5;
    private static final double MAX_INJECTED_MOLECULE_VELOCITY = 2.0;
    private static final double MAX_INJECTED_MOLECULE_ANGLE = Math.PI * 0.8;
    private static final int    MAX_PLACEMENT_ATTEMPTS = 500; // For random placement when creating gas or liquid.
    private static final double SAFE_INTER_MOLECULE_DISTANCE = 2.0;
    private static final double PRESSURE_DECAY_CALCULATION_WEIGHTING = 0.999;
    private static final int    VERLET_CALCULATIONS_PER_CLOCK_TICK = 8;

    // Constants used for setting the phase directly.
    public static final int PHASE_SOLID = 1;
    public static final int PHASE_LIQUID = 2;
    public static final int PHASE_GAS = 3;
    private static final double MIN_INITIAL_INTER_PARTICLE_DISTANCE = 1.2;
    private static final double MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE = 2.5;
    private static final double INJECTION_POINT_HORIZ_PROPORTION = 0.95;
    private static final double INJECTION_POINT_VERT_PROPORTION = 0.5;
	
    // Possible thermostat settings.
    public static final int NO_THERMOSTAT = 0;
    public static final int ISOKINETIC_THERMOSTAT = 1;
    public static final int ANDERSEN_THERMOSTAT = 2;
    public static final int ADAPTIVE_THERMOSTAT = 3;   // Adaptive uses isokinetic when temperature is changing and
                                                       // Andersen when temperature is stable.  This is done because
                                                       // it provides the most visually appealing behavior.
    
    // Parameters to control rates of change of the container size.
    private static final double MAX_PER_TICK_CONTAINER_SHRINKAGE = 50;
    private static final double MAX_PER_TICK_CONTAINER_EXPANSION = 200;
    
    // Countdown value used when recalculating temperature when the
    // container size is changing.
    private static final int CONTAINER_SIZE_CHANGE_RESET_COUNT = 25;
    
    // Range for deciding if the temperature is near the current set point.
    // The units are internal model units.
    private static final double TEMPERATURE_CLOSENESS_RANGE = 0.15;

    // Constant used to limit how close the atoms are allowed to get to one
    // another so that we don't end up getting crazy big forces.
    private static final double MIN_DISTANCE_SQUARED = 0.7225;

    // Parameters used for "hollywooding" of the water crystal.
    private static final double WATER_FULLY_MELTED_TEMPERATURE = 0.3;
    private static final double WATER_FULLY_MELTED_ELECTROSTATIC_FORCE = 1.0;
    private static final double WATER_FULLY_FROZEN_TEMPERATURE = 0.22;
    private static final double WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE = 4.0;
    
    // Parameters that control the increasing of gravity as the temperature
    // approaches zero.  This is done to counteract the tendency of the
    // thermostat to slow falling molecules noticably at low temps.  This is
    // a "hollywooding" thing.
    private static final double TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES = 0.10;
    private static final double LOW_TEMPERATURE_GRAVITY_INCREASE_RATE = 50;
    
    // Values used for converting from model temperature to the temperature
    // for a given particle.
    public static final double TRIPLE_POINT_MODEL_TEMPERATURE = 0.4;    // Empirically determined.
    public static final double CRITICAL_POINT_MODEL_TEMPERATURE = 0.8;  // Empirically determined.
    private static final double NEON_TRIPLE_POINT_IN_KELVIN = 25;
    private static final double NEON_CRITICAL_POINT_IN_KELVIN = 44;
    private static final double ARGON_TRIPLE_POINT_IN_KELVIN = 84;
    private static final double ARGON_CRITICAL_POINT_IN_KELVIN = 151;
    private static final double O2_TRIPLE_POINT_IN_KELVIN = 54;
    private static final double O2_CRITICAL_POINT_IN_KELVIN = 155;
    private static final double WATER_TRIPLE_POINT_IN_KELVIN = 273;
    private static final double WATER_CRITICAL_POINT_IN_KELVIN = 647;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    // Strategy patterns that are applied to the data set in order to create
    // the overall behavior of the simulation.
    private AtomPositionUpdater m_atomPositionUpdater;
    private MoleculeForceAndMotionCalculator m_moleculeForceAndMotionCalculator;
    private PhaseStateChanger m_phaseStateChanger;
    
    // Attributes of the container and simulation as a whole.
    private double m_particleContainerHeight;
    private double m_targetContainerHeight;
    private double m_minAllowableContainerHeight;
    private final List m_particles = new ArrayList();
    private double m_totalEnergy;
    private boolean m_lidBlownOff = false;
    private EngineFacade m_engineFacade;
    IClock m_clock;
    private ArrayList _listeners = new ArrayList();
    
    // Data set containing the atom and molecule position, motion, and force information.
    private MoleculeForceAndMotionDataSet m_moleculeDataSet;

    private double  m_normalizedContainerWidth;
    private double  m_normalizedContainerHeight;
    private double  m_potentialEnergy;
    private Random  m_rand = new Random();
    private double  m_temperatureSetPoint;
    private double  m_gravitationalAcceleration;
    private double  m_heatingCoolingAmount;
    private int     m_tempAdjustTickCounter;
    private int     m_currentMolecule;
    private double  m_particleDiameter;
    private double m_pressure;
    private int     m_thermostatType;
    private int     m_heightChangeCounter;
    private double  m_minModelTemperature;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public MultipleParticleModel2(IClock clock) {
        
        m_clock = clock;
        m_heightChangeCounter = 0;
        setThermostatType( ADAPTIVE_THERMOSTAT );
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                resetAll();
            }
        });
        
        // Set the default particle type.
        setMoleculeType( DEFAULT_MOLECULE );
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public IClock getClock(){
        return m_clock;
    }

    public List getParticles() {
        return Collections.unmodifiableList(m_particles);
    }

    public int getNumMolecules() {
        return m_particles.size() / m_moleculeDataSet.getAtomsPerMolecule();
    }

    public StatesOfMatterAtom getParticle(int i) {
        return (StatesOfMatterAtom)m_particles.get(i);
    }

    /**
     * Get a rectangle that represents the current size and position of the
     * the particle container.
     */
    public Rectangle2D getParticleContainerRect() {
        return new Rectangle2D.Double(0, 0, StatesOfMatterConstants.PARTICLE_CONTAINER_WIDTH, m_particleContainerHeight);
    }

    public synchronized double getKineticEnergy() {
        return m_engineFacade.measureKineticEnergy();
    }

    public synchronized double getPotentialEnergy() {
        return m_engineFacade.measurePotentialEnergy();
    }

    public synchronized double getTotalEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }
    
    public void setTemperature(double newTemperature){
        if (newTemperature > MAX_TEMPERATURE) {
            m_temperatureSetPoint = MAX_TEMPERATURE;
        }
        else if (newTemperature < MIN_TEMPERATURE){
            m_temperatureSetPoint = MIN_TEMPERATURE;
        }
        else{
            m_temperatureSetPoint = newTemperature;
        }

        notifyTemperatureChanged();
    }

    /**
     * Returns the value of the internal model temperature, which is generally
     * only meaningful to the model.
     */
    public double getModelTemperature(){
        return m_temperatureSetPoint;
    }
    
    /**
     * Get the current temperature set point in model units.
     * @return
     */
    public double getTemperatureSetPoint(){
        return m_temperatureSetPoint;
    }
    
    /**
     * Get the current temperature in degrees Kelvin.
     * @return
     */
    public double getTemperatureInKelvin(){
        return convertInternalTemperatureToKelvin();
    }
    
    public double getGravitationalAcceleration() {
        return m_gravitationalAcceleration;
    }

    public void setGravitationalAcceleration( double acceleration ) {
        if (acceleration > MAX_GRAVITATIONAL_ACCEL){
            System.err.println("WARNING: Attempt to set out-of-range value for gravitational acceleration.");
            assert false;
            m_gravitationalAcceleration = MAX_GRAVITATIONAL_ACCEL;
        }
        else if (acceleration < 0){
            System.err.println("WARNING: Attempt to set out-of-range value for gravitational acceleration.");
            assert false;
            m_gravitationalAcceleration = 0;
        }
        else{
            m_gravitationalAcceleration = acceleration;
        }
    }
    
    public int getParticleType(){
        return m_currentMolecule;
    }
    
    /**
     * Get the pressure value which is being calculated by the model and is
     * not adjusted to represent any "real" units (such as atmospheres).
     * 
     * @return
     */
    public double getModelPressure(){
        return m_moleculeForceAndMotionCalculator.getPressure();
    }
    
    public int getMoleculeType(){
        return m_currentMolecule;
    }
    
    public boolean getContainerExploded(){
    	return m_lidBlownOff;
    }
    
    /**
     * Set the molecule type to be simulated.
     * 
     * @param moleculeID
     */
    public void setMoleculeType(int moleculeID){
        
        // Verify that this is a supported value.
        if ((moleculeID != StatesOfMatterConstants.DIATOMIC_OXYGEN) &&
            (moleculeID != StatesOfMatterConstants.NEON) &&
            (moleculeID != StatesOfMatterConstants.ARGON) &&
            (moleculeID != StatesOfMatterConstants.WATER)){
            
            System.err.println("ERROR: Unsupported molecule type.");
            assert false;
            moleculeID = StatesOfMatterConstants.NEON;
        }
        
        m_currentMolecule = moleculeID;
        
        // Set the model parameters that are dependent upon the model type.
        int atomsPerMolecule;
        switch (m_currentMolecule){
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            m_particleDiameter = OxygenAtom.RADIUS * 2;
            atomsPerMolecule = 2;
            m_minModelTemperature = 0.5 * TRIPLE_POINT_MODEL_TEMPERATURE / O2_TRIPLE_POINT_IN_KELVIN;
            break;
        case StatesOfMatterConstants.NEON:
            m_particleDiameter = NeonAtom.RADIUS * 2;
            atomsPerMolecule = 1;
            m_minModelTemperature = 0.5 * TRIPLE_POINT_MODEL_TEMPERATURE / NEON_TRIPLE_POINT_IN_KELVIN;
            break;
        case StatesOfMatterConstants.ARGON:
            m_particleDiameter = ArgonAtom.RADIUS * 2;
            atomsPerMolecule = 1;
            m_minModelTemperature = 0.5 * TRIPLE_POINT_MODEL_TEMPERATURE / ARGON_TRIPLE_POINT_IN_KELVIN;
            break;
        case StatesOfMatterConstants.WATER:
            // Use a radius value that is artificially large, because the
            // educators have requested that water look "spaced out" so that
            // users can see the crystal structure better, and so that the
            // solid form will look larger (since water expands when frozen).
            m_particleDiameter = OxygenAtom.RADIUS * 2.9;
            atomsPerMolecule = 3;
            m_minModelTemperature = 0.5 * TRIPLE_POINT_MODEL_TEMPERATURE / WATER_TRIPLE_POINT_IN_KELVIN;
            break;
        default:
        	assert false; // Should never happen, so it should be debugged if it does.
        	atomsPerMolecule = 1;
        }

        // Create the data set that will represent the motion and forces for the molecules.
        m_moleculeDataSet = new MoleculeForceAndMotionDataSet( atomsPerMolecule );
        
        // Initiate a reset in order to get the particles into predetermined
        // locations and energy levels.
        resetParticles();
        
        // Notify listeners that the molecule type has changed.
        notifyMoleculeTypeChanged();
    }
    
    public int getThermostatType() {
        return m_thermostatType;
    }

    
    public void setThermostatType( int type ) {
        if ((type == NO_THERMOSTAT) ||
            (type == ISOKINETIC_THERMOSTAT) ||
            (type == ANDERSEN_THERMOSTAT) ||
            (type == ADAPTIVE_THERMOSTAT))
        {
            m_thermostatType = type;
        }
        else{
            throw new IllegalArgumentException( "Thermostat type setting out of range: " + type );
        }
    }
    
    public double getNormalizedContainerWidth() {
        return m_normalizedContainerWidth;
    }
    
    public double getNormalizedContainerHeight() {
        return m_normalizedContainerHeight;
    }
    
    public double getParticleContainerHeight() {
        return m_particleContainerHeight;
    }

    /**
     * Sets the target height of the container.  The target height is set
     * rather than the actual height because the model limits the rate at
     * which the height can changed.  The model will gradually move towards
     * the target height.
     * 
     * @param desiredContainerHeight
     */
    public void setTargetParticleContainerHeight( double desiredContainerHeight ) {
        
        if ((desiredContainerHeight <= StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT) &&
            (desiredContainerHeight > m_minAllowableContainerHeight)){
            // This is a valid value.
            m_targetContainerHeight = desiredContainerHeight;
        }
    }

    /**
     * Get the sigma value, which is one of the two parameters that describes
     * the Lennard-Jones potential.
     * 
     * @return
     */
    public double getSigma(){
        
        double sigma = 0;
        
        switch ( m_currentMolecule ){
        
        case StatesOfMatterConstants.NEON:
            sigma = NeonAtom.getSigma();
            break;
        
        case StatesOfMatterConstants.ARGON:
            sigma = ArgonAtom.getSigma();
            break;
        
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            sigma = StatesOfMatterConstants.SIGMA_FOR_DIATOMIC_OXYGEN;
            break;
        
        case StatesOfMatterConstants.MONATOMIC_OXYGEN:
            sigma = OxygenAtom.getSigma();
            break;
        
        case StatesOfMatterConstants.WATER:
            sigma = StatesOfMatterConstants.SIGMA_FOR_WATER;
            break;
            
        default:
            System.err.println("Error: Unrecognized molecule type when setting sigma value.");
            sigma = 0;
        }
        
        return sigma;
    }
    
    /**
     * Get the epsilon value, which is one of the two parameters that describes
     * the Lennard-Jones potential.
     * 
     * @return
     */
    public double getEpsilon(){
        
        double epsilon = 0;
        
        switch ( m_currentMolecule ){
        
        case StatesOfMatterConstants.NEON:
            epsilon = NeonAtom.getEpsilon();
            break;
        
        case StatesOfMatterConstants.ARGON:
            epsilon = ArgonAtom.getEpsilon();
            break;
        
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            epsilon = StatesOfMatterConstants.EPSILON_FOR_DIATOMIC_OXYGEN;
            break;
        
        case StatesOfMatterConstants.MONATOMIC_OXYGEN:
            epsilon = OxygenAtom.getEpsilon();
            break;
        
        case StatesOfMatterConstants.WATER:
            epsilon = StatesOfMatterConstants.EPSILON_FOR_WATER;
            break;
            
        default:
            System.err.println("Error: Unrecognized molecule type when setting epsilon value.");
            epsilon = 0;
        }
        
        return epsilon;
    }
    
    public MoleculeForceAndMotionDataSet getMoleculeDataSetRef(){
    	return m_moleculeDataSet;
    }

    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the particles (be they atoms or molecules) by getting rid of all
     * existing ones, creating the default number of the current type, and
     * putting them in their initial positions.
     */
    private void resetParticles() {
        
        // Get rid of any existing particles from the model set.
        for ( Iterator iter = m_particles.iterator(); iter.hasNext(); ) {
            StatesOfMatterAtom particle = (StatesOfMatterAtom) iter.next();
            // Tell the particle that it is being removed so that it can do
            // any necessary cleanup.
            particle.removedFromModel();
        }
        m_particles.clear();

        // Initialize the system parameters.
        m_lidBlownOff = false;
        m_gravitationalAcceleration = INITIAL_GRAVITATIONAL_ACCEL;
        m_heatingCoolingAmount = 0;
        m_tempAdjustTickCounter = 0;
        if (m_temperatureSetPoint != INITIAL_TEMPERATURE){
            m_temperatureSetPoint = INITIAL_TEMPERATURE;
            notifyTemperatureChanged();
        }
        
        // Set the initial size of the container.
        m_particleContainerHeight = StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT;
        m_targetContainerHeight = StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT;
        m_normalizedContainerHeight = m_particleContainerHeight / m_particleDiameter;
        m_normalizedContainerWidth = StatesOfMatterConstants.PARTICLE_CONTAINER_WIDTH / m_particleDiameter;
        
        // Initialize the particles.
        switch (m_currentMolecule){
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            initializeDiatomic(m_currentMolecule);
            break;
        case StatesOfMatterConstants.NEON:
            initializeMonotomic(m_currentMolecule);
            break;
        case StatesOfMatterConstants.ARGON:
            initializeMonotomic(m_currentMolecule);
            break;
        case StatesOfMatterConstants.WATER:
            initializeTriatomic(m_currentMolecule);
            break;
        default:
            System.err.println("ERROR: Unrecognized particle type, using default.");
            break;
        }
        
        calculateMinAllowableContainerHeight();

        // Let any listeners know that things have changed.
        notifyContainerSizeChanged();
        notifyResetOccurred();
    }
    
    public void resetAll(){
    	setMoleculeType( DEFAULT_MOLECULE );
    }

    /**
     * Calculate the minimum allowable container height based on the current
     * number of particles.
     */
    private void calculateMinAllowableContainerHeight() {
        m_minAllowableContainerHeight = m_particleDiameter * m_particleDiameter * 
            m_moleculeDataSet.getNumberOfMolecules() / StatesOfMatterConstants.PARTICLE_CONTAINER_WIDTH * 2;
    }
    
    /**
     * Set the phase of the particles in the simulation.
     * 
     * @param state
     */
    public void setPhase(int state){
    	
        switch (state){
        case PHASE_SOLID:
        	m_phaseStateChanger.setPhase(PhaseStateChanger.PHASE_SOLID);
            break;
            
        case PHASE_LIQUID:
        	m_phaseStateChanger.setPhase(PhaseStateChanger.PHASE_LIQUID);
            break;
            
        case PHASE_GAS:
        	m_phaseStateChanger.setPhase(PhaseStateChanger.PHASE_GAS);
            break;
            
        default:
            System.err.println("Error: Invalid state specified.");
            // Treat is as a solid.
        	m_phaseStateChanger.setPhase(PhaseStateChanger.PHASE_SOLID);
            break;
        }
    }
    
    /**
     * Sets the amount of heating or cooling that the system is undergoing.
     * 
     * @param normalizedHeatingCoolingAmount - Normalized amount of heating or cooling
     * that the system is undergoing, ranging from -1 to +1.
     */
    public void setHeatingCoolingAmount(double normalizedHeatingCoolingAmount){
        assert (normalizedHeatingCoolingAmount <= 1.0) && (normalizedHeatingCoolingAmount >= -1.0);
        
        m_heatingCoolingAmount = normalizedHeatingCoolingAmount * MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT;
    }
    
    /**
     * Inject a new molecule of the current type into the model.  This uses
     * the current temperature to assign an initial velocity.
     */
    public void injectMolecule(){
        
        double injectionPointX = StatesOfMatterConstants.CONTAINER_BOUNDS.width / m_particleDiameter * 
        	INJECTION_POINT_HORIZ_PROPORTION;
        double injectionPointY = StatesOfMatterConstants.CONTAINER_BOUNDS.height / m_particleDiameter *
        	INJECTION_POINT_VERT_PROPORTION;

        // Make sure that it is okay to inject a new molecule.
        if (( m_moleculeDataSet.getNumberOfRemainingSlots() > 1 ) &&
            ( m_normalizedContainerHeight > injectionPointY * 1.05) &&
            (!m_lidBlownOff)){

            double angle = Math.PI + ((m_rand.nextDouble() - 0.5) * MAX_INJECTED_MOLECULE_ANGLE);
            double velocity = MIN_INJECTED_MOLECULE_VELOCITY + (m_rand.nextDouble() *
                    (MAX_INJECTED_MOLECULE_VELOCITY - MIN_INJECTED_MOLECULE_VELOCITY));
            double xVel = Math.cos( angle ) * velocity;
            double yVel = Math.sin( angle ) * velocity;
            int atomsPerMolecule = m_moleculeDataSet.getAtomsPerMolecule();
        	Point2D moleculeCenterOfMassPosition = new Point2D.Double( injectionPointX, injectionPointY );
        	Vector2D.Double moleculeVelocity = new Vector2D.Double( xVel, yVel );
        	double moleculeRotationRate = (m_rand.nextDouble() - 0.5) * (Math.PI / 2);;
        	Point2D [] atomPositions = new Point2D[atomsPerMolecule];
        	for (int i = 0; i < atomsPerMolecule; i++){
        		atomPositions[i] = new Point2D.Double();
        	}

            // Position the atom positions.
            m_atomPositionUpdater.updateAtomPositions(m_moleculeDataSet);

            // Add the newly created molecule to the data set.
        	m_moleculeDataSet.addMolecule(atomPositions, moleculeCenterOfMassPosition, moleculeVelocity, moleculeRotationRate);
        	
            if (m_moleculeDataSet.getAtomsPerMolecule() == 1){
                
                // Add particle to model set.
            	// TODO: JPB TBD - Consider making this part of the process of syncing particles, i.e. if the
            	// number of particles in the data set don't match those in the model set, just add or delete
            	// the appropriate number to/from the model set.
                StatesOfMatterAtom particle;
                switch (m_currentMolecule){
                case StatesOfMatterConstants.ARGON:
                    particle = new ArgonAtom(0, 0);
                    break;
                case StatesOfMatterConstants.NEON:
                    particle = new NeonAtom(0, 0);
                    break;
                default:
                    particle = new StatesOfMatterAtom(0, 0, m_particleDiameter/2, 10);
                    break;
                }
                m_particles.add( particle );
                syncParticlePositions();
                notifyParticleAdded( particle );
            }
            else if (m_moleculeDataSet.getAtomsPerMolecule() == 2){

                assert m_currentMolecule == StatesOfMatterConstants.DIATOMIC_OXYGEN;
                
                // Add particles to model set.
                for (int i = 0; i < atomsPerMolecule; i++){
                    StatesOfMatterAtom atom;
                    atom = new OxygenAtom(0, 0);
                    m_particles.add( atom );
                    notifyParticleAdded( atom );
                    atomPositions[i] = new Point2D.Double(); 
                }
            }
            else if (atomsPerMolecule == 3){

                assert m_currentMolecule == StatesOfMatterConstants.WATER;
                
                // Add atoms to model set.
                StatesOfMatterAtom atom;
                atom = new OxygenAtom(0, 0);
                m_particles.add( atom );
                notifyParticleAdded( atom );
                atomPositions[0] = new Point2D.Double(); 
                atom = new HydrogenAtom(0, 0);
                m_particles.add( atom );
                notifyParticleAdded( atom );
                atomPositions[1] = new Point2D.Double(); 
                atom = new HydrogenAtom(0, 0);
                m_particles.add( atom );
                notifyParticleAdded( atom );
                atomPositions[2] = new Point2D.Double(); 
            }
            
            syncParticlePositions();
        }

        // Recalculate the minimum allowable container size, since it depends on the number of particles.
        calculateMinAllowableContainerHeight();
    }
    
    public void addListener(Listener listener){
        
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public boolean removeListener(Listener listener){
        return _listeners.remove( listener );
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    /**
     * @param clockEvent
     */
    private void handleClockTicked(ClockEvent clockEvent) {
        
        if (!m_lidBlownOff) {
            // Adjust the particle container height if needed.
            if ( m_targetContainerHeight != m_particleContainerHeight ){
                m_heightChangeCounter = CONTAINER_SIZE_CHANGE_RESET_COUNT;
                double heightChange = m_targetContainerHeight - m_particleContainerHeight;
                if (heightChange > 0){
                    // The container is growing.
                    if (m_particleContainerHeight + heightChange <= StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT){
                        m_particleContainerHeight += Math.min( heightChange, MAX_PER_TICK_CONTAINER_EXPANSION );
                    }
                    else{
                        m_particleContainerHeight = StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT;
                    }
                }
                else{
                    // The container is shrinking.
                    if (m_particleContainerHeight - heightChange >= m_minAllowableContainerHeight){
                        m_particleContainerHeight += Math.max( heightChange, -MAX_PER_TICK_CONTAINER_SHRINKAGE );
                    }
                    else{
                        m_particleContainerHeight = m_minAllowableContainerHeight;
                    }
                }
                m_normalizedContainerHeight = m_particleContainerHeight / m_particleDiameter;
                notifyContainerSizeChanged();
            }
            else {
                if (m_heightChangeCounter > 0) {
                    m_heightChangeCounter--;
                }
            }
        }
        else {
            // The lid is blowing off the container, so increase the container
            // size until the lid should be well off the screen.
            if (m_particleContainerHeight < StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT * 10) {
                m_particleContainerHeight += MAX_PER_TICK_CONTAINER_EXPANSION;
                notifyContainerSizeChanged();
            }
            
            // Decrease the pressure quickly as the particles escape.
            m_pressure = m_pressure * 0.85;
        }
        
        // Record the pressure to see if it changes.
        double pressureBeforeAlgorithm = getModelPressure();

        // Execute the Verlet algorithm.  The algorithm may be run several times
        // for each time step.
        for (int i = 0; i < VERLET_CALCULATIONS_PER_CLOCK_TICK; i++ ){
        	m_moleculeForceAndMotionCalculator.updateForcesAndMotion();
        }
        
        // Sync up the positions of the normalized particles (the molecule data
        // set) with the particles being monitored by the view (the model data
        // set);
        syncParticlePositions();
        
        // If the pressure changed, notify the listeners.
        if ( getModelPressure() != pressureBeforeAlgorithm ){
        	notifyPressureChanged();
        }
        
        // Adjust the temperature if needed.
        m_tempAdjustTickCounter++;
        if ((m_tempAdjustTickCounter > TICKS_PER_TEMP_ADJUSTEMENT) && m_heatingCoolingAmount != 0){
            m_tempAdjustTickCounter = 0;
            double newTemperature = m_temperatureSetPoint + m_heatingCoolingAmount;
            if (newTemperature >= MAX_TEMPERATURE){
                newTemperature = MAX_TEMPERATURE;
            }
            else if ((newTemperature <= AbstractPhaseStateChanger.SOLID_TEMPERATURE * 0.9) && (m_heatingCoolingAmount < 0)){
            	// The temperature goes down more slowly as we begin to
            	// approach absolute zero.
            	newTemperature = m_temperatureSetPoint * 0.95;  // Multiplier determined empirically.
            }
            else if (newTemperature <= m_minModelTemperature){
                newTemperature = m_minModelTemperature;
            }
            m_temperatureSetPoint = newTemperature;
            /*
             * TODO JPB TBD - This code causes temperature to decrease towards but
             * not reach absolute zero.  A decision was made on 10/7/2008 to
             * allow decreasing all the way to zero, so this is being removed.
             * It should be taken out permanently after a couple of months if
             * the decision stands.
            else if (m_temperatureSetPoint <= LOW_TEMPERATURE){
            	// Below a certain threshold temperature decreases assymtotically
            	// towards absolute zero without actually reaching it.
                m_temperatureSetPoint = (m_temperatureSetPoint - m_heatingCoolingAmount) * 0.95;
            }
            */
            notifyTemperatureChanged();
        }
    }
    
    // TODO: JPB TBD - At end of refactoring effort, evaluation whether it makes more sense to have initializer classes.
    /**
     * Initialize the various model components to handle a simulation in which
     * all the molecules are single atoms.
     * 
     * @param moleculeID
     */
    private void initializeMonotomic(int moleculeID){
        
        // Verify that a valid molecule ID was provided.
        assert (moleculeID == StatesOfMatterConstants.NEON) || 
               (moleculeID == StatesOfMatterConstants.ARGON);
        
        // Determine the number of atoms/molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container, so the number of
        // molecules that can fit depends on the size of the individual.
        double particleDiameter;
        if (moleculeID == StatesOfMatterConstants.NEON){
            particleDiameter = NeonAtom.RADIUS * 2;
        }
        else if (moleculeID == StatesOfMatterConstants.ARGON){
            particleDiameter = ArgonAtom.RADIUS * 2;
        }
        else if (moleculeID == StatesOfMatterConstants.MONATOMIC_OXYGEN){
            particleDiameter = OxygenAtom.RADIUS * 2;
        }
        else{
            // Force it to neon.
            moleculeID = StatesOfMatterConstants.NEON;
            particleDiameter = NeonAtom.RADIUS * 2;
        }
        
        // Initialize the number of atoms assuming that the solid form, when
        // made into a square, will consume about 1/3 the width of the
        // container.
        int numberOfAtoms = (int)Math.pow( StatesOfMatterConstants.CONTAINER_BOUNDS.width / 
                (( particleDiameter * 1.1 ) * 3), 2);
        
        // Create the normalized data set for the one-atom-per-molecule case.
        m_moleculeDataSet = new MoleculeForceAndMotionDataSet( 1 );
        
        // Create the strategies that will work on this data set.
        // TODO: JPB TBD - Add all the strategy pattern creation here.
        m_phaseStateChanger = new MonatomicPhaseStateChanger( this );
        m_atomPositionUpdater = new MonatomicAtomPositionUpdater();
        m_moleculeForceAndMotionCalculator = new MonatomicVerletAlgorithm( m_moleculeDataSet );
        
        // Create the individual atoms and add them to the data set.
        for (int i = 0; i < numberOfAtoms; i++){
            
            // Create the atom.
        	Point2D moleculeCenterOfMassPosition = new Point2D.Double();
        	Vector2D.Double moleculeVelocity = new Vector2D.Double();
        	Point2D [] atomPositions = new Point2D[1];
    		atomPositions[0] = new Point2D.Double();
    		
    		// Add the atom to the data set.
    		m_moleculeDataSet.addMolecule(atomPositions, moleculeCenterOfMassPosition, moleculeVelocity, 0);

            // Add particle to model set.
            StatesOfMatterAtom atom;
            if (moleculeID == StatesOfMatterConstants.NEON){
                atom = new NeonAtom(0, 0);
            }
            else if (moleculeID == StatesOfMatterConstants.ARGON){
                atom = new ArgonAtom(0, 0);
            }
            else{
                atom = new OxygenAtom(0, 0);
            }
            m_particles.add( atom );
            notifyParticleAdded( atom );
        }

        // Initialize the particle positions into a solid form.
        m_phaseStateChanger.setPhase( PhaseStateChanger.PHASE_SOLID );
        syncParticlePositions();
    }

    /**
     * Initialize the various model components to handle a simulation in which
     * each molecule consists of two atoms, e.g. oxygen.
     * 
     * @param moleculeID
     */
    private void initializeDiatomic(int moleculeID){
        
        // Verify that a valid molecule ID was provided.
        assert (moleculeID == StatesOfMatterConstants.DIATOMIC_OXYGEN);
        
        /*
         * TODO: JPB TBD - Commented out for this portion of the refactoring effort.
        
        // Determine the number of molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container.
        double largeMoleculeDimension = 1.8;
        int numberOfMoleculesAcross = 
            (int)Math.floor( m_normalizedContainerWidth / 3 / largeMoleculeDimension );
        m_numberOfAtoms = numberOfMoleculesAcross * numberOfMoleculesAcross * 4;
        m_numberOfSafeAtoms = m_numberOfAtoms;
        
        // Initialize the arrays that define the normalized attributes for
        // each individual atom.
        m_atomPositions  = new Point2D [MAX_NUM_ATOMS];
        m_moleculeVelocities = new Vector2D [MAX_NUM_ATOMS];
        m_moleculeForces     = new Vector2D [MAX_NUM_ATOMS];
        m_nextMoleculeForces = new Vector2D [MAX_NUM_ATOMS];
        
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[i] = new Point2D.Double();
            m_moleculeVelocities[i] = new Vector2D.Double();
            m_moleculeForces[i] = new Vector2D.Double();
            m_nextMoleculeForces[i] = new Vector2D.Double();
            
            // Add particle to model set.
            StatesOfMatterAtom atom;
            atom = new OxygenAtom(0, 0);
            m_particles.add( atom );
            notifyParticleAdded( atom );
        }
        
        // Initialize the arrays that define the normalized attributes for
        // each molecule.
        m_atomsPerMolecule = 2;
        m_moleculeCenterOfMassPositions = new Point2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeVelocities = new Vector2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeForces = new Vector2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_nextMoleculeForces = new Vector2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeRotationAngles = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeRotationRates = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeTorques = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_nextMoleculeTorques = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        
        // Initialize the rotational inertia.
        double a = 0.9;
        m_x0[0] = a/2;
        m_y0[0] = 0;
        m_x0[1] = -a/2;
        m_y0[1] = 0;
        m_moleculeRotationalInertia = 0;
        for (int i=0; i < 2; i++){
            m_moleculeRotationalInertia += Math.pow( m_x0[i], 2 ) + Math.pow(  m_y0[i], 2 );
        }
        
        m_moleculeMass = 2; // Because there are two atoms per molecule.
        
        int numberOfMolecules = m_numberOfAtoms / 2;
        
        for (int i = 0; i < numberOfMolecules; i++){
            m_moleculeCenterOfMassPositions[i] = new Point2D.Double();
            m_moleculeVelocities[i] = new Vector2D.Double();
            m_moleculeForces[i] = new Vector2D.Double();
            m_nextMoleculeForces[i] = new Vector2D.Double();
        }
        
        // Initialize the particle positions.
        solidifyDiatomicMolecules();
        syncParticlePositions();
         */
    }

    /**
     * Initialize the various model components to handle a simulation in which
     * each molecule consists of three atoms, e.g. water.
     * 
     * @param moleculeID
     */
    private void initializeTriatomic(int moleculeID){
        
        // Verify that a valid molecule ID was provided.
        assert (moleculeID == StatesOfMatterConstants.WATER); // Only water is supported so far.

        /*
         * TODO: JPB TBD - Commented out for this portion of the refactoring effort.

        // Determine the number of molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container.
        double particleDiameter;
        particleDiameter = OxygenAtom.RADIUS * 2.5; // TODO: This is just a guess, not sure if it's right.
       
        m_numberOfAtoms = (int)Math.pow( StatesOfMatterConstants.CONTAINER_BOUNDS.width / 
                (( particleDiameter + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL ) * 3), 2) * 3;
        
        // TODO: JPB TBD - The following value is used for the cool hexagon demo thing.
        // m_numberOfAtoms = 360;
        
        m_numberOfSafeAtoms = m_numberOfAtoms;
        
        // Initialize the arrays that define the normalized attributes for
        // each individual atom.
        m_atomPositions  = new Point2D [MAX_NUM_ATOMS];
        m_moleculeVelocities = new Vector2D [MAX_NUM_ATOMS];
        m_moleculeForces     = new Vector2D [MAX_NUM_ATOMS];
        m_nextMoleculeForces = new Vector2D [MAX_NUM_ATOMS];
        
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[i] = new Point2D.Double();
            m_moleculeVelocities[i] = new Vector2D.Double();
            m_moleculeForces[i] = new Vector2D.Double();
            m_nextMoleculeForces[i] = new Vector2D.Double();
            
            // Add particle to model set.
            StatesOfMatterAtom atom;
            if (i % 3 == 0){
                atom = new OxygenAtom(0, 0);
            }
            else {
                // Add a hydrogen atom.
                if ((i+1) % 6 == 0){
                    // TODO: JPB TBD - This is a temp test to distinguish
                    // charged hydrogen from uncharged hydrogen.
                    atom = new HydrogenAtom2(0, 0);
                }
                else{
                    atom = new HydrogenAtom(0, 0);
                }
            }
            m_particles.add( atom );
            notifyParticleAdded( atom );
        }
        
        // Initialize the arrays that define the normalized attributes for
        // each molecule.
        m_atomsPerMolecule = 3;
        m_moleculeCenterOfMassPositions = new Point2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeVelocities = new Vector2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeForces = new Vector2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_nextMoleculeForces = new Vector2D [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeRotationAngles = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeRotationRates = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeTorques = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_nextMoleculeTorques = new double [MAX_NUM_ATOMS / m_atomsPerMolecule];
        
        // JPB TBD - The following initializations are taken directly from
        // Paul Beale's code, and I need some clarification on exactly what
        // they do.
        double aOH = 1.0 / 3.12;
//        double thetaHOH = 109.47122*Math.PI/180;  ---> Original value that Paul used.
        double thetaHOH = 120*Math.PI/180;  // Tweaked value for hollywooding.  JPB TBD - Explain if used.
        m_x0[0] = 0;
        m_y0[0] = 0;
        m_x0[1] = aOH;
        m_y0[1] = 0;
        m_x0[2] = aOH*Math.cos( thetaHOH );
        m_y0[2] = aOH*Math.sin( thetaHOH );
        double xcm0 = (m_x0[0]+0.25*m_x0[1]+0.25*m_x0[2])/1.5;
        double ycm0 = (m_y0[0]+0.25*m_y0[1]+0.25*m_y0[2])/1.5;
        for (int i = 0; i < 3; i++){
            m_x0[i] -= xcm0;
            m_y0[i] -= ycm0;
        }
        
        for (int i = 0; i < MAX_NUM_ATOMS / m_atomsPerMolecule; i++){
            m_moleculeCenterOfMassPositions[i] = new Point2D.Double();
            m_moleculeVelocities[i] = new Vector2D.Double();
            m_moleculeForces[i] = new Vector2D.Double();
            m_nextMoleculeForces[i] = new Vector2D.Double();
        }
        
        m_moleculeMass = 1.5;
        m_moleculeRotationalInertia = 1.0*(Math.pow(m_x0[0],2)+Math.pow(m_y0[0],2))+0.25*(Math.pow(m_x0[1],2)+Math.pow(m_y0[1],2))+0.25*(Math.pow(m_x0[2],2)+Math.pow(m_y0[2],2));
        
        // Initialize the particle positions.
        solidifyTriatomicMolecules();
        syncParticlePositions();
        
        */
    }
    
    private void notifyResetOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).resetOccurred();
        }        
    }

    private void notifyParticleAdded(StatesOfMatterAtom particle){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).particleAdded( particle );
        }        
    }

    private void notifyTemperatureChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).temperatureChanged();
        }        
    }

    private void notifyPressureChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).pressureChanged();
        }        
    }

    private void notifyContainerSizeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).containerSizeChanged();
        }        
    }

    private void notifyMoleculeTypeChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).moleculeTypeChanged();
        }        
    }

    private void notifyContainerExploded(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).containerExploded();
        }        
    }

    private void conserveTotalEnergy() {
        double curKE = m_engineFacade.measureKineticEnergy();
        double curTotalEnergy = curKE + m_engineFacade.measurePotentialEnergy();

        double energyDiff = curTotalEnergy - m_totalEnergy;

        double targetKE = curKE - energyDiff;

        if (targetKE > 0) {
            new KineticEnergyAdjuster().adjust(m_particles, targetKE);
        }
    }

    private void capKineticEnergy() {
        new KineticEnergyCapper(m_particles).cap(StatesOfMatterConstants.PARTICLE_MAX_KE);
    }
    
    /**
     * Randomize the positions of the particles within the container and give
     * them velocity equivalent to that of a gas.
     */
    private void gasifyMonatomicMolecules(){
    	
        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.
    	
        setTemperature( GAS_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        for (int i = 0; i < m_numberOfAtoms; i++){
            // Temporarily position the particles at (0,0).
            m_atomPositions[i].setLocation( 0, 0 );
            
            // Assign each particle an initial velocity.
            m_moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
                    temperatureSqrt * rand.nextGaussian() );
        }
        
        // Redistribute the particles randomly around the container, but make
        // sure that they are not too close together or they end up with a
        // disproportionate amount of kinetic energy.
        double newPosX, newPosY;
        double rangeX = m_normalizedContainerWidth - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        double rangeY = m_normalizedContainerHeight - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        for (int i = 0; i < m_numberOfAtoms; i++){
            for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++){
                // Pick a random position.
                newPosX = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (rand.nextDouble() * rangeX);
                newPosY = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (rand.nextDouble() * rangeY);
                boolean positionAvailable = true;
                // See if this position is available.
                for (int k = 0; k < i; k++){
                    if (m_atomPositions[k].distance( newPosX, newPosY ) < MIN_INITIAL_INTER_PARTICLE_DISTANCE){
                        positionAvailable = false;
                        break;
                    }
                }
                if (positionAvailable){
                    // We found an open position.
                    m_atomPositions[i].setLocation( newPosX, newPosY );
                    break;
                }
                else if (j == MAX_PLACEMENT_ATTEMPTS - 1){
                    // This is the last attempt, so use this position anyway.
                    m_atomPositions[i].setLocation( newPosX, newPosY );
                }
            }
        }
        syncParticlePositions();
        */
    }
    
    /**
     * Randomize the positions of the molecules that comprise a gas.  This
     * works for diatomic and triatomic molecules.
     */
    private void gasifyMultiAtomicMolecules(){
        
        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.

        setTemperature( GAS_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        int numberOfMolecules = m_numberOfAtoms / m_atomsPerMolecule;
        
        for (int i = 0; i < numberOfMolecules; i++){
            // Temporarily position the molecules at (0,0).
            m_moleculeCenterOfMassPositions[i].setLocation( 0, 0 );
            
            // Assign each molecule an initial velocity.
            m_moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
                    temperatureSqrt * rand.nextGaussian() );
            
            // Assign each molecule an initial rotational position.
            m_moleculeRotationAngles[i] = rand.nextDouble() * Math.PI * 2;

            // Assign each molecule an initial rotation rate.
            // TODO: JPB TBD - Check with Paul if this is a reasonable way to do this.
            m_moleculeRotationRates[i] = rand.nextDouble() * temperatureSqrt * Math.PI * 2;
        }
        
        // Redistribute the molecules randomly around the container, but make
        // sure that they are not too close together or they end up with a
        // disproportionate amount of kinetic energy.
        double newPosX, newPosY;
        double rangeX = m_normalizedContainerWidth - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        double rangeY = m_normalizedContainerHeight - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        for (int i = 0; i < numberOfMolecules; i++){
            for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++){
                // Pick a random position.
                newPosX = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (rand.nextDouble() * rangeX);
                newPosY = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (rand.nextDouble() * rangeY);
                boolean positionAvailable = true;
                // See if this position is available.
                for (int k = 0; k < i; k++){
                    if (m_moleculeCenterOfMassPositions[k].distance( newPosX, newPosY ) < MIN_INITIAL_INTER_PARTICLE_DISTANCE * 1.5){
                        positionAvailable = false;
                        break;
                    }
                }
                if (positionAvailable){
                    // We found an open position.
                    m_moleculeCenterOfMassPositions[i].setLocation( newPosX, newPosY );
                    break;
                }
                else if (j == MAX_PLACEMENT_ATTEMPTS - 1){
                    // This is the last attempt, so use this position anyway.
                    Point2D openPoint = findOpenMoleculeLocation();
                    if (openPoint != null){
                        m_moleculeCenterOfMassPositions[i].setLocation( openPoint );
                    }
                }
            }
        }
        
        // Move the atoms to correspond to the molecule positions.
        if (m_atomsPerMolecule == 2){
            updateDiatomicAtomPositions();
        }
        else{
            // Must be triatomic.
            updateTriatomicAtomPositions();
        }
        
        // Sync up with the model-view interaction particles.
        syncParticlePositions();
        
        */
    }

    /**
     * Set the atoms to be in a liquid state.
     */
    private void liquifyMonatomicMolecules(){
        
        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.

    	
    	setTemperature( LIQUID_TEMPERATURE );
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        
        // Set the initial velocity for each of the atoms based on the new
        // temperature.

        for (int i = 0; i < m_numberOfAtoms; i++){
            // Assign each particle an initial velocity.
            m_moleculeVelocities[i].setComponents( temperatureSqrt * m_rand.nextGaussian(), 
                    temperatureSqrt * m_rand.nextGaussian() );
        }
        
        // Assign each atom to a position centered on its blob.
        
        int atomsPlaced = 0;
        
        Point2D centerPoint = new Point2D.Double(m_normalizedContainerWidth / 2, m_normalizedContainerHeight / 4);
        int currentLayer = 0;
        int particlesOnCurrentLayer = 0;
        int particlesThatWillFitOnCurrentLayer = 1;
        
        for (int j = 0; j < m_numberOfAtoms; j++){
            
            for (int k = 0; k < MAX_PLACEMENT_ATTEMPTS; k++){
                
                double distanceFromCenter = currentLayer * MIN_INITIAL_INTER_PARTICLE_DISTANCE;
                double angle = ((double)particlesOnCurrentLayer / (double)particlesThatWillFitOnCurrentLayer * 2 * Math.PI) +
                        ((double)particlesThatWillFitOnCurrentLayer / (4 * Math.PI));
                double xPos = centerPoint.getX() + (distanceFromCenter * Math.cos( angle ));
                double yPos = centerPoint.getY() + (distanceFromCenter * Math.sin( angle ));
                particlesOnCurrentLayer++;  // Consider this spot used even if we don't actually put the
                                            // particle there.
                if (particlesOnCurrentLayer >= particlesThatWillFitOnCurrentLayer){
                    
                    // This layer is full - move to the next one.
                    currentLayer++;
                    particlesThatWillFitOnCurrentLayer = 
                        (int)( currentLayer * 2 * Math.PI / MIN_INITIAL_INTER_PARTICLE_DISTANCE );
                    particlesOnCurrentLayer = 0;
                }

                // Check if the position is too close to the wall.  Note
                // that we don't check inter-particle distances here - we
                // rely on the placement algorithm to make sure that we don't
                // run into problems with this.
                if ((xPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_normalizedContainerWidth - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (yPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_normalizedContainerHeight - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE)){
                    
                    // This is an acceptable position.
                    m_atomPositions[atomsPlaced++].setLocation( xPos, yPos );
                    break;
                }
            }
        }

        // Synchronize the normalized particles with the particles monitored
        // by the model.
        syncParticlePositions();
        
        */
    }

    /**
     * Set the molecules to be in a liquid state.  This changes the
     * temperature and also instantly places the various molecules into
     * a blob.  This method works for di- and tri-atomic molecules.
     */
    private void liquifyMultiAtomicMolecules(){
     
        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.

        setTemperature( LIQUID_TEMPERATURE );
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        int numberOfMolecules = m_numberOfAtoms / m_atomsPerMolecule;

        // Initialize the velocities and angles of the molecules.
        
        for (int i = 0; i < numberOfMolecules; i++){

            // Assign each molecule an initial velocity.
            m_moleculeVelocities[i].setComponents( temperatureSqrt * m_rand.nextGaussian(), 
                    temperatureSqrt * m_rand.nextGaussian() );
            
            // Assign each molecule an initial rotation rate.
            // TODO: JPB TBD - Check with Paul if this is a reasonable way to do this.
            m_moleculeRotationRates[i] = m_rand.nextDouble() * temperatureSqrt * Math.PI * 2;
        }
        
        // Assign each atom to a position.
        
        int moleculesPlaced = 0;
        
        // Note: Due to the shape of the molecules, it is difficult if not
        // impossible to come up with an algorithm that works for all 
        // multi-atomic cases, so the following "tweak factor" was introduced.
        // The values were arrived at empirically.
        double tweakFactor = 1;
        if (m_atomsPerMolecule == 2){
            tweakFactor = 1.2;
        }
        else if (m_currentMolecule == StatesOfMatterConstants.WATER){
            tweakFactor = 0.9;
        }
        
        Point2D centerPoint = new Point2D.Double(m_normalizedContainerWidth / 2, m_normalizedContainerHeight / 4);
        int currentLayer = 0;
        int particlesOnCurrentLayer = 0;
        int particlesThatWillFitOnCurrentLayer = 1;
        
        for (int i = 0; i < m_numberOfAtoms / m_atomsPerMolecule; i++){
            
            for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++){
                
                double distanceFromCenter = currentLayer * MIN_INITIAL_INTER_PARTICLE_DISTANCE * tweakFactor;
                double angle = ((double)particlesOnCurrentLayer / (double)particlesThatWillFitOnCurrentLayer * 2 * Math.PI) +
                        ((double)particlesThatWillFitOnCurrentLayer / (4 * Math.PI));
                double xPos = centerPoint.getX() + (distanceFromCenter * Math.cos( angle ));
                double yPos = centerPoint.getY() + (distanceFromCenter * Math.sin( angle ));
                particlesOnCurrentLayer++;  // Consider this spot used even if we don't actually put the
                                            // particle there.
                if (particlesOnCurrentLayer >= particlesThatWillFitOnCurrentLayer){
                    
                    // This layer is full - move to the next one.
                    currentLayer++;
                    particlesThatWillFitOnCurrentLayer = 
                        (int)( currentLayer * 2 * Math.PI / (MIN_INITIAL_INTER_PARTICLE_DISTANCE * tweakFactor) );
                    particlesOnCurrentLayer = 0;
                }

                // Check if the position is too close to the wall.  Note
                // that we don't check inter-particle distances here - we rely
                // on the placement algorithm to make sure that this is not a
                // problem.
                if ((xPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_normalizedContainerWidth - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (yPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_normalizedContainerHeight - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE)){
                    
                    // This is an acceptable position.
                    m_moleculeCenterOfMassPositions[moleculesPlaced].setLocation( xPos, yPos );
                    m_moleculeRotationAngles[moleculesPlaced] = angle + Math.PI / 2;
                    moleculesPlaced++;
                    break;
                }
            }
        }
    
        // Move the atoms to correspond to the molecule positions.
        if (m_atomsPerMolecule == 2){
            updateDiatomicAtomPositions();
        }
        else{
            // Must be triatomic.
            updateTriatomicAtomPositions();
        }

        // Synchronize the normalized particles with the particles monitored
        // by the model.
        syncParticlePositions();
        
        */
    }

    /**
     * Create positions corresponding to a hexagonal 2d "crystal" structure
     * for a set of particles.  Note that this assumes a normalized value
     * of 1.0 for the diameter of the particles.
     */
    private void solidifyMonatomicMolecules(){

        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.

        setTemperature( SOLID_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        int particlesPerLayer = (int)Math.round( Math.sqrt( m_numberOfAtoms ) );
        if ((m_atomsPerMolecule == 2) && (particlesPerLayer % 2 != 0)){
            // We must have an even number of particles per layer if the
            // molecules need to be diatomic or we will run into problems.
            particlesPerLayer++;
        }
        double startingPosX = (m_normalizedContainerWidth / 2) - (double)(particlesPerLayer / 2) - 
                ((particlesPerLayer / 2) * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
        double startingPosY = 2.0 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL;
        
        int particlesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; particlesPlaced < m_numberOfAtoms; i++){ // One iteration per layer.
            for (int j = 0; (j < particlesPerLayer) && (particlesPlaced < m_numberOfAtoms); j++){
                xPos = startingPosX + j + (j * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL) / 2;
                }
                yPos = startingPosY + (double)i * (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL)* 0.7071;
                m_atomPositions[(i * particlesPerLayer) + j].setLocation( xPos, yPos );
                particlesPlaced++;

                // Assign each particle an initial velocity.
                m_moleculeVelocities[(i * particlesPerLayer) + j].setComponents( temperatureSqrt * rand.nextGaussian(), 
                        temperatureSqrt * rand.nextGaussian() );
            }
        }
        
        */
    }
    
    /**
     * Create positions for a solid composed of diatomic molecules,
     * such as diatomic oxygen (i.e. O2).
     */
    private void solidifyDiatomicMolecules(){

        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.

        // Make sure we are really running in diatomic mode.
        assert m_atomsPerMolecule == 2;
        
        setTemperature( SOLID_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        int moleculesPerLayer = (int)Math.round( Math.sqrt( m_numberOfAtoms ) / 2 );
        
        double startingPosX = (m_normalizedContainerWidth / 2) - 
            ((double)moleculesPerLayer + (((double)moleculesPerLayer / 2 - 1) * DISTANCE_BETWEEN_DIATOMIC_PAIRS / 2));
        double startingPosY = 2.0;
        
        // Place the molecules by placing their centers of mass.
        
        int moleculesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; moleculesPlaced < m_numberOfAtoms / 2; i++){ // One iteration per layer.
            for (int j = 0; (j < moleculesPerLayer) && (moleculesPlaced < m_numberOfAtoms / 2); j++){
                xPos = startingPosX + (j * 2) + DISTANCE_BETWEEN_DIATOMIC_PAIRS;
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL) / 2;
                }
                yPos = startingPosY + (double)i + DISTANCE_BETWEEN_DIATOMIC_PAIRS;
                m_moleculeCenterOfMassPositions[(i * moleculesPerLayer) + j].setLocation( xPos, yPos );
                m_moleculeRotationAngles[(i * moleculesPerLayer) + j] = 0;
                
                moleculesPlaced++;

                // Assign each molecule an initial velocity.
                double xVel = temperatureSqrt * rand.nextGaussian();
                double yVel = temperatureSqrt * rand.nextGaussian();
                m_moleculeVelocities[(i * moleculesPerLayer) + j].setComponents( xVel, yVel ); 
            }
        }
        
        updateDiatomicAtomPositions();
        
        */
    }
    
    /**
     * Create positions for a solid composed of triatomic molecules,
     * such as water.
     */
    private void solidifyTriatomicMolecules(){

        /*
         * TODO: JPB TBD - This functionality should be moved into the PhaseStateChanger objects.

        // Make sure we are really running in triatomic mode.
        assert m_atomsPerMolecule == 3;
        
        setTemperature( SOLID_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        int moleculesPerLayer = (int)Math.round( Math.sqrt( m_numberOfAtoms / 3 ) );
        
        double startingPosX = (m_normalizedContainerWidth / 2) - (double)(moleculesPerLayer / 2) - 
                ((moleculesPerLayer / 2) * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
        double startingPosY = 2.0 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL;
        
        // Place the molecules by placing their centers of mass.
        
        int moleculesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; moleculesPlaced < m_numberOfAtoms / 3; i++){ // One iteration per circular layer.
            for (int j = 0; (j < moleculesPerLayer) && (moleculesPlaced < m_numberOfAtoms / 3); j++){
                xPos = startingPosX + j + (j * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL) / 2;
                }
                yPos = startingPosY + (double)i * (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL)* 0.7071;
                m_moleculeCenterOfMassPositions[(i * moleculesPerLayer) + j].setLocation( xPos, yPos );
                m_moleculeRotationAngles[(i * moleculesPerLayer) + j] = m_rand.nextDouble() * 2 * Math.PI;
                
                moleculesPlaced++;

                // Assign each molecule an initial velocity.
                double xVel = temperatureSqrt * rand.nextGaussian();
                double yVel = temperatureSqrt * rand.nextGaussian();
                m_moleculeVelocities[(i * moleculesPerLayer) + j].setComponents( xVel, yVel ); 
            }
        }
        
        updateTriatomicAtomPositions();
        
        */
    }

    /**
     * Set the positions of the non-normalized particles based on the positions
     * of the normalized ones.
     */
    private void syncParticlePositions(){
        // TODO: JPB TBD - This way of un-normalizing needs to be worked out,
        // and setting it as done below is a temporary thing.
        double positionMultiplier = m_particleDiameter;
        Point2D [] atomPositions = m_moleculeDataSet.getAtomPositions();
        for (int i = 0; i < m_moleculeDataSet.getNumberOfAtoms(); i++){
            ((StatesOfMatterAtom)m_particles.get( i )).setPosition( atomPositions[i].getX() * positionMultiplier,
                    atomPositions[i].getY() * positionMultiplier);
        }
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of particles.
     */
    private void verletMonatomic(){

    	/*
         * TODO: JPB TBD - This functionality should be moved into the MoleculeForceAndMotionCalculator objects.

        double kineticEnergy = 0;
        
        double timeStepSqrHalf = TIME_STEP * TIME_STEP * 0.5;
        double timeStepHalf = TIME_STEP / 2;
        
        // Update the positions of all particles based on their current
        // velocities and the forces acting on them.
        for (int i = 0; i < m_numberOfAtoms; i++){
            double xPos = m_atomPositions[i].getX() + (TIME_STEP * m_moleculeVelocities[i].getX()) + 
                    (timeStepSqrHalf * m_moleculeForces[i].getX());
            double yPos = m_atomPositions[i].getY() + (TIME_STEP * m_moleculeVelocities[i].getY()) + 
                    (timeStepSqrHalf * m_moleculeForces[i].getY());
            m_atomPositions[i].setLocation( xPos, yPos );
        }
        
        // Zero out potential energy.
        m_potentialEnergy = 0;
        
        // Calculate the forces exerted on the particles by the container
        // walls and by gravity.
        double pressureZoneWallForce = 0;
        boolean interactionOccurredWithTop = false;
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Clear the previous calculation's particle forces.
            m_nextMoleculeForces[i].setComponents( 0, 0 );
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_atomPositions[i], m_normalizedContainerWidth, m_normalizedContainerHeight, 
                    m_nextMoleculeForces[i]);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            if (m_nextMoleculeForces[i].getY() < 0){
                pressureZoneWallForce += -m_nextMoleculeForces[i].getY();
                interactionOccurredWithTop = true;
            }
            else if (m_atomPositions[i].getY() > m_normalizedContainerHeight / 2){
            	// If the particle bounced on one of the walls above the midpoint, add
            	// in that value to the pressure.
            	pressureZoneWallForce += Math.abs( m_nextMoleculeForces[i].getX() );
            }
            
            // Add in the effect of gravity.
            double gravitationalAcceleration = m_gravitationalAcceleration;
            if (m_temperatureSetPoint < TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES){
            	// Below a certain temperature, gravity is increased to counteract some odd-looking behavior
            	// caused by the thermostat.
            	gravitationalAcceleration = gravitationalAcceleration * 
            	    ((TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES - m_temperatureSetPoint) * 
                    LOW_TEMPERATURE_GRAVITY_INCREASE_RATE + 1);
            }
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        // TODO: JPB TBD - Clean this up if we end up using it.
        double pressureCalcWeighting = 0.9995;
        m_pressure = (1 - pressureCalcWeighting) * (pressureZoneWallForce / (m_normalizedContainerWidth + m_normalizedContainerHeight)) + 
                pressureCalcWeighting * m_pressure;
        
        // If there are any atoms that are currently designated as "unsafe",
        // check them to see if they can be moved into the "safe" category.
        if (m_numberOfSafeAtoms < m_numberOfAtoms){
            updateMoleculeSafety();
        }
        
        // Calculate the forces created through interactions with other
        // particles.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < m_numberOfSafeAtoms; i++){
            for (int j = i + 1; j < m_numberOfSafeAtoms; j++){
                
                double dx = m_atomPositions[i].getX() - m_atomPositions[j].getX();
                double dy = m_atomPositions[i].getY() - m_atomPositions[j].getY();
                double distanceSqrd = (dx * dx) + (dy * dy);

                if (distanceSqrd == 0){
                    // Handle the special case where the particles are right
                    // on top of each other by assigning an arbitrary spacing.
                    // In general, this only happens when injecting new
                    // particles.
                    dx = 1;
                    dy = 1;
                    distanceSqrd = 2;
                }
                
                if (distanceSqrd < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // This pair of particles is close enough to one another
                    // that we need to calculate their interaction forces.
                    if (distanceSqrd < MIN_DISTANCE_SQUARED){
                        distanceSqrd = MIN_DISTANCE_SQUARED;
                    }
                    double r2inv = 1 / distanceSqrd;
                    double r6inv = r2inv * r2inv * r2inv;
                    double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
                    force.setX( dx * forceScaler );
                    force.setY( dy * forceScaler );
                    m_nextMoleculeForces[i].add( force );
                    m_nextMoleculeForces[j].subtract( force );
                    m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                }
            }
        }
        
        // Calculate the new velocities based on the old ones and the forces
        // that are acting on the particle.
        Vector2D.Double velocityIncrement = new Vector2D.Double();
        for (int i = 0; i < m_numberOfAtoms; i++){
            velocityIncrement.setX( timeStepHalf * (m_moleculeForces[i].getX() + m_nextMoleculeForces[i].getX()));
            velocityIncrement.setY( timeStepHalf * (m_moleculeForces[i].getY() + m_nextMoleculeForces[i].getY()));
            m_moleculeVelocities[i].add( velocityIncrement );
            kineticEnergy += ((m_moleculeVelocities[i].getX() * m_moleculeVelocities[i].getX()) + 
                    (m_moleculeVelocities[i].getY() * m_moleculeVelocities[i].getY())) / 2;
        }
        
        double calculatedTemperature = kineticEnergy / m_numberOfAtoms;
        boolean temperatureIsChanging = false;
        
        if ((m_heatingCoolingAmount != 0) ||
            (m_temperatureSetPoint + TEMPERATURE_CLOSENESS_RANGE < calculatedTemperature) ||
            (m_temperatureSetPoint - TEMPERATURE_CLOSENESS_RANGE > calculatedTemperature)) {
            temperatureIsChanging = true;
        }
        
        // Run the thermostat, which keeps the atoms from getting way too much
        // energy over time due to simulation inaccuracies and limitations.
        // Note that the thermostat is NOT run if the container size is
        // changing and interaction occurred with the moving surfaces of the
        // container, so that the increase/decrease in temperature caused by
        // the size change can actually happen.
        if ((m_heightChangeCounter == 0 || !interactionOccurredWithTop) && !m_lidBlownOff){
            if ((m_thermostatType == ISOKINETIC_THERMOSTAT) ||
                (m_thermostatType == ADAPTIVE_THERMOSTAT && temperatureIsChanging)){
            	
                // Isokinetic thermostat
                
                double temperatureScaleFactor;
                if (m_temperatureSetPoint <= m_minModelTemperature){
                    temperatureScaleFactor = 0.5;
                }
                else{
                    temperatureScaleFactor = Math.sqrt( m_temperatureSetPoint * m_numberOfAtoms / kineticEnergy );
                }
                kineticEnergy = 0;
                for (int i = 0; i < m_numberOfAtoms; i++){
                    m_moleculeVelocities[i].setComponents( m_moleculeVelocities[i].getX() * temperatureScaleFactor, 
                            m_moleculeVelocities[i].getY() * temperatureScaleFactor );
                    kineticEnergy += ((m_moleculeVelocities[i].getX() * m_moleculeVelocities[i].getX()) + 
                            (m_moleculeVelocities[i].getY() * m_moleculeVelocities[i].getY())) / 2;
                }
            }
            else if ((m_thermostatType == ANDERSEN_THERMOSTAT) ||
                     (m_thermostatType == ADAPTIVE_THERMOSTAT && !temperatureIsChanging)){
                // Modified Andersen Thermostat to maintain fixed temperature
                // modification to reduce abruptness of heat bath interactions.
                // For bare Andersen, set gamma=0.0d0.
            	
                double gammaX = 0.9999;
                double gammaY = gammaX;
                double temperature = m_temperatureSetPoint;
                if (m_temperatureSetPoint <= m_minModelTemperature){
                	// Use a values that will cause the molecules to stop
                	// moving if we are below the minimum temperature, since
                	// we want to create the appearance of absolute zero.
                	gammaX = 0.992;
                	gammaY = 0.999;   // Scale a little differently in Y direction so particles don't
                	                  // stop falling when absolute zero is reached.
                	temperature = 0;
                }
                for (int i = 0; i < m_numberOfAtoms; i++){
                    double xVel = m_moleculeVelocities[i].getX() * gammaX + m_rand.nextGaussian() * Math.sqrt(  temperature * (1 - Math.pow(gammaX, 2)) );
                    double yVel = m_moleculeVelocities[i].getY() * gammaY + m_rand.nextGaussian() * Math.sqrt(  temperature * (1 - Math.pow(gammaX, 2)) );
                    m_moleculeVelocities[i].setComponents( xVel, yVel );
                }
            }
        }
        else{
            // Since the container size is changing, we should update the
            // temperature set point based on the current amount of kinetic
            // energy, since it may have changed as a result of the container
            // size change.
            setTemperature( calculateTemperatureFromKineticEnergy() );
        }
        
        // Replace the new forces with the old ones.
        for (int i = 0; i < m_numberOfAtoms; i++){
            m_moleculeForces[i].setComponents( m_nextMoleculeForces[i].getX(), m_nextMoleculeForces[i].getY() );
        }
        */
    }

    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of diatomic molecules.
     */
    private void verletDiatomic(){
        
    	/*
         * TODO: JPB TBD - This functionality should be moved into the MoleculeForceAndMotionCalculator objects.

        assert m_atomsPerMolecule == 2;
        
        int numberOfMolecules = m_numberOfAtoms / 2;
        
        // JPB TBD - I skipped initializing m_x0 and m_y0 here, as they were
        // in the code that Paul supplied, because I thought it to be
        // redundant.  Check on this.
        
        double timeStepSqrHalf = TIME_STEP * TIME_STEP / 2;
        double timeStepHalf = TIME_STEP / 2;
        double massInverse = 1 / m_moleculeMass;
        double inertiaInverse = 1 / m_moleculeRotationalInertia;
        
        // Update center of mass positions and angles for the molecules.
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xPos = m_moleculeCenterOfMassPositions[i].getX() + (TIME_STEP * m_moleculeVelocities[i].getX()) +
                (timeStepSqrHalf * m_moleculeForces[i].getX() * massInverse);
            double yPos = m_moleculeCenterOfMassPositions[i].getY() + (TIME_STEP * m_moleculeVelocities[i].getY()) +
                (timeStepSqrHalf * m_moleculeForces[i].getY() * massInverse);
            
            m_moleculeCenterOfMassPositions[i].setLocation( xPos, yPos );
            
            m_moleculeRotationAngles[i] += (TIME_STEP * m_moleculeRotationRates[i]) +
                (timeStepSqrHalf * m_moleculeTorques[i] * inertiaInverse);
        }
        
        updateDiatomicAtomPositions();
        
        // Calculate the force from the walls.  This force is assumed to act
        // on the center of mass, so there is no torque.
        // Calculate the forces exerted on the particles by the container
        // walls and by gravity.
        double totalTopForce = 0;
        boolean interactionOccurredWithTop = false;
        for (int i = 0; i < numberOfMolecules; i++){

            // Clear the previous calculation's particle forces and torques.
            m_nextMoleculeForces[i].setComponents( 0, 0 );
            m_nextMoleculeTorques[i] = 0;
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_moleculeCenterOfMassPositions[i], m_normalizedContainerWidth, m_normalizedContainerHeight, 
                    m_nextMoleculeForces[i]);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            if (m_nextMoleculeForces[i].getY() < 0){
                totalTopForce += -m_nextMoleculeForces[i].getY();
                interactionOccurredWithTop = true;
            }
            
            // Add in the effect of gravity.
            double gravitationalAcceleration = m_gravitationalAcceleration;
            if (m_temperatureSetPoint < TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES){
            	// Below a certain temperature, gravity is increased to counteract some odd-looking behavior
            	// caused by the thermostat.
            	gravitationalAcceleration = gravitationalAcceleration * 
            	    ((TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES - m_temperatureSetPoint) * 
                    LOW_TEMPERATURE_GRAVITY_INCREASE_RATE + 1);
            }
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        updatePressure(totalTopForce);
        
        // If there are any atoms that are currently designated as "unsafe",
        // check them to see if they can be moved into the "safe" category.
        if (m_numberOfSafeAtoms < m_numberOfAtoms){
            updateMoleculeSafety();
        }
        double numberOfSafeMolecules = m_numberOfSafeAtoms / m_atomsPerMolecule;
        
        // Calculate the force and torque due to inter-particle interactions.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < numberOfSafeMolecules; i++){
            for (int j = i + 1; j < numberOfSafeMolecules; j++){
                for (int ii = 0; ii < 2; ii++){
                    for (int jj = 0; jj < 2; jj++){
                        // Calculate the distance between the potentially
                        // interacting atoms.
                        double dx = m_atomPositions[2 * i + ii].getX() - m_atomPositions[2 * j + jj].getX();
                        double dy = m_atomPositions[2 * i + ii].getY() - m_atomPositions[2 * j + jj].getY();
                        double distanceSquared = dx * dx + dy * dy;
                        
                        if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                        	
                            if (distanceSquared < MIN_DISTANCE_SQUARED){
                                distanceSquared = MIN_DISTANCE_SQUARED;
                            }

                            // Calculate the Lennard-Jones interaction forces.
                            double r2inv = 1 / distanceSquared;
                            double r6inv = r2inv * r2inv * r2inv;
                            double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
                            double fx = dx * forceScaler;
                            double fy = dy * forceScaler;
                            force.setComponents( fx, fy );
                            m_nextMoleculeForces[i].add( force );
                            m_nextMoleculeForces[j].subtract( force );
                            m_nextMoleculeTorques[i] += 
                                (m_atomPositions[2 * i + ii].getX() - m_moleculeCenterOfMassPositions[i].getX()) * fy -
                                (m_atomPositions[2 * i + ii].getY() - m_moleculeCenterOfMassPositions[i].getY()) * fx;
                            m_nextMoleculeTorques[j] -= 
                                (m_atomPositions[2 * j + jj].getX() - m_moleculeCenterOfMassPositions[j].getX()) * fy -
                                (m_atomPositions[2 * j + jj].getY() - m_moleculeCenterOfMassPositions[j].getY()) * fx;
                            
                            m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                        }
                    }
                }
            }
        }
        
        // Update center of mass velocities and angles and calculate kinetic
        // energy.
        double centersOfMassKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xVel = m_moleculeVelocities[i].getX() + 
                timeStepHalf * (m_moleculeForces[i].getX() + m_nextMoleculeForces[i].getX()) * massInverse;
            double yVel = m_moleculeVelocities[i].getY() + 
                timeStepHalf * (m_moleculeForces[i].getY() + m_nextMoleculeForces[i].getY()) * massInverse;
            m_moleculeVelocities[i].setComponents( xVel, yVel );
            
            m_moleculeRotationRates[i] += timeStepHalf * 
                (m_moleculeTorques[i] + m_nextMoleculeTorques[i]) * inertiaInverse;
            
            centersOfMassKineticEnergy += 0.5 * m_moleculeMass * 
               (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
            rotationalKineticEnergy += 0.5 * m_moleculeRotationalInertia * Math.pow(m_moleculeRotationRates[i], 2);
            
            // Move the newly calculated forces and torques into the current spots.
            m_moleculeForces[i].setComponents( m_nextMoleculeForces[i].getX(), m_nextMoleculeForces[i].getY());
            m_moleculeTorques[i] = m_nextMoleculeTorques[i];
        }
        
        double calculatedTemperature = (centersOfMassKineticEnergy + rotationalKineticEnergy) / numberOfMolecules / 1.5;
        boolean temperatureIsChanging = false;
        
        if ((m_heatingCoolingAmount != 0) ||
            (m_temperatureSetPoint + TEMPERATURE_CLOSENESS_RANGE < calculatedTemperature) ||
            (m_temperatureSetPoint - TEMPERATURE_CLOSENESS_RANGE > calculatedTemperature)) {
            temperatureIsChanging = true;
        }
        
        // Run the thermostat, which keeps the atoms from getting way too much
        // energy over time due to simulation inaccuracies and limitations.
        // Note that the thermostat is NOT run if the container size is
        // changing and interaction occurred with the moving surfaces of the
        // container, so that the increase/decrease in temperature caused by
        // the size change can actually happen.
        if ((m_heightChangeCounter == 0 || !interactionOccurredWithTop) && !m_lidBlownOff){
            
            if ((m_thermostatType == ISOKINETIC_THERMOSTAT) ||
                (m_thermostatType == ADAPTIVE_THERMOSTAT && temperatureIsChanging)){

                // Isokinetic thermostat
                
                double temperatureScaleFactor;
                if (m_temperatureSetPoint <= m_minModelTemperature){
                    temperatureScaleFactor = 0;
                }
                else{
                    temperatureScaleFactor = Math.sqrt( 1.5 * m_temperatureSetPoint * numberOfMolecules / (centersOfMassKineticEnergy + rotationalKineticEnergy) );
                }
                for (int i = 0; i < numberOfMolecules; i++){
                    m_moleculeVelocities[i].setComponents( m_moleculeVelocities[i].getX() * temperatureScaleFactor, 
                            m_moleculeVelocities[i].getY() * temperatureScaleFactor );
                    m_moleculeRotationRates[i] *= temperatureScaleFactor;
                }
                centersOfMassKineticEnergy = centersOfMassKineticEnergy * temperatureScaleFactor * temperatureScaleFactor;
                rotationalKineticEnergy = rotationalKineticEnergy * temperatureScaleFactor * temperatureScaleFactor;
            }
            else if ((m_thermostatType == ANDERSEN_THERMOSTAT) ||
                     (m_thermostatType == ADAPTIVE_THERMOSTAT && !temperatureIsChanging)){

                // Modified Andersen Thermostat to maintain fixed temperature
                // modification to reduce abruptness of heat bath interactions.
                // For bare Andersen, set gamma=0.0d0.
                double gamma = 0.9999;
                double temperature = m_temperatureSetPoint;
                if (m_temperatureSetPoint <= m_minModelTemperature){
                	// Use a values that will cause the molecules to stop
                	// moving if we are below the minimum temperature, since
                	// we want to create the appearance of absolute zero.
                	gamma = 0.992;
                	temperature = 0;
                }
                double velocityScalingFactor = Math.sqrt( temperature * massInverse * (1 - Math.pow( gamma, 2)));
                double rotationScalingFactor = Math.sqrt( temperature * inertiaInverse * (1 - Math.pow( gamma, 2)));
                
                for (int i = 0; i < numberOfMolecules; i++){
                    double xVel = m_moleculeVelocities[i].getX() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                    double yVel = m_moleculeVelocities[i].getY() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                    m_moleculeVelocities[i].setComponents( xVel, yVel );
                    m_moleculeRotationRates[i] = gamma * m_moleculeRotationRates[i] + 
                            m_rand.nextGaussian() * rotationScalingFactor;
                }
            }
        }
        else{
            // Since the container size is changing, we should update the
            // temperature set point based on the current amount of kinetic
            // energy, since it may have changed as a result of the container
            // size change.
            setTemperature( calculateTemperatureFromKineticEnergy() );
        }
        */
    }

	private void updatePressure( double totalTopForce ) {
        m_pressure = (1 - PRESSURE_DECAY_CALCULATION_WEIGHTING) * (totalTopForce / m_normalizedContainerWidth) + 
        	PRESSURE_DECAY_CALCULATION_WEIGHTING * m_pressure;
	}
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of triatomic molecules.
     */
    private void verletTriatomic(){
        
    	/*
         * TODO: JPB TBD - This functionality should be moved into the MoleculeForceAndMotionCalculator objects.

        assert m_atomsPerMolecule == 3;
        
        int numberOfMolecules = m_numberOfAtoms / 3;

        // TODO: JPB TBD Work with Paul to come up with better names for these
        // charge-defining variables.
        double q0;
        if ( m_temperatureSetPoint < WATER_FULLY_FROZEN_TEMPERATURE ){
            // Use stronger electrostatic forces in order to create more of
            // a crystal structure.
            q0 = WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE;
        }
        else if ( m_temperatureSetPoint > WATER_FULLY_MELTED_TEMPERATURE ){
            // Use weaker electrostatic forces in order to create more of an
            // appearance of liquid.
            q0 = WATER_FULLY_MELTED_ELECTROSTATIC_FORCE;
        }
        else {
            // We are somewhere in between the temperature for being fully
            // melted or frozen, so scale accordingly.
            double temperatureFactor = (m_temperatureSetPoint - WATER_FULLY_FROZEN_TEMPERATURE)/
                    (WATER_FULLY_MELTED_TEMPERATURE - WATER_FULLY_FROZEN_TEMPERATURE);
            q0 = WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE - 
                (temperatureFactor * (WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE - WATER_FULLY_MELTED_ELECTROSTATIC_FORCE));
        }
        double [] normalCharges = new double [] {-2*q0, q0, q0};
        double [] alteredCharges = new double [] {-2*q0, 1.67*q0, 0.33*q0};
        
        // JPB TBD - I skipped initializing m_x0 and m_y0 here, as they were
        // in the code that Paul supplied, because I thought it to be
        // redundant.  Check on this.
        
        double timeStepSqrHalf = TIME_STEP * TIME_STEP / 2;
        double timeStepHalf = TIME_STEP / 2;
        double massInverse = 1 / m_moleculeMass;
        double inertiaInverse = 1 / m_moleculeRotationalInertia;
        
        // Update center of mass positions and angles for the molecules.
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xPos = m_moleculeCenterOfMassPositions[i].getX() + (TIME_STEP * m_moleculeVelocities[i].getX()) +
                (timeStepSqrHalf * m_moleculeForces[i].getX() * massInverse);
            double yPos = m_moleculeCenterOfMassPositions[i].getY() + (TIME_STEP * m_moleculeVelocities[i].getY()) +
                (timeStepSqrHalf * m_moleculeForces[i].getY() * massInverse);
            
            m_moleculeCenterOfMassPositions[i].setLocation( xPos, yPos );
            
            m_moleculeRotationAngles[i] += (TIME_STEP * m_moleculeRotationRates[i]) +
                (timeStepSqrHalf * m_moleculeTorques[i] * inertiaInverse);
        }
        
        updateTriatomicAtomPositions();
        
        // Calculate the force from the walls.  This force is assumed to act
        // on the center of mass, so there is no torque.
        // Calculate the forces exerted on the particles by the container
        // walls and by gravity.
        double totalTopForce = 0;
        boolean interactionOccurredWithTop = false;
        for (int i = 0; i < numberOfMolecules; i++){
            
            // Clear the previous calculation's particle forces and torques.
            m_nextMoleculeForces[i].setComponents( 0, 0 );
            m_nextMoleculeTorques[i] = 0;
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_moleculeCenterOfMassPositions[i], m_normalizedContainerWidth, m_normalizedContainerHeight, 
                    m_nextMoleculeForces[i]);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            if (m_nextMoleculeForces[i].getY() < 0){
                totalTopForce += -m_nextMoleculeForces[i].getY();
                interactionOccurredWithTop = true;
            }
            
            // Add in the effect of gravity.
            double gravitationalAcceleration = m_gravitationalAcceleration;
            if (m_temperatureSetPoint < TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES){
            	// Below a certain temperature, gravity is increased to counteract some odd-looking behavior
            	// caused by the thermostat.
            	gravitationalAcceleration = gravitationalAcceleration * 
            	    ((TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES - m_temperatureSetPoint) * 
                    LOW_TEMPERATURE_GRAVITY_INCREASE_RATE + 1);
            }
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        // TODO: JPB TBD - Clean this up if we end up using it.
        double pressureCalcWeighting = 0.9995;
        m_pressure = (1 - pressureCalcWeighting) * (totalTopForce / m_normalizedContainerWidth) + 
                pressureCalcWeighting * m_pressure;
        
        // If there are any atoms that are currently designated as "unsafe",
        // check them to see if they can be moved into the "safe" category.
        if (m_numberOfSafeAtoms < m_numberOfAtoms){
            updateMoleculeSafety();
        }
        double numberOfSafeMolecules = m_numberOfSafeAtoms / m_atomsPerMolecule;
        
        // Calculate the force and torque due to inter-particle interactions.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < numberOfSafeMolecules; i++){
            
            // Select which charges to use for this molecule.  This is part of
            // the "hollywooding" to make the solid form appear more crystalline.
            double [] chargesA;
            if (i % 2 == 0){
                chargesA = normalCharges;
            }
            else{
                chargesA = alteredCharges;
            }
            
            for (int j = i + 1; j < numberOfSafeMolecules; j++){
            
                // Select charges for this molecule.
                double [] chargesB;
                if (j % 2 == 0){
                    chargesB = normalCharges;
                }
                else{
                    chargesB = alteredCharges;
                }
                
                // Calculate Lennard-Jones potential between mass centers.
                double dx = m_moleculeCenterOfMassPositions[i].getX() - m_moleculeCenterOfMassPositions[j].getX();
                double dy = m_moleculeCenterOfMassPositions[i].getY() - m_moleculeCenterOfMassPositions[j].getY();
                double distanceSquared = dx * dx + dy * dy;
                
                if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // Calculate the Lennard-Jones interaction forces.
                	
                    if (distanceSquared < MIN_DISTANCE_SQUARED){
                        distanceSquared = MIN_DISTANCE_SQUARED;
                    }

                    double r2inv = 1 / distanceSquared;
                    double r6inv = r2inv * r2inv * r2inv;
                    
                    // A scaling factor is added here for the repulsive
                    // portion of the Lennard-Jones force.  The idea is that
                    // the force goes up at lower temperatures in order to
                    // make the ice appear more spacious.  This is not real
                    // physics, it is "hollywooding" in order to get the
                    // crystalline behavior we need for ice.
                    double scalingFactor;
                    double maxScalingFactor = 3;  // TODO: JPB TBD - Make a constant if kept.
                    if (m_temperatureSetPoint > WATER_FULLY_MELTED_TEMPERATURE){
                        // No scaling of the repulsive force.
                        scalingFactor = 1;
                    }
                    else if (m_temperatureSetPoint < WATER_FULLY_FROZEN_TEMPERATURE){
                        // Scale by the max to force space in the crystal.
                        scalingFactor = maxScalingFactor;
                    }
                    else{
                        // We are somewhere between fully frozen and fully
                        // liquified, so adjust the scaling factor accordingly.
                        double temperatureFactor = (m_temperatureSetPoint - WATER_FULLY_FROZEN_TEMPERATURE)/
                                (WATER_FULLY_MELTED_TEMPERATURE - WATER_FULLY_FROZEN_TEMPERATURE);
                        scalingFactor = maxScalingFactor - (temperatureFactor * (maxScalingFactor - 1));
                    }
                    double forceScaler = 48 * r2inv * r6inv * ((r6inv * scalingFactor) - 0.5);
                    force.setX( dx * forceScaler );
                    force.setY( dy * forceScaler );
                    m_nextMoleculeForces[i].add( force );
                    m_nextMoleculeForces[j].subtract( force );
                    m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                }

                if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // Calculate coulomb-like interactions between atoms on
                    // different water molecules.
                    for (int ii = 0; ii < 3; ii++){
                        for (int jj = 0; jj < 3; jj++){
                            if (((3 * i + ii + 1) % 6 == 0) ||  ((3 * j + jj + 1) % 6 == 0)){
                                // This is a hydrogen atom that is not going to be included
                                // in the calculation in order to try to create a more
                                // crystalline solid.  This is part of the "hollywooding"
                                // that we do to create a better looking water crystal at
                                // low temperatures.
                                continue;
                            }
                            dx = m_atomPositions[3 * i + ii].getX() - m_atomPositions[3 * j + jj].getX();
                            dy = m_atomPositions[3 * i + ii].getY() - m_atomPositions[3 * j + jj].getY();
                            double r2inv = 1/(dx*dx + dy*dy);
                            double forceScaler=chargesA[ii]*chargesB[jj]*r2inv*r2inv;
                            force.setX( dx * forceScaler );
                            force.setY( dy * forceScaler );
                            
                            m_nextMoleculeForces[i].add( force );
                            m_nextMoleculeForces[j].subtract( force );
                            m_nextMoleculeTorques[i] += (m_atomPositions[3 * i + ii].getX() - 
                                    m_moleculeCenterOfMassPositions[i].getX()) * force.getY() -
                                   (m_atomPositions[3 * i + ii].getY() - 
                                           m_moleculeCenterOfMassPositions[i].getY()) * force.getX();
                            m_nextMoleculeTorques[j] -= (m_atomPositions[3 * j + jj].getX() - m_moleculeCenterOfMassPositions[j].getX()) * force.getY() -
                                (m_atomPositions[3 * j + jj].getY() - m_moleculeCenterOfMassPositions[j].getY()) * force.getX();
                        }
                    }
                }
            }
        }
        
        // Update center of mass velocities and angles and calculate kinetic
        // energy.
        // JPB TBD - Come up with better names for these terms?
        double kecm = 0;
        double kerot = 0;
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xVel = m_moleculeVelocities[i].getX() + 
                timeStepHalf * (m_moleculeForces[i].getX() + m_nextMoleculeForces[i].getX()) * massInverse;
            double yVel = m_moleculeVelocities[i].getY() + 
                timeStepHalf * (m_moleculeForces[i].getY() + m_nextMoleculeForces[i].getY()) * massInverse;
            m_moleculeVelocities[i].setComponents( xVel, yVel );
            
            m_moleculeRotationRates[i] += timeStepHalf * (m_moleculeTorques[i] + m_nextMoleculeTorques[i]) * inertiaInverse;
            
            kecm += 0.5 * m_moleculeMass * 
               (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
            kerot += 0.5 * m_moleculeRotationalInertia * Math.pow(m_moleculeRotationRates[i], 2);
            
            // Move the newly calculated forces and torques into the current spots.
            m_moleculeForces[i].setComponents( m_nextMoleculeForces[i].getX(), m_nextMoleculeForces[i].getY());
            m_moleculeTorques[i] = m_nextMoleculeTorques[i];
        }
        
        double calculatedTemperature = (kecm + kerot) / numberOfMolecules / 1.5;
        boolean temperatureIsChanging = false;
        
        if ((m_heatingCoolingAmount != 0) ||
            (m_temperatureSetPoint + TEMPERATURE_CLOSENESS_RANGE < calculatedTemperature) ||
            (m_temperatureSetPoint - TEMPERATURE_CLOSENESS_RANGE > calculatedTemperature)) {
            temperatureIsChanging = true;
        }
        
        // Run the thermostat, which keeps the atoms from getting way too much
        // energy over time due to simulation inaccuracies and limitations.
        // Note that the thermostat is NOT run if the container size is
        // changing and interaction occurred with the moving surfaces of the
        // container, so that the increase/decrease in temperature caused by
        // the size change can actually happen.
        if ((m_heightChangeCounter == 0 || !interactionOccurredWithTop) && !m_lidBlownOff){
            if ((m_thermostatType == ISOKINETIC_THERMOSTAT) ||
                (m_thermostatType == ADAPTIVE_THERMOSTAT && temperatureIsChanging)){

                // Isokinetic thermostat
                
                double temperatureScaleFactor;
                if (m_temperatureSetPoint <= m_minModelTemperature){
                    temperatureScaleFactor = 0;
                }
                else{
                    temperatureScaleFactor = Math.sqrt( 1.5 * m_temperatureSetPoint * numberOfMolecules / (kecm + kerot) );
                }
                for (int i = 0; i < numberOfMolecules; i++){
                    m_moleculeVelocities[i].setComponents( m_moleculeVelocities[i].getX() * temperatureScaleFactor, 
                            m_moleculeVelocities[i].getY() * temperatureScaleFactor );
                    m_moleculeRotationRates[i] *= temperatureScaleFactor;
                }
                kecm = kecm * temperatureScaleFactor * temperatureScaleFactor;
                kerot = kerot * temperatureScaleFactor * temperatureScaleFactor;
            }
            else if ((m_thermostatType == ANDERSEN_THERMOSTAT) ||
                     (m_thermostatType == ADAPTIVE_THERMOSTAT && !temperatureIsChanging)){

                // Modified Andersen Thermostat to maintain fixed temperature
                // modification to reduce abruptness of heat bath interactions.
                // For bare Andersen, set gamma=0.0d0.
                double gamma = 0.9999;
                double temperature = m_temperatureSetPoint;
                if (m_temperatureSetPoint <= m_minModelTemperature){
                	// Use a values that will cause the molecules to stop
                	// moving if we are below the minimum temperature, since
                	// we want to create the appearance of absolute zero.
                	gamma = 0.992;
                	temperature = 0;
                }
                double velocityScalingFactor = Math.sqrt( temperature * massInverse * (1 - Math.pow( gamma, 2)));
                double rotationScalingFactor = Math.sqrt( temperature * inertiaInverse * (1 - Math.pow( gamma, 2)));
                
                for (int i = 0; i < numberOfMolecules; i++){
                    double xVel = m_moleculeVelocities[i].getX() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                    double yVel = m_moleculeVelocities[i].getY() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                    m_moleculeVelocities[i].setComponents( xVel, yVel );
                    m_moleculeRotationRates[i] = gamma * m_moleculeRotationRates[i] + 
                            m_rand.nextGaussian() * rotationScalingFactor;
                }
            }
        }
        else{
            // Since the container size is changing, we should update the
            // temperature set point based on the current amount of kinetic
            // energy, since it may have changed as a result of the container
            // size change.
            setTemperature( calculatedTemperature );
        }
        
        */
    }
    
    /**
     * Position the individual atoms based on their centers of mass and
     * initial angle.  !IMPORTANT NOTE - This assumes that the molecules
     * are positioned in diatomic pairs in the arrays.  For instance,
     * the atoms at indexes 0 & 1 are one pair, at 2 & 3 are a pair, and so
     * on.
     */
    private void updateDiatomicAtomPositions(){
      
    	/*
         * TODO: JPB TBD - This functionality should be moved into the AtomPositionUpdater objects.

        assert m_atomsPerMolecule == 2;

        double xPos, yPos;
        
        for (int i = 0; i < m_numberOfAtoms / 2; i++){
            double cosineTheta = Math.cos( m_moleculeRotationAngles[i] );
            double sineTheta = Math.sin( m_moleculeRotationAngles[i] );
            for (int j = 0; j < 2; j++){
                xPos = m_moleculeCenterOfMassPositions[i].getX() + cosineTheta * m_x0[j] - sineTheta * m_y0[j];
                yPos = m_moleculeCenterOfMassPositions[i].getY() + sineTheta * m_x0[j] + cosineTheta * m_y0[j];
                m_atomPositions[i * 2 + j].setLocation( xPos, yPos );
            }
        }
        */
    }

    /**
     * Position the individual atoms based on their centers of mass and
     * initial angle.  !IMPORTANT NOTE - This assumes that the molecules
     * are positioned in groups of three in the arrays.  For instance, a
     * water molecule will be Oxygen followed by two Hydrogens.
     */
    private void updateTriatomicAtomPositions(){
     
    	/*
         * TODO: JPB TBD - This functionality should be moved into the AtomPositionUpdater objects.

        assert m_atomsPerMolecule == 3;

        double xPos, yPos;
        
        for (int i = 0; i < m_numberOfAtoms / 3; i++){
            double cosineTheta = Math.cos( m_moleculeRotationAngles[i] );
            double sineTheta = Math.sin( m_moleculeRotationAngles[i] );
            for (int j = 0; j < 3; j++){
                xPos = m_moleculeCenterOfMassPositions[i].getX() + cosineTheta * m_x0[j] - sineTheta * m_y0[j];
                yPos = m_moleculeCenterOfMassPositions[i].getY() + sineTheta * m_x0[j] + cosineTheta * m_y0[j];
                m_atomPositions[i * 3 + j].setLocation( xPos, yPos );
            }
        }
        */
    }
    
    /**
     * Update the safety status of any molecules that may have previously been
     * designated as unsafe.  An "unsafe" molecule is one that was injected
     * into the container and was found to be so close to one or more of the
     * other molecules that if its interaction forces were calculated, it
     * would be given a ridiculously large amount of kinetic energy that might
     * end up launching it out of the container.
     */
    private void updateMoleculeSafety(){
     
    	/*
         * TODO: JPB TBD - This functionality should be moved into the MoleculeForceAndMotionCalculator objects.

        for (int i = m_numberOfSafeAtoms; i < m_numberOfAtoms; i += m_atomsPerMolecule){
            
            boolean moleculeIsUnsafe = false;

            // Find out if this molecule is still to close to all the "safe"
            // molecules to become safe itself.
            if (m_atomsPerMolecule == 1){
                for (int j = 0; j < m_numberOfSafeAtoms; j++){
                    if ( m_atomPositions[i].distance( m_atomPositions[j] ) < SAFE_INTER_MOLECULE_DISTANCE ){
                        moleculeIsUnsafe = true;
                        break;
                    }
                }
            }
            else{
                for (int j = 0; j < m_numberOfSafeAtoms; j += m_atomsPerMolecule){
                    if ( m_moleculeCenterOfMassPositions[i / m_atomsPerMolecule].distance( m_moleculeCenterOfMassPositions[j / m_atomsPerMolecule] ) < SAFE_INTER_MOLECULE_DISTANCE ){
                        moleculeIsUnsafe = true;
                        break;
                    }
                }
            }
            
            if (!moleculeIsUnsafe){
                // The molecule just tested was safe, so adjust the arrays
                // accordingly.
                if (i != m_numberOfSafeAtoms){
                    // There is at least one unsafe atom/molecule in front of
                    // this one in the arrays, so some swapping must be done
                	// before the number of safe atoms can be incremented.
                    
                    // Swap the safe atom(s) with the first unsafe one.
                    Point2D tempAtomPosition;
                    
                    for (int j = 0; j < m_atomsPerMolecule; j++){
                        tempAtomPosition = m_atomPositions[m_numberOfSafeAtoms + j];
                        m_atomPositions[m_numberOfSafeAtoms + j] = m_atomPositions[i + j];
                        m_atomPositions[i + j] = tempAtomPosition;
                    }
                    
                    Vector2D tempMoleculeVelocity;
                    Vector2D tempMoleculeForce;
                    
                    int firstUnsafeMoleculeIndex = m_numberOfSafeAtoms / m_atomsPerMolecule;
                    int safeMoleculeIndex = i / m_atomsPerMolecule;
                    tempMoleculeVelocity = m_moleculeVelocities[firstUnsafeMoleculeIndex];
                    tempMoleculeForce = m_moleculeForces[firstUnsafeMoleculeIndex];
                    m_moleculeVelocities[firstUnsafeMoleculeIndex] = m_moleculeVelocities[safeMoleculeIndex];
                    m_moleculeForces[firstUnsafeMoleculeIndex] = m_moleculeForces[safeMoleculeIndex];
                    m_moleculeVelocities[safeMoleculeIndex] = tempMoleculeVelocity;
                    m_moleculeForces[safeMoleculeIndex] = tempMoleculeForce;
                    
                    if ( m_atomsPerMolecule > 1 ){
                    	// Swap the molecular parameters that are only used for composite molecules.

                    	Point2D tempMoleculeCenterOfMassPosition;
                        double tempMoleculeRotationAngle;
                        double tempMoleculeRotationRate;

                        tempMoleculeCenterOfMassPosition = m_moleculeCenterOfMassPositions[firstUnsafeMoleculeIndex];
                        tempMoleculeRotationAngle = m_moleculeRotationAngles[firstUnsafeMoleculeIndex];
                        tempMoleculeRotationRate = m_moleculeRotationRates[firstUnsafeMoleculeIndex];
                        m_moleculeCenterOfMassPositions[firstUnsafeMoleculeIndex] = m_moleculeCenterOfMassPositions[safeMoleculeIndex];
                        m_moleculeRotationAngles[firstUnsafeMoleculeIndex] = m_moleculeRotationAngles[safeMoleculeIndex];
                        m_moleculeRotationRates[firstUnsafeMoleculeIndex] = m_moleculeRotationRates[safeMoleculeIndex];
                        m_moleculeCenterOfMassPositions[safeMoleculeIndex] = tempMoleculeCenterOfMassPosition;
                        m_moleculeRotationAngles[safeMoleculeIndex] = tempMoleculeRotationAngle;
                        m_moleculeRotationRates[safeMoleculeIndex] = tempMoleculeRotationRate;
                    }
                    // Note: Don't worry about torque, since there isn't any until the molecules become "safe".
                }
                m_numberOfSafeAtoms += m_atomsPerMolecule;
            }
        }
        */
    }
    
    /**
     * Calculate the force exerted on a particle at the provided position by
     * the walls of the container.  The result is returned in the provided
     * vector.
     * 
     * @param position - Current position of the particle.
     * @param containerWidth - Width of the container where particles are held.
     * @param containerHeight - Height of the container where particles are held.
     * @param resultantForce - Vector in which the resulting force is returned.
     */
    private void calculateWallForce(Point2D position, double containerWidth, double containerHeight,
            Vector2D resultantForce){
        
        // Debug stuff - make sure this is being used correctly.
        assert resultantForce != null;
        assert position != null;
        
        // Non-debug run time check.
        if ((resultantForce == null) || (position == null)){
            return;
        }
        
        double xPos = position.getX();
        double yPos = position.getY();
        
        double minDistance = WALL_DISTANCE_THRESHOLD * 0.8;
        double distance;
        
        if (yPos < StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT / m_particleDiameter){  // This handles the case where particles have blown out the top.
	        // Calculate the force in the X direction.
	        if (xPos < WALL_DISTANCE_THRESHOLD){
	            // Close enough to the left wall to feel the force.
	            if (xPos < minDistance){
                    if ((xPos < 0) && (m_lidBlownOff)){
                        // The particle is outside the container after the
                        // container has exploded, so don't let the walls
                        // exert any force.
                        xPos = Double.POSITIVE_INFINITY;
                    }
                    else{
                        // Limit the distance, and thus the force, if we are really close.
                        xPos = minDistance;
                    }
                }
	            resultantForce.setX( (48/(Math.pow(xPos, 13))) - (24/(Math.pow( xPos, 7))) );
	            m_potentialEnergy += 4/(Math.pow(xPos, 12)) - 4/(Math.pow( xPos, 6)) + 1;
	        }
	        else if (containerWidth - xPos < WALL_DISTANCE_THRESHOLD){
	            // Close enough to the right wall to feel the force.
	            distance = containerWidth - xPos;
	            if (distance < minDistance){
                    if ((distance < 0) && (m_lidBlownOff)){
                        // The particle is outside the container after the
                        // container has exploded, so don't let the walls
                        // exert any force.
                        xPos = Double.POSITIVE_INFINITY;
                    }
                    else{
                        distance = minDistance;
                    }
                }
	            resultantForce.setX( -(48/(Math.pow(distance, 13))) + 
	                    (24/(Math.pow( distance, 7))) );
	            m_potentialEnergy += 4/(Math.pow(distance, 12)) - 
	                    4/(Math.pow( distance, 6)) + 1;
	        }
        }
        
        // Calculate the force in the Y direction.
        if (yPos < WALL_DISTANCE_THRESHOLD){
            // Close enough to the bottom wall to feel the force.
            if (yPos < minDistance){
                if ((yPos < 0) && (!m_lidBlownOff)){
                    // The particles are energetic enough to end up outside
                    // the container, so consider it to be exploded.
                    m_lidBlownOff = true;
                    notifyContainerExploded();
                }
                yPos = minDistance;
            }
            if (!m_lidBlownOff || ((xPos > 0) && (xPos < containerWidth))){
                // Only calculate the force if the particle is inside the
                // container.
                resultantForce.setY( 48/(Math.pow(yPos, 13)) - (24/(Math.pow( yPos, 7))) );
                m_potentialEnergy += 4/(Math.pow(yPos, 12)) - 4/(Math.pow( yPos, 6)) + 1;
            }
        }
        else if ( ( containerHeight - yPos < WALL_DISTANCE_THRESHOLD ) && !m_lidBlownOff ){
            // Close enough to the top to feel the force.
            distance = containerHeight - yPos;
            if (distance < minDistance){
                distance = minDistance;
            }
            resultantForce.setY( -48/(Math.pow(distance, 13)) + (24/(Math.pow( distance, 7))) );
            m_potentialEnergy += 4/(Math.pow(distance, 12)) - 4/(Math.pow( distance, 6)) + 1;
        }
    }
    
    /**
     * Take the internal temperature value and convert it to Kelvin.  This
     * is dependent on the type of molecule selected.  The values and ranges
     * used in this method were derived from information provided by Paul
     * Beale.
     */
    private double convertInternalTemperatureToKelvin(){
        
        double temperatureInKelvin = 0;
        double triplePoint = 0;
        double criticalPoint = 0;
        
        switch (m_currentMolecule){
        
        case StatesOfMatterConstants.NEON:
        	triplePoint = NEON_TRIPLE_POINT_IN_KELVIN;
        	criticalPoint = NEON_CRITICAL_POINT_IN_KELVIN;
            break;
            
        case StatesOfMatterConstants.ARGON:
        	triplePoint = ARGON_TRIPLE_POINT_IN_KELVIN;
        	criticalPoint = ARGON_CRITICAL_POINT_IN_KELVIN;
            break;

        case StatesOfMatterConstants.WATER:
        	triplePoint = WATER_TRIPLE_POINT_IN_KELVIN;
        	criticalPoint = WATER_CRITICAL_POINT_IN_KELVIN;
            break;
            
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
        	triplePoint = O2_TRIPLE_POINT_IN_KELVIN;
        	criticalPoint = O2_CRITICAL_POINT_IN_KELVIN;
            break;
            
        default:
            break;
        }

        if (m_temperatureSetPoint <= m_minModelTemperature){
        	// We treat anything below the minimum temperature as absolute zero.
        	temperatureInKelvin = 0;
        }
        else if (m_temperatureSetPoint < TRIPLE_POINT_MODEL_TEMPERATURE){
        	temperatureInKelvin = m_temperatureSetPoint * triplePoint / TRIPLE_POINT_MODEL_TEMPERATURE;
        	
        	if ( temperatureInKelvin < 0.5){
        		// Don't return zero - or anything that would round to it - as
        		// a value until we actually reach the minimum internal temperature.
        		temperatureInKelvin = 0.5;
        	}
        }
        else if (m_temperatureSetPoint < CRITICAL_POINT_MODEL_TEMPERATURE){
        	double slope = (criticalPoint - triplePoint) / (CRITICAL_POINT_MODEL_TEMPERATURE - TRIPLE_POINT_MODEL_TEMPERATURE);
        	double offset = triplePoint - (slope * TRIPLE_POINT_MODEL_TEMPERATURE);
        	temperatureInKelvin = m_temperatureSetPoint * slope + offset;
        }
        else {
        	temperatureInKelvin = m_temperatureSetPoint * criticalPoint / CRITICAL_POINT_MODEL_TEMPERATURE;
        }
        return temperatureInKelvin;
    }
    
    /**
     * Take the internal pressure value and convert it to atmospheres.  This
     * is dependent on the type of molecule selected.  The values and ranges
     * used in this method were derived from information provided by Paul
     * Beale.
     * 
     * @return
     */
    public double getPressureInAtmospheres() {
        
        double pressureInAtmospheres = 0;
        
        switch (m_currentMolecule){
        
        case StatesOfMatterConstants.NEON:
            pressureInAtmospheres = 200 * getModelPressure();
            break;
            
        case StatesOfMatterConstants.ARGON:
            pressureInAtmospheres = 125 * getModelPressure();
            break;

        case StatesOfMatterConstants.WATER:
            pressureInAtmospheres = 200 * getModelPressure();
            break;
            
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            pressureInAtmospheres = 125 * getModelPressure();
            break;
            
        default:
            pressureInAtmospheres = 0;
            break;
        }
        
        return pressureInAtmospheres;
    }

    /**
     * Calculate the temperature by examining the kinetic energy of the
     * molecules.
     * 
     * @return
     */
    private double calculateTemperatureFromKineticEnergy(){
        
    	/*
         * TODO: JPB TBD - This functionality should be moved into the molecule data set objects (I think).

        double translationalKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        double numberOfMolecules = m_numberOfAtoms / m_atomsPerMolecule;
        double kineticEnergyPerMolecule;
        
        if (m_atomsPerMolecule == 1){
            for (int i = 0; i < m_numberOfAtoms; i++){
                translationalKineticEnergy += ((m_moleculeVelocities[i].getX() * m_moleculeVelocities[i].getX()) + 
                        (m_moleculeVelocities[i].getY() * m_moleculeVelocities[i].getY())) / 2;
            }
            kineticEnergyPerMolecule = translationalKineticEnergy / m_numberOfAtoms;
        }
        else{
            for (int i = 0; i < m_numberOfAtoms / m_atomsPerMolecule; i++){
                translationalKineticEnergy += 0.5 * m_moleculeMass * 
                        (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
                rotationalKineticEnergy += 0.5 * m_moleculeRotationalInertia * Math.pow(m_moleculeRotationRates[i], 2);
            }            
            kineticEnergyPerMolecule = 
                (translationalKineticEnergy + rotationalKineticEnergy) / numberOfMolecules / 1.5;
        }
            
        return kineticEnergyPerMolecule;
        */
    	return 0;
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------
    
    /* TODO: JPB TBD Commented out for abstract refactor thing 10/20/2008
     * 
     */
//    public static interface Listener {
//        
//        /**
//         * Inform listeners that the model has been reset.
//         */
//        public void resetOccurred();
//        
//        /**
//         * Inform listeners that a new particle has been added to the model.
//         */
//        public void particleAdded(StatesOfMatterAtom particle);
//        
//        /**
//         * Inform listeners that the temperature of the system has changed.
//         */
//        public void temperatureChanged();
//        
//        /**
//         * Inform listeners that the pressure of the system has changed.
//         */
//        public void pressureChanged();
//
//        /**
//         * Inform listeners that the size of the container has changed.
//         */
//        public void containerSizeChanged();
//        
//        /**
//         * Inform listeners that the type of molecule being simulated has
//         * changed.
//         */
//        public void moleculeTypeChanged();
//
//        /**
//         * Inform listeners that the container has exploded.
//         */
//        public void containerExploded();
//
//    }
    
    public static class Adapter implements Listener {
        public void resetOccurred(){}
        public void particleAdded(StatesOfMatterAtom particle){}
        public void temperatureChanged(){}
        public void pressureChanged(){}
        public void containerSizeChanged(){}
        public void moleculeTypeChanged(){}
        public void containerExploded(){}
    }
    
    /**
     * Does a linear search for a location that is suitably far away enough
     * from all other molecules.  This is generally used when the attempt to
     * place a molecule at a random location fails.  This is expensive in
     * terms of computational power, and should thus be used sparingly.
     * 
     * @return
     */
    private Point2D findOpenMoleculeLocation(){

    	/*
         * TODO: JPB TBD - This functionality should be moved into the phaseStateChanger.

        
        double posX, posY;
        double rangeX = m_normalizedContainerWidth - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        double rangeY = m_normalizedContainerHeight - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        for (int i = 0; i < rangeX / MIN_INITIAL_INTER_PARTICLE_DISTANCE; i++){
            for (int j = 0; j < rangeY / MIN_INITIAL_INTER_PARTICLE_DISTANCE; j++){
                posX = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (i * MIN_INITIAL_INTER_PARTICLE_DISTANCE);
                posY = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (j * MIN_INITIAL_INTER_PARTICLE_DISTANCE);
                
                // See if this position is available.
                boolean positionAvailable = true;
                for (int k = 0; k < m_numberOfAtoms / m_atomsPerMolecule; k++){
                    if (m_moleculeCenterOfMassPositions[k].distance( posX, posY ) < MIN_INITIAL_INTER_PARTICLE_DISTANCE){
                        positionAvailable = false;
                        break;
                    }
                }
                if (positionAvailable){
                    // We found an open position.
                    return new Point2D.Double( posX, posY );
                }
            }
        }
    */
        return null;
    }
}
