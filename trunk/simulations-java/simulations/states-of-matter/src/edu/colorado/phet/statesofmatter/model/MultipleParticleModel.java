/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Point2D;
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
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import edu.colorado.phet.statesofmatter.model.engine.EngineFacade;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyAdjuster;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyCapper;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
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

    // TODO: JPB TBD - These constants are here as a result of the first attempt
    // to integrate Paul Beale's IDL implementation of the Verlet algorithm.
    // Eventually some or all of them will be moved.
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_ARGON_CRYSTAL = 7;
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_OXYGEN_CRYSTAL = 9;
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_NEON_CRYSTAL = 9;
    public static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.3;  // In particle diameters.
    public static final double DISTANCE_BETWEEN_DIATOMIC_PAIRS = 0.8;  // In particle diameters.
    public static final double DIATOMIC_FORCE_CONSTANT = 100; // For calculating force between diatomic pairs.
    public static final double TIME_STEP = Math.pow( 0.5, 6.0 );
    public static final double INITIAL_TEMPERATURE = 0.2;
    public static final double MAX_TEMPERATURE = 2.0;
    public static final double MIN_TEMPERATURE = 0.001;
    public static final double TEMPERATURE_STEP = -0.1;
    private static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    private static final double INITIAL_GRAVITATIONAL_ACCEL = 0.02;
    public static final double MAX_GRAVITATIONAL_ACCEL = 1;
    private static final double MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT = 0.025;
    private static final int    TICKS_PER_TEMP_ADJUSTEMENT = 10; // JPB TBD - I'm not sure if this is a reasonable
                                                                 // way to do this (i.e. that it is based on the
                                                                 // number of ticks).  Should it instead be based on
                                                                 // the time step defined above?
    private static final int MAX_NUM_ATOMS = 500;
    private static final double INJECTED_MOLECULE_VELOCITY = 1.0;
    private static final double INJECTION_POINT_HORIZ_PROPORTION = 0.95;
    private static final double INJECTION_POINT_VERT_PROPORTION = 0.5;
    private static final int MAX_PLACEMENT_ATTEMPTS = 500; // For random placement when creating gas or liquid.
    private static final double SAFE_INTER_MOLECULE_DISTANCE = 2.0;
    
    // Constants used for setting the phase directly.
    public static final int PHASE_SOLID = 1;
    public static final int PHASE_LIQUID = 2;
    public static final int PHASE_GAS = 3;
    private static final double SOLID_TEMPERATURE = 0.15;
    private static final double LIQUID_TEMPERATURE = 0.5;
    private static final double GAS_TEMPERATURE = 1.0;
    private static final double MIN_INITIAL_INTER_PARTICLE_DISTANCE = 1.5;
    
    // Supported molecules.
    public static final int NEON = 1;
    public static final int ARGON = 2;
    public static final int MONATOMIC_OXYGEN = 3;
    public static final int DIATOMIC_OXYGEN = 4;
    public static final int WATER = 5;
    
    // Possible thermostat settings.
    public static final int NO_THERMOSTAT = 0;
    public static final int ISOKINETIC_THERMOSTAT = 1;
    public static final int ANDERSEN_THERMOSTAT = 2;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final List m_particles = new ArrayList();
    private double m_totalEnergy;
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

    private double m_normalizedContainerWidth;
    private double m_normalizedContainerHeight;
    private double m_potentialEnergy;
    private Random m_rand = new Random();
    private double m_temperature;
    private double m_gravitationalAcceleration;
    private double m_heatingCoolingAmount;
    private int    m_tempAdjustTickCounter;
    private int    m_currentMolecule;
    private double m_particleDiameter;
    private double m_pressure;
    private PressureCalculator m_pressureCalculator;
    private int    m_thermostatType;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public MultipleParticleModel(IClock clock) {
        
        m_clock = clock;
        m_pressureCalculator = new PressureCalculator();
        setThermostatType( ISOKINETIC_THERMOSTAT );
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        });
        
        // Set the default particle type.
        setMolecule( NEON );
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public List getParticles() {
        return Collections.unmodifiableList(m_particles);
    }

    public int getNumMolecules() {
        return m_particles.size() / m_atomsPerMolecule;
    }

    public StatesOfMatterAtom getParticle(int i) {
        return (StatesOfMatterAtom)m_particles.get(i);
    }

    public ParticleContainer getParticleContainer() {
        return new RectangularParticleContainer(StatesOfMatterConstants.CONTAINER_BOUNDS);
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
            m_temperature = MAX_TEMPERATURE;
        }
        else if (newTemperature < MIN_TEMPERATURE){
            m_temperature = MIN_TEMPERATURE;
        }
        else{
            m_temperature = newTemperature;
        }

        notifyTemperatureChanged();
    }

    public double getTemperature(){
        return m_temperature;
    }
    
    public double getNormalizedTemperature(){
        return (getTemperature() / MAX_TEMPERATURE);
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
    
    public double getPressure(){
        return m_pressure;
    }
    
    public int getMolecule(){
        return m_currentMolecule;
    }
    
    public void setMolecule(int moleculeID){
        
        // Verify that this is a supported value.
        if ((moleculeID != DIATOMIC_OXYGEN) &&
            (moleculeID != NEON) &&
            (moleculeID != ARGON) &&
            (moleculeID != WATER)){
            
            System.err.println("Error: Unsupported molecule type.");
            assert false;
            moleculeID = NEON;
        }
        
        m_currentMolecule = moleculeID;
        
        // Set the diameter and atoms/molecule based on the molecule type.
        switch (m_currentMolecule){
        case DIATOMIC_OXYGEN:
            m_particleDiameter = OxygenAtom.RADIUS * 2;
            m_atomsPerMolecule = 2;
            break;
        case NEON:
            m_particleDiameter = NeonAtom.RADIUS * 2;
            m_atomsPerMolecule = 1;
            break;
        case ARGON:
            m_particleDiameter = ArgonAtom.RADIUS * 2;
            m_atomsPerMolecule = 1;
            break;
        case WATER:
            m_particleDiameter = OxygenAtom.RADIUS * 2.2; //TODO: JPB TBD - This is temporary, need to clean up with other diameter stuff.
            m_atomsPerMolecule = 3;
            break;
        }

        // This causes a reset - otherwise it would be too hard to do.
        reset();
    }
    
    public int getThermostatType() {
        return m_thermostatType;
    }

    
    public void setThermostatType( int type ) {
        if ((type == NO_THERMOSTAT) ||
            (type == ISOKINETIC_THERMOSTAT) ||
            (type == ANDERSEN_THERMOSTAT))
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
        m_gravitationalAcceleration = INITIAL_GRAVITATIONAL_ACCEL;
        m_heatingCoolingAmount = 0;
        m_tempAdjustTickCounter = 0;
        if (m_temperature != INITIAL_TEMPERATURE){
            m_temperature = INITIAL_TEMPERATURE;
            notifyTemperatureChanged();
        }
        
        // Clear out the pressure calculation.
        m_pressureCalculator.clear();
        
        // Set the size of the container.
        m_normalizedContainerWidth = StatesOfMatterConstants.CONTAINER_BOUNDS.width / m_particleDiameter;
        m_normalizedContainerHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.height / m_particleDiameter;
        
        // Initialize the particles.
        switch (m_currentMolecule){
        case DIATOMIC_OXYGEN:
            initializeDiatomic(m_currentMolecule);
            break;
        case NEON:
            initializeMonotomic(m_currentMolecule);
            break;
        case ARGON:
            initializeMonotomic(m_currentMolecule);
            break;
        case WATER:
            initializeTriatomic(m_currentMolecule);
            break;
        default:
            System.err.println("Error: Unrecognized particle type, using default number of layers.");
            break;
        }
        
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
        
        // Let any listeners know that the model has been reset.
        notifyResetOccurred();
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
            liquifyParticles();
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
     * @param heatingCoolingAmount - Normalized amount of heating or cooling
     * that the system is undergoing, ranging from -1 to +1.
     */
    public void setHeatingCoolingAmount(double heatingCoolingAmount){
        assert (heatingCoolingAmount <= 1.0) && (heatingCoolingAmount >= -1.0);
        
        m_heatingCoolingAmount = heatingCoolingAmount;
    }
    
    /**
     * Inject a new molecule of the current type into the model.  This uses
     * the current temperature to assign an initial velocity.
     */
    public void injectMolecule(){
        
        double angle = (m_rand.nextDouble() + 1) * (2 * Math.PI / 3);
        double xVel = Math.cos( angle ) * INJECTED_MOLECULE_VELOCITY;
        double yVel = Math.sin( angle ) * INJECTED_MOLECULE_VELOCITY;
        
        if ( m_numberOfAtoms + m_atomsPerMolecule <= MAX_NUM_ATOMS ){
            if (m_atomsPerMolecule == 1){
                // Add particle and its velocity and forces to normalized set.
                m_atomPositions[m_numberOfAtoms] = 
                    new Point2D.Double(m_normalizedContainerWidth * INJECTION_POINT_HORIZ_PROPORTION,
                        m_normalizedContainerHeight * INJECTION_POINT_VERT_PROPORTION);
                m_atomVelocities[m_numberOfAtoms] = new Vector2D.Double( xVel, yVel );
                m_atomForces[m_numberOfAtoms] = new Vector2D.Double();
                m_nextAtomForces[m_numberOfAtoms] = new Vector2D.Double();
                m_numberOfAtoms++;
                
                // Add particle to model set.
                StatesOfMatterAtom particle;
                switch (m_currentMolecule){
                case MONATOMIC_OXYGEN:
                    particle = new OxygenAtom(0, 0);
                    break;
                case ARGON:
                    particle = new ArgonAtom(0, 0);
                    break;
                case NEON:
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

                assert m_currentMolecule == DIATOMIC_OXYGEN;
                
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
                    new Point2D.Double(m_normalizedContainerWidth * INJECTION_POINT_HORIZ_PROPORTION,
                        m_normalizedContainerHeight * INJECTION_POINT_VERT_PROPORTION);
                m_moleculeVelocities[numberOfMolecules - 1] = new Vector2D.Double( xVel, yVel );
                m_moleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                m_nextMoleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                m_moleculeRotationRates[numberOfMolecules - 1] = (m_rand.nextDouble() - 0.5) * (Math.PI / 2);
                
                updateDiatomicAtomPositions();
                syncParticlePositions();
            }
            else if (m_atomsPerMolecule == 3){

                assert m_currentMolecule == WATER;
                
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
                    new Point2D.Double(m_normalizedContainerWidth * INJECTION_POINT_HORIZ_PROPORTION,
                        m_normalizedContainerHeight * INJECTION_POINT_VERT_PROPORTION);
                m_moleculeVelocities[numberOfMolecules - 1] = new Vector2D.Double( xVel, yVel );
                m_moleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                m_nextMoleculeForces[numberOfMolecules - 1] = new Vector2D.Double();
                
                updateTriatomicAtomPositions();
                syncParticlePositions();
            }
        }
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
        
        // Execute the Verlet algorithm.
        for (int i = 0; i < 8; i++ ){
            if (m_atomsPerMolecule == 1){
                verletMonotomic();
            }
            else if (m_atomsPerMolecule == 2){
                verletDiatomic();
            }
            else if (m_atomsPerMolecule == 3){
                verletTriatomic();
            }
        }
        syncParticlePositions();
        if (m_pressure != m_pressureCalculator.getPressure()){
            // The pressure has changed.  Send out notifications and update
            // the current value.
            m_pressure = m_pressureCalculator.getPressure();
            notifyPressureChanged();
        }
        
        // Adjust the temperature.
        m_tempAdjustTickCounter++;
        if ((m_tempAdjustTickCounter > TICKS_PER_TEMP_ADJUSTEMENT) && m_heatingCoolingAmount != 0){
            m_tempAdjustTickCounter = 0;
            m_temperature += m_heatingCoolingAmount * MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT;
            if (m_temperature >= MAX_TEMPERATURE){
                m_temperature = MAX_TEMPERATURE;
            }
            else if (m_temperature <= MIN_TEMPERATURE){
                m_temperature = MIN_TEMPERATURE;
            }
            notifyTemperatureChanged();
            System.out.println("m_temperature = " + m_temperature);
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
        assert (moleculeID == NEON) || (moleculeID == ARGON) || (moleculeID == MONATOMIC_OXYGEN);
        
        // Determine the number of molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container.
        double particleDiameter;
        if (moleculeID == NEON){
            particleDiameter = NeonAtom.RADIUS * 2;
        }
        else if (moleculeID == ARGON){
            particleDiameter = ArgonAtom.RADIUS * 2;
        }
        else if (moleculeID == MONATOMIC_OXYGEN){
            particleDiameter = OxygenAtom.RADIUS * 2;
        }
        else{
            // Force it to neon.
            moleculeID = NEON;
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
            if (moleculeID == NEON){
                atom = new NeonAtom(0, 0);
            }
            else if (moleculeID == ARGON){
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
        assert (moleculeID == DIATOMIC_OXYGEN);
        
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
        assert (moleculeID == WATER); // Only water is supported so far.
        
        // Determine the number of molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container.
        double particleDiameter;
        particleDiameter = OxygenAtom.RADIUS * 2.5; // TODO: This is just a guess, not sure if it's right.
       
        m_numberOfAtoms = (int)Math.pow( StatesOfMatterConstants.CONTAINER_BOUNDS.width / 
                (( particleDiameter + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL ) * 3), 2) * 3;
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
            else{
                atom = new HydrogenAtom(0, 0);
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
        double thetaHOH = 109.47122*Math.PI/180;
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
        double temperatureSqrt = Math.sqrt( m_temperature );
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
        double minWallDistance = 1.5; // TODO: JPB TBD - This is arbitrary, should eventually be a const.
        double rangeX = m_normalizedContainerWidth - (2 * minWallDistance);
        double rangeY = m_normalizedContainerHeight - (2 * minWallDistance);
        for (int i = 0; i < m_numberOfAtoms; i++){
            for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++){
                // Pick a random position.
                newPosX = minWallDistance + (rand.nextDouble() * rangeX);
                newPosY = minWallDistance + (rand.nextDouble() * rangeY);
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
    }
    
    /**
     * Randomize the positions of the molecules that comprise a gas.  This
     * works for diatomic and triatomic molecules.
     */
    private void gasifyMultiAtomicMolecules(){
        
        setTemperature( GAS_TEMPERATURE );
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_temperature );
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
        double minWallDistance = 1.5; // TODO: JPB TBD - This is arbitrary, should eventually be a const.
        double rangeX = m_normalizedContainerWidth - (2 * minWallDistance);
        double rangeY = m_normalizedContainerHeight - (2 * minWallDistance);
        for (int i = 0; i < numberOfMolecules; i++){
            for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++){
                // Pick a random position.
                newPosX = minWallDistance + (rand.nextDouble() * rangeX);
                newPosY = minWallDistance + (rand.nextDouble() * rangeY);
                boolean positionAvailable = true;
                // See if this position is available.
                for (int k = 0; k < i; k++){
                    if (m_moleculeCenterOfMassPositions[k].distance( newPosX, newPosY ) < MIN_INITIAL_INTER_PARTICLE_DISTANCE){
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
                    m_moleculeCenterOfMassPositions[i].setLocation( newPosX, newPosY );
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
     * Set the particles to be in a liquid state.
     */
    private void liquifyParticles(){
        
        // TODO: JPB TBD - This is faked for now and needs to be complete.
        if (m_atomsPerMolecule == 1){
            gasifyMonatomicMolecules();
        }
        else{
            gasifyMultiAtomicMolecules();
        }
        setTemperature( LIQUID_TEMPERATURE );
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
        double temperatureSqrt = Math.sqrt( m_temperature );
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
        double temperatureSqrt = Math.sqrt( m_temperature );
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
        double temperatureSqrt = Math.sqrt( m_temperature );
        int moleculesPerLayer = (int)Math.round( Math.sqrt( m_numberOfAtoms / 3 ) );
        
        double startingPosX = (m_normalizedContainerWidth / 2) - (double)(moleculesPerLayer / 2) - 
                ((moleculesPerLayer / 2) * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
        double startingPosY = 2.0 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL;
        
        // Place the molecules by placing their centers of mass.
        
        int moleculesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; moleculesPlaced < m_numberOfAtoms / 3; i++){ // One iteration per layer.
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
    private void verletMonotomic(){
        
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
        for (int i = 0; i < m_numberOfAtoms; i++){
            
            // Clear the previous calculation's particle forces.
            m_nextAtomForces[i].setComponents( 0, 0 );
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_atomPositions[i], m_nextAtomForces[i], m_normalizedContainerWidth, 
                    m_normalizedContainerHeight);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            m_pressureCalculator.accumulatePressureValue( m_nextAtomForces[i] );
            
            // Add in the effect of gravity.
            m_nextAtomForces[i].setY( m_nextAtomForces[i].getY() - m_gravitationalAcceleration );
        }
        
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
        
        // Calculate the new velocities.
        Vector2D.Double velocityIncrement = new Vector2D.Double();
        for (int i = 0; i < m_numberOfAtoms; i++){
            velocityIncrement.setX( timeStepHalf * (m_atomForces[i].getX() + m_nextAtomForces[i].getX()));
            velocityIncrement.setY( timeStepHalf * (m_atomForces[i].getY() + m_nextAtomForces[i].getY()));
            m_atomVelocities[i].add( velocityIncrement );
            kineticEnergy += ((m_atomVelocities[i].getX() * m_atomVelocities[i].getX()) + 
                    (m_atomVelocities[i].getY() * m_atomVelocities[i].getY())) / 2;
        }
        
        if (m_thermostatType == ISOKINETIC_THERMOSTAT){
            // Isokinetic thermostat
            
            double temperatureScaleFactor;
            if (m_temperature == 0){
                temperatureScaleFactor = 0;
            }
            else{
                temperatureScaleFactor = Math.sqrt( m_temperature * m_numberOfAtoms / kineticEnergy );
            }
            kineticEnergy = 0;
            for (int i = 0; i < m_numberOfAtoms; i++){
                m_atomVelocities[i].setComponents( m_atomVelocities[i].getX() * temperatureScaleFactor, 
                        m_atomVelocities[i].getY() * temperatureScaleFactor );
                kineticEnergy += ((m_atomVelocities[i].getX() * m_atomVelocities[i].getX()) + 
                        (m_atomVelocities[i].getY() * m_atomVelocities[i].getY())) / 2;
            }
        }
        else if (m_thermostatType == ANDERSEN_THERMOSTAT){
            // Modified Andersen Thermostat to maintain fixed temperature
            // modification to reduce abruptness of heat bath interactions.
            // For bare Andersen, set gamma=0.0d0.
            double gamma = 0.9999;
            for (int i = 0; i < m_numberOfAtoms; i++){
                double xVel = m_atomVelocities[i].getX() * gamma + m_rand.nextGaussian() * Math.sqrt(  m_temperature * (1 - Math.pow(gamma, 2)) );
                double yVel = m_atomVelocities[i].getY() * gamma + m_rand.nextGaussian() * Math.sqrt(  m_temperature * (1 - Math.pow(gamma, 2)) );
                m_atomVelocities[i].setComponents( xVel, yVel );
            }
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
        for (int i = 0; i < numberOfMolecules; i++){
            
            // Clear the previous calculation's particle forces and torques.
            m_nextMoleculeForces[i].setComponents( 0, 0 );
            m_nextMoleculeTorques[i] = 0;
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_moleculeCenterOfMassPositions[i], m_nextMoleculeForces[i], m_normalizedContainerWidth, 
                    m_normalizedContainerHeight);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            m_pressureCalculator.accumulatePressureValue( m_nextMoleculeForces[i] );
            
            // Add in the effect of gravity.
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - m_gravitationalAcceleration );
        }
        
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
        
        if (m_thermostatType == ISOKINETIC_THERMOSTAT){
            // Isokinetic thermostat
            
            double temperatureScaleFactor;
            if (m_temperature == 0){
                temperatureScaleFactor = 0;
            }
            else{
                temperatureScaleFactor = Math.sqrt( 1.5 * m_temperature * numberOfMolecules / (centersOfMassKineticEnergy + rotationalKineticEnergy) );
            }
            for (int i = 0; i < numberOfMolecules; i++){
                m_moleculeVelocities[i].setComponents( m_moleculeVelocities[i].getX() * temperatureScaleFactor, 
                        m_moleculeVelocities[i].getY() * temperatureScaleFactor );
                m_moleculeRotationRates[i] *= temperatureScaleFactor;
            }
            centersOfMassKineticEnergy = centersOfMassKineticEnergy * temperatureScaleFactor * temperatureScaleFactor;
            rotationalKineticEnergy = rotationalKineticEnergy * temperatureScaleFactor * temperatureScaleFactor;
        }
        else if (m_thermostatType == ANDERSEN_THERMOSTAT){
            // Modified Andersen Thermostat to maintain fixed temperature
            // modification to reduce abruptness of heat bath interactions.
            // For bare Andersen, set gamma=0.0d0.
            double gamma = 0.9999;
            double velocityScalingFactor = Math.sqrt( m_temperature * massInverse * (1 - Math.pow( gamma, 2)));
            double rotationScalingFactor = Math.sqrt( m_temperature * inertiaInverse * (1 - Math.pow( gamma, 2)));
            
            for (int i = 0; i < numberOfMolecules; i++){
                double xVel = m_moleculeVelocities[i].getX() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                double yVel = m_moleculeVelocities[i].getY() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                m_moleculeVelocities[i].setComponents( xVel, yVel );
                m_moleculeRotationRates[i] = gamma * m_moleculeRotationRates[i] + 
                        m_rand.nextGaussian() * rotationScalingFactor;
            }
        }
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of triatomic molecules.
     */
    private void verletTriatomic(){
        
        assert m_atomsPerMolecule == 3;
        
        int numberOfMolecules = m_numberOfAtoms / 3;
        
        // JPB TBD - Go over this with Paul and understand it.
        double q0 = 3;
        double [] q = new double [] {-2*q0, q0, q0};
        
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
        for (int i = 0; i < numberOfMolecules; i++){
            
            // Clear the previous calculation's particle forces and torques.
            m_nextMoleculeForces[i].setComponents( 0, 0 );
            m_nextMoleculeTorques[i] = 0;
            
            // Get the force values caused by the container walls.
            calculateWallForce(m_moleculeCenterOfMassPositions[i], m_nextMoleculeForces[i], m_normalizedContainerWidth, 
                    m_normalizedContainerHeight);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            m_pressureCalculator.accumulatePressureValue( m_nextMoleculeForces[i] );
            
            // Add in the effect of gravity.
            m_nextMoleculeForces[i].setY( m_nextMoleculeForces[i].getY() - m_gravitationalAcceleration );
        }
        
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
                
                // Calculate Lennard-Jones potential between mass centers.
                double dx = m_moleculeCenterOfMassPositions[i].getX() - m_moleculeCenterOfMassPositions[j].getX();
                double dy = m_moleculeCenterOfMassPositions[i].getY() - m_moleculeCenterOfMassPositions[j].getY();
                double distanceSquared = dx * dx + dy * dy;
                
                if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // Calculate the Lennard-Jones interaction forces.
                    double r2inv = 1 / distanceSquared;
                    double r6inv = r2inv * r2inv * r2inv;
                    double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
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
                            dx = m_atomPositions[3 * i + ii].getX() - m_atomPositions[3 * j + jj].getX();
                            dy = m_atomPositions[3 * i + ii].getY() - m_atomPositions[3 * j + jj].getY();
                            double r2inv = 1/(dx*dx + dy*dy);
                            double forceScaler=q[ii]*q[jj]*r2inv*r2inv;
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
        
        if (m_thermostatType == ISOKINETIC_THERMOSTAT){
            // Isokinetic thermostat
            
            double temperatureScaleFactor;
            if (m_temperature == 0){
                temperatureScaleFactor = 0;
            }
            else{
                temperatureScaleFactor = Math.sqrt( 1.5 * m_temperature * numberOfMolecules / (kecm + kerot) );
            }
            for (int i = 0; i < numberOfMolecules; i++){
                m_moleculeVelocities[i].setComponents( m_moleculeVelocities[i].getX() * temperatureScaleFactor, 
                        m_moleculeVelocities[i].getY() * temperatureScaleFactor );
                m_moleculeRotationRates[i] *= temperatureScaleFactor;
            }
            kecm = kecm * temperatureScaleFactor * temperatureScaleFactor;
            kerot = kerot * temperatureScaleFactor * temperatureScaleFactor;
        }
        else if (m_thermostatType == ANDERSEN_THERMOSTAT){
            // Modified Andersen Thermostat to maintain fixed temperature
            // modification to reduce abruptness of heat bath interactions.
            // For bare Andersen, set gamma=0.0d0.
            double gamma = 0.9999;
            double velocityScalingFactor = Math.sqrt( m_temperature * massInverse * (1 - Math.pow( gamma, 2)));
            double rotationScalingFactor = Math.sqrt( m_temperature * inertiaInverse * (1 - Math.pow( gamma, 2)));
            
            for (int i = 0; i < numberOfMolecules; i++){
                double xVel = m_moleculeVelocities[i].getX() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                double yVel = m_moleculeVelocities[i].getY() * gamma + m_rand.nextGaussian() * velocityScalingFactor;
                m_moleculeVelocities[i].setComponents( xVel, yVel );
                m_moleculeRotationRates[i] = gamma * m_moleculeRotationRates[i] + 
                        m_rand.nextGaussian() * rotationScalingFactor;
            }

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
     * @param resultantForce - Vector in which the resulting force is returned.
     */
    private void calculateWallForce(Point2D position, Vector2D resultantForce, double containerWidth,
            double containerHeight){
        
        // Debug stuff - make sure this is being used correctly.
        assert resultantForce != null;
        assert position != null;
        
        // Non-debug run time check.
        if ((resultantForce == null) || (position == null)){
            return;
        }
        
        double xPos = position.getX();
        double yPos = position.getY();
        
        double minDistance = WALL_DISTANCE_THRESHOLD / 2;
        double distance;
        
        // Calculate the force in the X direction.
        if (xPos < WALL_DISTANCE_THRESHOLD){
            // Close enough to the left wall to feel the force.
            if (xPos < minDistance){
                // Limit the distance, and thus the force, if we are really close.
                xPos = minDistance;
            }
            resultantForce.setX( (48/(Math.pow(xPos, 13))) - (24/(Math.pow( xPos, 7))) );
            m_potentialEnergy += 4/(Math.pow(xPos, 12)) - 4/(Math.pow( xPos, 6)) + 1;
        }
        else if (containerWidth - xPos < WALL_DISTANCE_THRESHOLD){
            // Close enough to the right wall to feel the force.
            distance = containerWidth - xPos;
            if (distance < minDistance){
                distance = minDistance;
            }
            resultantForce.setX( -(48/(Math.pow(distance, 13))) + 
                    (24/(Math.pow( distance, 7))) );
            m_potentialEnergy += 4/(Math.pow(distance, 12)) - 
                    4/(Math.pow( distance, 6)) + 1;
        }
        
        // Calculate the force in the Y direction.
        if (yPos < WALL_DISTANCE_THRESHOLD){
            // Close enough to the bottom wall to feel the force.
            if (yPos < minDistance){
                yPos = minDistance;
            }
            resultantForce.setY( 48/(Math.pow(yPos, 13)) - (24/(Math.pow( yPos, 7))) );
            m_potentialEnergy += 4/(Math.pow(yPos, 12)) - 4/(Math.pow( yPos, 6)) + 1;
        }
        else if (containerHeight - yPos < WALL_DISTANCE_THRESHOLD){
            // Close enough to the top to feel the force.
            distance = containerHeight - yPos;
            if (distance < minDistance){
                distance = minDistance;
            }
            resultantForce.setY( -48/(Math.pow(distance, 13)) +
                    (24/(Math.pow( distance, 7))) );
            m_potentialEnergy += 4/(Math.pow(distance, 12)) - 
                    4/(Math.pow( distance, 6)) + 1;
        }
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

    }
    
    public static class Adapter implements Listener {
        public void resetOccurred(){}
        public void particleAdded(StatesOfMatterAtom particle){}
        public void temperatureChanged(){}
        public void pressureChanged(){}
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
            m_pressueSamples[m_accumulationPosition] += Math.abs(  forceVector.getX() ) / (m_normalizedContainerHeight * 2);
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
