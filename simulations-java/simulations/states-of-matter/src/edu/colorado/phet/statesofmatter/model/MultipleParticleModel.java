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
import edu.colorado.phet.statesofmatter.model.engine.EngineFacade;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyAdjuster;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyCapper;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom2;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * This is the main class for the model portion of the "States of Matter"
 * simulation.
 *
 * @author John Blanco
 */
public class MultipleParticleModel {
    
	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Minimum container height fraction.
    public static final double MIN_CONTAINER_HEIGHT_FRACTION = 0.1;

    // TODO: JPB TBD - These constants are here as a result of the first attempt
    // to integrate Paul Beale's IDL implementation of the Verlet algorithm.
    // Eventually some or all of them will be moved.
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_ARGON_CRYSTAL = 7;
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_OXYGEN_CRYSTAL = 9;
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_NEON_CRYSTAL = 9;
    public static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.3;  // In particle diameters.
    public static final double DISTANCE_BETWEEN_DIATOMIC_PAIRS = 0.8;  // In particle diameters.
    public static final double DIATOMIC_FORCE_CONSTANT = 100; // For calculating force between diatomic pairs.
    public static final double TIME_STEP = 0.020;  // Time per simulation clock tick, in seconds.
    public static final double INITIAL_TEMPERATURE = 0.2;
    public static final double MAX_TEMPERATURE = 100.0;
    public static final double MIN_TEMPERATURE = 0.01;
    public static final double TEMPERATURE_STEP = -0.1;
    private static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    private static final double INITIAL_GRAVITATIONAL_ACCEL = 0.02;
    public static final double MAX_GRAVITATIONAL_ACCEL = 0.4;
    private static final double MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT = 0.025;
    private static final int    TICKS_PER_TEMP_ADJUSTEMENT = 10; // JPB TBD - I'm not sure if this is a reasonable
                                                                 // way to do this (i.e. that it is based on the
                                                                 // number of ticks).  Should it instead be based on
                                                                 // the time step defined above?
    private static final int MAX_NUM_ATOMS = 500;
    private static final double MIN_INJECTED_MOLECULE_VELOCITY = 0.5;
    private static final double MAX_INJECTED_MOLECULE_VELOCITY = 2.0;
    private static final double MAX_INJECTED_MOLECULE_ANGLE = Math.PI * 0.8;
    private static final double INJECTION_POINT_HORIZ_PROPORTION = 0.95;
    private static final double INJECTION_POINT_VERT_PROPORTION = 0.5;
    private static final int MAX_PLACEMENT_ATTEMPTS = 500; // For random placement when creating gas or liquid.
    private static final double SAFE_INTER_MOLECULE_DISTANCE = 2.0;
    public static final int DEFAULT_MOLECULE = StatesOfMatterConstants.NEON;
    
    // Constants used for setting the phase directly.
    public static final int PHASE_SOLID = 1;
    public static final int PHASE_LIQUID = 2;
    public static final int PHASE_GAS = 3;
    private static final double SOLID_TEMPERATURE = 0.15;
    private static final double LIQUID_TEMPERATURE = 0.45;
    private static final double GAS_TEMPERATURE = 1.0;
    private static final double MIN_INITIAL_INTER_PARTICLE_DISTANCE = 1.2;
    private static final double MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE = 2.5;
    
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
    
    // TODO: JPB TBD - Temp for debug, remove eventually.
    private static final boolean USE_NEW_PRESSURE_CALC_METHOD = true;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private double m_particleContainerHeight;
    private double m_targetContainerHeight;
    private double m_minAllowableContainerHeight;
    private final List m_particles = new ArrayList();
    private double m_totalEnergy;
    private boolean m_lidBlownOff = false;
    private EngineFacade m_engineFacade;
    IClock m_clock;
    private ArrayList _listeners = new ArrayList();
    
    // TODO: JPB TBD - These variables are here as a result of the first attempt
    // to integrate Paul Beale's IDL implementation of the Verlet algorithm.
    // Eventually some or all of them will be refactored to other objects.
    private Point2D [] m_atomPositions;
    private Vector2D [] m_atomVelocities;
    private Vector2D [] m_atomForces;
    private Vector2D [] m_nextAtomForces;
    private int m_numberOfAtoms;
    private int m_numberOfSafeAtoms;
    private int m_atomsPerMolecule;
    
    // JPB TBD - Important note to myself about refactoring: I think I can
    // consolidate a lot of the molecular arrays with the atomic arrays, like
    // the ones for the forces, and generalize them such that they are used
    // when the molecule and atom are the same thing.
    private Point2D [] m_moleculeCenterOfMassPositions;
    private Vector2D [] m_moleculeVelocities;
    private Vector2D [] m_moleculeForces;
    private Vector2D [] m_nextMoleculeForces;
    private double [] m_moleculeRotationAngles;
    private double [] m_moleculeRotationRates;
    private double [] m_moleculeTorques;
    private double [] m_nextMoleculeTorques;
    private double m_moleculeMass;
    private double m_moleculeRotationalInertia;
    double [] m_x0 = new double [3];
    double [] m_y0 = new double [3];

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
    private double  m_pressure;
    private double  m_pressure2;
    private PressureCalculator m_pressureCalculator;
    private int     m_thermostatType;
    private int     m_heightChangeCounter;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public MultipleParticleModel(IClock clock) {
        
        m_clock = clock;
        m_pressureCalculator = new PressureCalculator();
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
        return m_particles.size() / m_atomsPerMolecule;
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
        if (USE_NEW_PRESSURE_CALC_METHOD){
            return m_pressure2;
        }
        else{
            return m_pressure;
        }
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
        
        // Set the diameter and atoms/molecule based on the molecule type.
        switch (m_currentMolecule){
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            m_particleDiameter = OxygenAtom.RADIUS * 2;
            m_atomsPerMolecule = 2;
            break;
        case StatesOfMatterConstants.NEON:
            m_particleDiameter = NeonAtom.RADIUS * 2;
            m_atomsPerMolecule = 1;
            break;
        case StatesOfMatterConstants.ARGON:
            m_particleDiameter = ArgonAtom.RADIUS * 2;
            m_atomsPerMolecule = 1;
            break;
        case StatesOfMatterConstants.WATER:
            // Use a radius value that is artificially large, because the
            // educators have requested that water look "spaced out" so that
            // users can see the crystal structure better, and so that the
            // solid form will look larger (since water expands when frozen).
            m_particleDiameter = OxygenAtom.RADIUS * 2.9;
            m_atomsPerMolecule = 3;
            break;
        }

        // This causes a reset and puts the particles into predetermined
        // locations and energy levels.
        reset();
        
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

    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {
        
        // Get rid of any existing particles.
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
        
        // Clear out the pressure calculation.
        m_pressureCalculator.clear();
        m_pressure2 = 0;
        
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
            System.err.println("ERROR: Unrecognized particle type, using default number of layers.");
            break;
        }
        
        calculateMinAllowableContainerHeight();
        
        /*
        // Calculate the number of particles to create and simulate.
        int numInitialLayers = 0;
        switch (m_currentMolecule){
        case StatesOfMatterParticleType.OXYGEN:
            numInitialLayers = NUMBER_OF_LAYERS_IN_INITIAL_OXYGEN_CRYSTAL;
            break;
        case StatesOfMatterParticleType.NEON:
            numInitialLayers = NUMBER_OF_LAYERS_IN_INITIAL_NEON_CRYSTAL;
            break;
        case StatesOfMatterParticleType.ARGON:
            numInitialLayers = NUMBER_OF_LAYERS_IN_INITIAL_ARGON_CRYSTAL;
            break;
        default:
            System.err.println("Error: Unrecognized particle type, using default number of layers.");
            break;
        }
        m_numberOfAtoms = (2 * numInitialLayers) * (numInitialLayers - 1);

        // Initialize the vectors that define the normalized particle attributes.
        m_atomPositions  = new Point2D [MAX_NUM_ATOMS];
        m_atomVelocities = new Vector2D [MAX_NUM_ATOMS];
        m_atomForces     = new Vector2D [MAX_NUM_ATOMS];
        m_nextAtomForces = new Vector2D [MAX_NUM_ATOMS];
        
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[i] = new Point2D.Double();
            m_atomVelocities[i] = new Vector2D.Double();
            m_atomForces[i] = new Vector2D.Double();
            m_nextAtomForces[i] = new Vector2D.Double();
            
            // Add particle to model set.
            StatesOfMatterAtom particle = new StatesOfMatterAtom(0, 0, m_particleDiameter/2, 10);
            m_particles.add( particle );
            notifyParticleAdded( particle );
        }
        
        // Initialize the particle positions.
        solidifyParticles();
        
        // Initialize particle velocities.
        for (int i = 0; i < m_numberOfAtoms; i++){
            double temperatureSqrt = Math.sqrt( m_temperature );
            m_atomVelocities[i].setComponents( temperatureSqrt * m_rand.nextGaussian() , 
                    temperatureSqrt * m_rand.nextGaussian() );
        }
        syncParticlePositions();
        */
        
        /*
         * TODO: JPB TBD - This is the original code that John De Goes had written, which
         * is being kept for now as a reference, but should ultimately be deleted.
        ParticleCreationStrategy strategy = 
            new PackedHexagonalParticleCreationStrategy(StatesOfMatterConstants.ICE_CUBE_BOUNDS, 
                    StatesOfMatterConstants.PARTICLE_MASS, 
                    StatesOfMatterConstants.PARTICLE_RADIUS, 
                    StatesOfMatterConstants.PARTICLE_CREATION_CUSHION, 
                    StatesOfMatterConstants.ICE_CUBE_DIST_FROM_FLOOR);

        m_particles.clear();

        strategy.createParticles(m_particles, StatesOfMatterConstants.INITIAL_MAX_PARTICLE_COUNT);

        m_engineFacade = new EngineFacade(m_particles, EngineConfig.TEST);

        double targetKineticEnergy = 
            StatesOfMatterConstants.INITIAL_TOTAL_ENERGY_PER_PARTICLE * getNumParticles() - 
            m_engineFacade.measurePotentialEnergy();

        KineticEnergyAdjuster adjuster = new KineticEnergyAdjuster();

        adjuster.adjust(m_particles, targetKineticEnergy);

        m_totalEnergy = m_engineFacade.measureKineticEnergy() + m_engineFacade.measurePotentialEnergy();
        */

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
        m_minAllowableContainerHeight = m_particleDiameter * m_particleDiameter * m_numberOfAtoms / m_atomsPerMolecule / 
                StatesOfMatterConstants.PARTICLE_CONTAINER_WIDTH * 2;
    }
    
    
    /**
     * Set the phase of the particles in the simulation.
     * 
     * @param state
     */
    public void setPhase(int state){
        
        switch (state){
        case PHASE_SOLID:
            if (m_atomsPerMolecule == 1){
                solidifyMonatomicMolecules();
            }
            else if (m_atomsPerMolecule == 2){
                solidifyDiatomicMolecules();
            }
            else if ( m_atomsPerMolecule == 3){
                solidifyTriatomicMolecules();
            }
            break;
            
        case PHASE_LIQUID:
            if (m_atomsPerMolecule == 1){
                liquifyMonatomicMolecules();
            }
            else{
                liquifyMultiAtomicMolecules();
            }
            break;
            
        case PHASE_GAS:
            if (m_atomsPerMolecule == 1){
                gasifyMonatomicMolecules();
            }
            else{
                gasifyMultiAtomicMolecules();
            }
            break;
            
        default:
            System.err.println("Error: Invalid state specified.");
            // Treat is as a solid.
            solidifyMonatomicMolecules();
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

        if (( m_numberOfAtoms + m_atomsPerMolecule <= MAX_NUM_ATOMS ) &&
            ( m_normalizedContainerHeight > injectionPointY * 1.05)){

            double angle = Math.PI + ((m_rand.nextDouble() - 0.5) * MAX_INJECTED_MOLECULE_ANGLE);
            double velocity = MIN_INJECTED_MOLECULE_VELOCITY + (m_rand.nextDouble() *
                    (MAX_INJECTED_MOLECULE_VELOCITY - MIN_INJECTED_MOLECULE_VELOCITY));
            double xVel = Math.cos( angle ) * velocity;
            double yVel = Math.sin( angle ) * velocity;
            if (m_atomsPerMolecule == 1){
                // Add particle and its velocity and forces to normalized set.
                m_atomPositions[m_numberOfAtoms] = 
                    new Point2D.Double( injectionPointX, injectionPointY );
                m_atomVelocities[m_numberOfAtoms] = new Vector2D.Double( xVel, yVel );
                m_atomForces[m_numberOfAtoms] = new Vector2D.Double();
                m_nextAtomForces[m_numberOfAtoms] = new Vector2D.Double();
                m_numberOfAtoms++;
                
                // Add particle to model set.
                StatesOfMatterAtom particle;
                switch (m_currentMolecule){
                case StatesOfMatterConstants.MONATOMIC_OXYGEN:
                    particle = new OxygenAtom(0, 0);
                    break;
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
            else if (m_atomsPerMolecule == 2){

                assert m_currentMolecule == StatesOfMatterConstants.DIATOMIC_OXYGEN;
                
                // Add particles to model set.
                for (int i = 0; i < m_atomsPerMolecule; i++){
                    StatesOfMatterAtom atom;
                    atom = new OxygenAtom(0, 0);
                    m_particles.add( atom );
                    notifyParticleAdded( atom );
                    m_atomPositions[m_numberOfAtoms + i] = new Point2D.Double(); 
                }
                
                m_numberOfAtoms += 2;
                int numberOfMolecules = m_numberOfAtoms / 2;
                
                m_moleculeCenterOfMassPositions[numberOfMolecules - 1] = 
                    new Point2D.Double( injectionPointX, injectionPointY );
                m_moleculeVelocities[numberOfMolecules - 1] = new Vector2D.Double( xVel, yVel );
                m_moleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                m_nextMoleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                m_moleculeRotationRates[numberOfMolecules - 1] = (m_rand.nextDouble() - 0.5) * (Math.PI / 2);
                
                updateDiatomicAtomPositions();
                syncParticlePositions();
            }
            else if (m_atomsPerMolecule == 3){

                assert m_currentMolecule == StatesOfMatterConstants.WATER;
                
                // Add atoms to model set.
                StatesOfMatterAtom atom;
                atom = new OxygenAtom(0, 0);
                m_particles.add( atom );
                notifyParticleAdded( atom );
                m_atomPositions[m_numberOfAtoms] = new Point2D.Double(); 
                atom = new HydrogenAtom(0, 0);
                m_particles.add( atom );
                notifyParticleAdded( atom );
                m_atomPositions[m_numberOfAtoms + 1] = new Point2D.Double(); 
                atom = new HydrogenAtom(0, 0);
                m_particles.add( atom );
                notifyParticleAdded( atom );
                m_atomPositions[m_numberOfAtoms + 2] = new Point2D.Double(); 
                
                m_numberOfAtoms += 3;
                int numberOfMolecules = m_numberOfAtoms / 3;
                
                m_moleculeCenterOfMassPositions[numberOfMolecules - 1] = 
                    new Point2D.Double( injectionPointX, injectionPointY );
                m_moleculeVelocities[numberOfMolecules - 1] = new Vector2D.Double( xVel, yVel );
                m_moleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                m_nextMoleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                
                updateTriatomicAtomPositions();
                syncParticlePositions();
            }
        }
        
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
    
    private void handleClockTicked(ClockEvent clockEvent) {
        
        // Adjust the particle container height if needed.
        if (!m_lidBlownOff) {
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
            m_pressure2 = m_pressure2 * 0.95;
        }
        
        // Execute the Verlet algorithm.
        for (int i = 0; i < 8; i++ ){
            if (m_atomsPerMolecule == 1){
                verletMonatomic();
                if (USE_NEW_PRESSURE_CALC_METHOD){
                    notifyPressureChanged(); // TODO: JPB TBD - This is temporary until the approach for
                                             // actually detecting pressure changes is worked out.
                }
            }
            else if (m_atomsPerMolecule == 2){
                verletDiatomic();
                if (USE_NEW_PRESSURE_CALC_METHOD){
                    notifyPressureChanged(); // TODO: JPB TBD - This is temporary until the approach for
                                             // actually detecting pressure changes is worked out.
                }
            }
            else if (m_atomsPerMolecule == 3){
                verletTriatomic();
                if (USE_NEW_PRESSURE_CALC_METHOD){
                    notifyPressureChanged(); // TODO: JPB TBD - This is temporary until the approach for
                                             // actually detecting pressure changes is worked out.
                }
            }
        }
        syncParticlePositions();
        
        if (!USE_NEW_PRESSURE_CALC_METHOD){
            if (m_pressure != m_pressureCalculator.getPressure()){
                // The pressure has changed.  Send out notifications and update
                // the current value.
                m_pressure = m_pressureCalculator.getPressure();
                notifyPressureChanged();
            }
        }
        
        // Adjust the temperature.
        m_tempAdjustTickCounter++;
        if ((m_tempAdjustTickCounter > TICKS_PER_TEMP_ADJUSTEMENT) && m_heatingCoolingAmount != 0){
            m_tempAdjustTickCounter = 0;
            m_temperatureSetPoint += m_heatingCoolingAmount;
            if (m_temperatureSetPoint >= MAX_TEMPERATURE){
                m_temperatureSetPoint = MAX_TEMPERATURE;
            }
            else if (m_temperatureSetPoint <= MIN_TEMPERATURE){
                m_temperatureSetPoint = MIN_TEMPERATURE;
            }
            notifyTemperatureChanged();
        }
        
        /*
         * TODO: JPB TBD - This is the original code that John De Goes had written, which
         * is being kept for now as a reference, but should ultimately be deleted.

        for (int i = 0; i < StatesOfMatterConstants.COMPUTATIONS_PER_RENDER; i++) {
            ForceComputation computation = m_engineFacade.step(clockEvent.getSimulationTimeChange());

            computation.apply(m_particles);

            // Cap the kinetic energy:
            capKineticEnergy();
        }

        // Readjust to conserve total energy:
        conserveTotalEnergy();
        */
    }
    
    /**
     * Initialize the various model components to handle a simulation in which
     * all the molecules are single atoms.
     * 
     * @param moleculeID
     */
    private void initializeMonotomic(int moleculeID){
        
        // TODO: JPB TBD - Decide whether to remove support for monatomic oxygen at some point.
        // Verify that a valid molecule ID was provided.
        assert (moleculeID == StatesOfMatterConstants.NEON) || 
               (moleculeID == StatesOfMatterConstants.ARGON) || 
               (moleculeID == StatesOfMatterConstants.MONATOMIC_OXYGEN);
        
        // Determine the number of molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container.
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
        
        m_numberOfAtoms = (int)Math.pow( StatesOfMatterConstants.CONTAINER_BOUNDS.width / 
                (( particleDiameter + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL ) * 3), 2);
        m_numberOfSafeAtoms = m_numberOfAtoms;
        
        // Initialize the vectors that define the normalized particle attributes.
        m_atomPositions  = new Point2D [MAX_NUM_ATOMS];
        m_atomVelocities = new Vector2D [MAX_NUM_ATOMS];
        m_atomForces     = new Vector2D [MAX_NUM_ATOMS];
        m_nextAtomForces = new Vector2D [MAX_NUM_ATOMS];
        m_atomsPerMolecule = 1;
        
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[i] = new Point2D.Double();
            m_atomVelocities[i] = new Vector2D.Double();
            m_atomForces[i] = new Vector2D.Double();
            m_nextAtomForces[i] = new Vector2D.Double();
            
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
        
        // Initialize the particle positions.
        solidifyMonatomicMolecules();
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
        m_atomVelocities = new Vector2D [MAX_NUM_ATOMS];
        m_atomForces     = new Vector2D [MAX_NUM_ATOMS];
        m_nextAtomForces = new Vector2D [MAX_NUM_ATOMS];
        
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[i] = new Point2D.Double();
            m_atomVelocities[i] = new Vector2D.Double();
            m_atomForces[i] = new Vector2D.Double();
            m_nextAtomForces[i] = new Vector2D.Double();
            
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
        m_atomVelocities = new Vector2D [MAX_NUM_ATOMS];
        m_atomForces     = new Vector2D [MAX_NUM_ATOMS];
        m_nextAtomForces = new Vector2D [MAX_NUM_ATOMS];
        
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[i] = new Point2D.Double();
            m_atomVelocities[i] = new Vector2D.Double();
            m_atomForces[i] = new Vector2D.Double();
            m_nextAtomForces[i] = new Vector2D.Double();
            
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
        setTemperature( GAS_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        for (int i = 0; i < m_numberOfAtoms; i++){
            // Temporarily position the particles at (0,0).
            m_atomPositions[i].setLocation( 0, 0 );
            
            // Assign each particle an initial velocity.
            m_atomVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
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
                    System.err.println("WARNING: Unable to locate usable atom position randomly, proceeding anyway.");
                    m_atomPositions[i].setLocation( newPosX, newPosY );
                }
            }
        }
        syncParticlePositions();
    }
    
    /**
     * Randomize the positions of the molecules that comprise a gas.  This
     * works for diatomic and triatomic molecules.
     */
    private void gasifyMultiAtomicMolecules(){
        
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
                    System.err.println("Warning: Unable to locate usable molecule position randomly.");
                    Point2D openPoint = findOpenMoleculeLocation();
                    if (openPoint != null){
                        System.err.println("Warning: Linear search returned point " + openPoint);
                        m_moleculeCenterOfMassPositions[i].setLocation( openPoint );
                    }
                    else{
                        System.err.println("Warning: Linear also unable to find usable position.");
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
    }

    /**
     * Set the atoms to be in a liquid state.
     */
    private void liquifyMonatomicMolecules(){
        
        setTemperature( LIQUID_TEMPERATURE );
        double temperatureSqrt = Math.sqrt( m_temperatureSetPoint );
        
        // Set the initial velocity for each of the atoms based on the new
        // temperature.

        for (int i = 0; i < m_numberOfAtoms; i++){
            // Assign each particle an initial velocity.
            m_atomVelocities[i].setComponents( temperatureSqrt * m_rand.nextGaussian(), 
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
    }

    /**
     * Set the molecules to be in a liquid state.  This changes the
     * temperature and also instantly places the various molecules into
     * a blob.  This method works for di- and tri-atomic molecules.
     */
    private void liquifyMultiAtomicMolecules(){
        
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
    }

    /**
     * Create positions corresponding to a hexagonal 2d "crystal" structure
     * for a set of particles.  Note that this assumes a normalized value
     * of 1.0 for the diameter of the particles.
     */
    private void solidifyMonatomicMolecules(){

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
                m_atomVelocities[(i * particlesPerLayer) + j].setComponents( temperatureSqrt * rand.nextGaussian(), 
                        temperatureSqrt * rand.nextGaussian() );
            }
        }
    }
    
    /**
     * Create positions for a solid composed of diatomic molecules,
     * such as diatomic oxygen (i.e. O2).
     */
    private void solidifyDiatomicMolecules(){

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
    }
    
    /**
     * Create positions for a solid composed of triatomic molecules,
     * such as water.
     */
    private void solidifyTriatomicMolecules(){

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
    }

    /**
     * Set the positions of the non-normalized particles based on the positions
     * of the normalized ones.
     */
    private void syncParticlePositions(){
        // TODO: JPB TBD - This way of un-normalizing needs to be worked out,
        // and setting it as done below is a temporary thing.
        double positionMultiplier = m_particleDiameter;
        for (int i = 0; i < m_numberOfAtoms; i++){
            ((StatesOfMatterAtom)m_particles.get( i )).setPosition( 
                    m_atomPositions[i].getX() * positionMultiplier, 
                    m_atomPositions[i].getY() * positionMultiplier);
        }
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of particles.
     */
    private void verletMonatomic(){
        
        double kineticEnergy = 0;
        
        double timeStepSqrHalf = TIME_STEP * TIME_STEP * 0.5;
        double timeStepHalf = TIME_STEP / 2;
        
        // Update the positions of all particles based on their current
        // velocities and the forces acting on them.
        for (int i = 0; i < m_numberOfAtoms; i++){
            double xPos = m_atomPositions[i].getX() + (TIME_STEP * m_atomVelocities[i].getX()) + 
                    (timeStepSqrHalf * m_atomForces[i].getX());
            double yPos = m_atomPositions[i].getY() + (TIME_STEP * m_atomVelocities[i].getY()) + 
                    (timeStepSqrHalf * m_atomForces[i].getY());
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
            m_nextAtomForces[i].setComponents( 0, 0 );
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_atomPositions[i], m_normalizedContainerWidth, m_normalizedContainerHeight, 
                    m_nextAtomForces[i]);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            m_pressureCalculator.accumulatePressureValue( m_nextAtomForces[i] );
            if (m_nextAtomForces[i].getY() < 0){
                pressureZoneWallForce += -m_nextAtomForces[i].getY();
                interactionOccurredWithTop = true;
            }
            else if (m_atomPositions[i].getY() > m_normalizedContainerHeight / 2){
            	// If the particle bounced on one of the walls above the midpoint, add
            	// in that value to the pressure.
            	pressureZoneWallForce += Math.abs( m_nextAtomForces[i].getX() );
            }
            
            
            // Add in the effect of gravity.
            m_nextAtomForces[i].setY( m_nextAtomForces[i].getY() - m_gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        // TODO: JPB TBD - Clean this up if we end up using it.
        double pressureCalcWeighting = 0.9995;
        m_pressure2 = (1 - pressureCalcWeighting) * (pressureZoneWallForce / (m_normalizedContainerWidth + m_normalizedContainerHeight)) + 
                pressureCalcWeighting * m_pressure2;
        
        // Advance the moving average window of the pressure calculator.
        m_pressureCalculator.advanceWindow();
        
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
                        System.out.println("Spacing less than min distance: " + Math.sqrt( distanceSqrd ));
                        distanceSqrd = MIN_DISTANCE_SQUARED;
                    }
                    double r2inv = 1 / distanceSqrd;
                    double r6inv = r2inv * r2inv * r2inv;
                    double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
                    force.setX( dx * forceScaler );
                    force.setY( dy * forceScaler );
                    m_nextAtomForces[i].add( force );
                    m_nextAtomForces[j].subtract( force );
                    m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                }
            }
        }
        
        // Calculate the new velocities based on the old ones and the forces
        // that are acting on the particle.
        Vector2D.Double velocityIncrement = new Vector2D.Double();
        for (int i = 0; i < m_numberOfAtoms; i++){
            velocityIncrement.setX( timeStepHalf * (m_atomForces[i].getX() + m_nextAtomForces[i].getX()));
            velocityIncrement.setY( timeStepHalf * (m_atomForces[i].getY() + m_nextAtomForces[i].getY()));
            m_atomVelocities[i].add( velocityIncrement );
            kineticEnergy += ((m_atomVelocities[i].getX() * m_atomVelocities[i].getX()) + 
                    (m_atomVelocities[i].getY() * m_atomVelocities[i].getY())) / 2;
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
                if (m_temperatureSetPoint == 0){
                    temperatureScaleFactor = 0;
                }
                else{
                    temperatureScaleFactor = Math.sqrt( m_temperatureSetPoint * m_numberOfAtoms / kineticEnergy );
                }
                kineticEnergy = 0;
                for (int i = 0; i < m_numberOfAtoms; i++){
                    m_atomVelocities[i].setComponents( m_atomVelocities[i].getX() * temperatureScaleFactor, 
                            m_atomVelocities[i].getY() * temperatureScaleFactor );
                    kineticEnergy += ((m_atomVelocities[i].getX() * m_atomVelocities[i].getX()) + 
                            (m_atomVelocities[i].getY() * m_atomVelocities[i].getY())) / 2;
                }
            }
            else if ((m_thermostatType == ANDERSEN_THERMOSTAT) ||
                     (m_thermostatType == ADAPTIVE_THERMOSTAT && !temperatureIsChanging)){
                // Modified Andersen Thermostat to maintain fixed temperature
                // modification to reduce abruptness of heat bath interactions.
                // For bare Andersen, set gamma=0.0d0.
                double gamma = 0.9999;
                for (int i = 0; i < m_numberOfAtoms; i++){
                    double xVel = m_atomVelocities[i].getX() * gamma + m_rand.nextGaussian() * Math.sqrt(  m_temperatureSetPoint * (1 - Math.pow(gamma, 2)) );
                    double yVel = m_atomVelocities[i].getY() * gamma + m_rand.nextGaussian() * Math.sqrt(  m_temperatureSetPoint * (1 - Math.pow(gamma, 2)) );
                    m_atomVelocities[i].setComponents( xVel, yVel );
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
            m_atomForces[i].setComponents( m_nextAtomForces[i].getX(), m_nextAtomForces[i].getY() );
        }
    }

    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of diatomic molecules.
     */
    private void verletDiatomic(){
        
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
            m_pressureCalculator.accumulatePressureValue( m_nextMoleculeForces[i] );
            if (m_nextMoleculeForces[i].getY() < 0){
                totalTopForce += -m_nextMoleculeForces[i].getY();
                interactionOccurredWithTop = true;
            }
            
            // Add in the effect of gravity.
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - m_gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        // TODO: JPB TBD - Clean this up if we end up using it.
        double pressureCalcGammaWeighting = 0.999;
        m_pressure2 = (1 - pressureCalcGammaWeighting) * (totalTopForce / m_normalizedContainerWidth) + 
                pressureCalcGammaWeighting * m_pressure2;
        
        // Advance the moving average window of the pressure calculator.
        m_pressureCalculator.advanceWindow();
        
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
                                System.out.println("Spacing less than min distance: " + Math.sqrt( distanceSquared ));
                                distanceSquared = MIN_DISTANCE_SQUARED;
                            }

                            // Calculate the Lennard-Jones interaction forces.
                            double r2inv = 1 / distanceSquared;
                            double r6inv = r2inv * r2inv * r2inv;
                            double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
                            if (forceScaler > 1000){
                                // TODO: JPB TBD - This is here to help track down when things
                                // get crazy, and should be removed eventually.
                                System.err.println("Big force, forceScaler = " + forceScaler);
                            }
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
                if (m_temperatureSetPoint == 0){
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
                double velocityScalingFactor = Math.sqrt( m_temperatureSetPoint * massInverse * (1 - Math.pow( gamma, 2)));
                double rotationScalingFactor = Math.sqrt( m_temperatureSetPoint * inertiaInverse * (1 - Math.pow( gamma, 2)));
                
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
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of triatomic molecules.
     */
    private void verletTriatomic(){
        
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
            m_pressureCalculator.accumulatePressureValue( m_nextMoleculeForces[i] );
            if (m_nextMoleculeForces[i].getY() < 0){
                totalTopForce += -m_nextMoleculeForces[i].getY();
                interactionOccurredWithTop = true;
            }
            
            // Add in the effect of gravity.
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - m_gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        // TODO: JPB TBD - Clean this up if we end up using it.
        double pressureCalcWeighting = 0.9995;
        m_pressure2 = (1 - pressureCalcWeighting) * (totalTopForce / m_normalizedContainerWidth) + 
                pressureCalcWeighting * m_pressure2;
        
        // Advance the moving average window of the pressure calculator.
        m_pressureCalculator.advanceWindow();
        
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
                        System.out.println("Spacing less than min distance: " + Math.sqrt( distanceSquared ));
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
                    if (forceScaler > 1000){
                        // TODO: JPB TBD - This is here to help track down when things
                        // get crazy, and should be removed eventually.
                        System.err.println("Big force, forceScaler = " + forceScaler);
                    }
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
//        System.out.println("kecm/n = " + kecm/numberOfMolecules + ", kerot/n = " + kerot/numberOfMolecules);
        
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
                if (m_temperatureSetPoint == 0){
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
                double velocityScalingFactor = Math.sqrt( m_temperatureSetPoint * massInverse * (1 - Math.pow( gamma, 2)));
                double rotationScalingFactor = Math.sqrt( m_temperatureSetPoint * inertiaInverse * (1 - Math.pow( gamma, 2)));
                
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
    }
    
    /**
     * Position the individual atoms based on their centers of mass and
     * initial angle.  !IMPORTANT NOTE - This assumes that the molecules
     * are positioned in diatomic pairs in the arrays.  For instance,
     * the atoms at indexes 0 & 1 are one pair, at 2 & 3 are a pair, and so
     * on.
     */
    private void updateDiatomicAtomPositions(){
      
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
    }

    /**
     * Position the individual atoms based on their centers of mass and
     * initial angle.  !IMPORTANT NOTE - This assumes that the molecules
     * are positioned in groups of three in the arrays.  For instance, a
     * water molecule will be Oxygen followed by two Hydrogens.
     */
    private void updateTriatomicAtomPositions(){
      
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
        
        for (int i = m_numberOfSafeAtoms; i < m_numberOfAtoms; i += m_atomsPerMolecule){
            
            boolean moleculeIsUnsafe = false;

            // Find out if this molecule is still to close to all the "safe"
            // molecules.
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
                    // There is at least one unsafe atom in front of this one
                    // in the arrays, so some swapping must be done before the
                    // number of safe atoms can be incremented.
                    // TODO: JPB TBD - This is ugly, and should be radically
                    // improved when the refactoring of the model takes place.
                    
                    // Swap the safe atom with the first unsafe one.
                    Point2D tempAtomPosition;
                    Vector2D tempAtomVelocity;
                    Vector2D tempAtomForce;
                    
                    for (int j = 0; j < m_atomsPerMolecule; j++){
                        tempAtomPosition = m_atomPositions[m_numberOfSafeAtoms + j];
                        tempAtomVelocity = m_atomVelocities[m_numberOfSafeAtoms + j];
                        tempAtomForce = m_atomForces[m_numberOfSafeAtoms + j];
                        m_atomPositions[m_numberOfSafeAtoms + j] = m_atomPositions[i + j];
                        m_atomVelocities[m_numberOfSafeAtoms + j] = m_atomVelocities[i + j];
                        m_atomForces[m_numberOfSafeAtoms + j] = m_atomForces[i + j];
                        m_atomPositions[i + j] = tempAtomPosition;
                        m_atomVelocities[i + j] = tempAtomVelocity;
                        m_atomForces[i + j] = tempAtomForce;
                    }
                    
                    // Now swap the molecule.  Note that we don't worry about
                    // torque here because there shouldn't be any until the
                    // molecule is deemed safe.
                    Point2D tempMoleculeCenterOfMassPosition;
                    Vector2D tempMoleculeVelocity;
                    Vector2D tempMoleculeForce;
                    double tempMoleculeRotationAngle;
                    double tempMoleculeRotationRate;
                    
                    if (m_atomsPerMolecule > 1){
                        int firstUnsafeMoleculeIndex = m_numberOfSafeAtoms / m_atomsPerMolecule;
                        int safeMoleculeIndex = i / m_atomsPerMolecule;
                        tempMoleculeCenterOfMassPosition = m_moleculeCenterOfMassPositions[firstUnsafeMoleculeIndex];
                        tempMoleculeVelocity = m_moleculeVelocities[firstUnsafeMoleculeIndex];
                        tempMoleculeForce = m_moleculeForces[firstUnsafeMoleculeIndex];
                        tempMoleculeRotationAngle = m_moleculeRotationAngles[firstUnsafeMoleculeIndex];
                        tempMoleculeRotationRate = m_moleculeRotationRates[firstUnsafeMoleculeIndex];
                        m_moleculeCenterOfMassPositions[firstUnsafeMoleculeIndex] = m_moleculeCenterOfMassPositions[safeMoleculeIndex];
                        m_moleculeVelocities[firstUnsafeMoleculeIndex] = m_moleculeVelocities[safeMoleculeIndex];
                        m_moleculeForces[firstUnsafeMoleculeIndex] = m_moleculeForces[safeMoleculeIndex];
                        m_moleculeRotationAngles[firstUnsafeMoleculeIndex] = m_moleculeRotationAngles[safeMoleculeIndex];
                        m_moleculeRotationRates[firstUnsafeMoleculeIndex] = m_moleculeRotationRates[safeMoleculeIndex];
                        m_moleculeCenterOfMassPositions[safeMoleculeIndex] = tempMoleculeCenterOfMassPosition;
                        m_moleculeVelocities[safeMoleculeIndex] = tempMoleculeVelocity;
                        m_moleculeForces[safeMoleculeIndex] = tempMoleculeForce;
                        m_moleculeRotationAngles[safeMoleculeIndex] = tempMoleculeRotationAngle;
                        m_moleculeRotationRates[safeMoleculeIndex] = tempMoleculeRotationRate;
                    }
                }
                m_numberOfSafeAtoms += m_atomsPerMolecule;
            }
        }
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
        if ((resultantForce.getX() > 1000) || (resultantForce.getY() > 1000)){
            // TODO: JPB TBD - Here to help track down when things get crazy,
            // remove eventually.
            System.err.println("Big wall force, = " + resultantForce);
        }
    }
    
    /**
     * Take the steps necessary to start the "explosion" of the container.
     */
    private void explode() {
        
        if (m_lidBlownOff == false) {
            
            m_lidBlownOff = true;
            notifyContainerExploded();
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
        
        switch (m_currentMolecule){
        
        case StatesOfMatterConstants.NEON:
            if (m_temperatureSetPoint <= 0.25){
                temperatureInKelvin = m_temperatureSetPoint * 60;
            }
            else if (m_temperatureSetPoint <= 0.5){
                temperatureInKelvin = m_temperatureSetPoint * 55 + 1.25;
            }
            else {
                temperatureInKelvin = m_temperatureSetPoint * 62.5 - 2.5;
            }
            break;
            
        case StatesOfMatterConstants.ARGON:
            if (m_temperatureSetPoint <= 0.25){
                temperatureInKelvin = m_temperatureSetPoint * 213;
            }
            else {
                temperatureInKelvin = m_temperatureSetPoint * 163.8 + 12.3;
            }
            break;

        case StatesOfMatterConstants.WATER:
            if (m_temperatureSetPoint <= 0.275){
                temperatureInKelvin = m_temperatureSetPoint * 1000;
            }
            else{
                temperatureInKelvin = m_temperatureSetPoint * 440 + 154;
            }
            break;
            
        case StatesOfMatterConstants.DIATOMIC_OXYGEN:
            temperatureInKelvin = m_temperatureSetPoint * 180;
            break;
            
        default:
            temperatureInKelvin = 0;
            break;
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
        
        double translationalKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        double numberOfMolecules = m_numberOfAtoms / m_atomsPerMolecule;
        double kineticEnergyPerMolecule;
        
        if (m_atomsPerMolecule == 1){
            for (int i = 0; i < m_numberOfAtoms; i++){
                translationalKineticEnergy += ((m_atomVelocities[i].getX() * m_atomVelocities[i].getX()) + 
                        (m_atomVelocities[i].getY() * m_atomVelocities[i].getY())) / 2;
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
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void resetOccurred();
        
        /**
         * Inform listeners that a new particle has been added to the model.
         */
        public void particleAdded(StatesOfMatterAtom particle);
        
        /**
         * Inform listeners that the temperature of the system has changed.
         */
        public void temperatureChanged();
        
        /**
         * Inform listeners that the pressure of the system has changed.
         */
        public void pressureChanged();

        /**
         * Inform listeners that the size of the container has changed.
         */
        public void containerSizeChanged();
        
        /**
         * Inform listeners that the type of molecule being simulated has
         * changed.
         */
        public void moleculeTypeChanged();

        /**
         * Inform listeners that the container has exploded.
         */
        public void containerExploded();

    }
    
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
        
        return null;
    }
    
    /**
     * This class enables the user to calculate a moving average of the
     * pressure within the container.
     *
     * @author John Blanco
     */
    private class PressureCalculator{
        
        private final static int WINDOW_SIZE = 1000;
        
        private double [] m_pressueSamples;
        private int       m_accumulationPosition;
        private int       m_numSamples;
        
        public PressureCalculator(){
            m_pressueSamples = new double[WINDOW_SIZE];
            m_accumulationPosition = 0;
            m_numSamples = 0;
        }
        
        public void accumulatePressureValue(Vector2D forceVector){
            if (forceVector.getY() > 0){
                // Add this value, since it corresponds to the force on the
                // bottom of the container, which is what we use for
                // calculating the pressure.
                m_pressueSamples[m_accumulationPosition] += forceVector.getY() / m_normalizedContainerWidth;
            }
            
            // JPB TBD - This is the old pressure calculation, which uses
            // the force from the two walls.  Keep for a while until we
            // determine that using the bottom is acceptable.  Aug 15, 2008.
//            m_pressueSamples[m_accumulationPosition] += Math.abs(  forceVector.getX() ) / (m_normalizedContainerHeight * 2);
        }
        
        public double getPressure(){
            
            if (m_numSamples == 0){
                // Prevent divide by 0 issues.
                return 0;
            }
            
            // Calculate the pressure as the moving average of all accumulated
            // pressure samples.
            double accumulatedPressure = 0;
            for (int i = 0; i < m_numSamples; i++){
                accumulatedPressure += m_pressueSamples[i];
            }
            return accumulatedPressure/m_numSamples;
        }
        
        public void advanceWindow(){
            m_accumulationPosition = (m_accumulationPosition + 1) % WINDOW_SIZE;
            m_pressueSamples[m_accumulationPosition] = 0;
            m_numSamples = m_numSamples >= WINDOW_SIZE ? m_numSamples : m_numSamples + 1; 
        }
        
        public void clear(){
            m_accumulationPosition = 0;
            m_numSamples = 0;
            for (int i = 0; i < WINDOW_SIZE; i++){
                m_pressueSamples[i] = 0;
            }
        }
    }
}
