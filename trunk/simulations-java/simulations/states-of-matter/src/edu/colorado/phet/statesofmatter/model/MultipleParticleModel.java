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
    public static final double MAX_TEMPERATURE = 4.0;
    public static final double MIN_TEMPERATURE = 0.01;
    public static final double TEMPERATURE_STEP = -0.1;
    private static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    private static final double INITIAL_GRAVITATIONAL_ACCEL = 0.02;
    private static final double MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT = 0.025;
    private static final int    TICKS_PER_TEMP_ADJUSTEMENT = 10; // JPB TBD - I'm not sure if this is a reasonable
                                                                 // way to do this (i.e. that it is based on the
                                                                 // number of ticks).  Should it instead be based on
                                                                 // the time step defined above?
    private static final int MAX_NUM_ATOMS = 300;
    private static final double INJECTED_MOLECULE_VELOCITY = 1.0;
    private static final double INJECTION_POINT_HORIZ_PROPORTION = 0.97;
    private static final double INJECTION_POINT_VERT_PROPORTION = 0.5;
    
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
    public static final int DIATOMIC_OXYGEN = 3;
    public static final int WATER = 4;

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
    private int m_atomsPerMolecule;
    
    private Point2D [] m_moleculeCentersOfMass;
    private double [] m_moleculeRotationAngles;
    private double [] m_moleculeRotationRates;
    private double [] m_moleculeTorque;
    private double m_triatomicMoleculeMass;
    private double m_triatomicRotationalInertia;
    double [] m_x0;
    double [] m_y0;

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
    private boolean m_thermostatEnabled;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public MultipleParticleModel(IClock clock) {
        
        m_clock = clock;
        m_pressureCalculator = new PressureCalculator();
        m_thermostatEnabled = true;
        
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

    public int getNumParticles() {
        return m_particles.size();
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
        m_gravitationalAcceleration = acceleration;
    }
    
    public int getParticleType(){
        return m_currentMolecule;
    }
    
    public double getPressure(){
        return m_pressure;
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
    
    public boolean getIsThermostatEnabled() {
        return m_thermostatEnabled;
    }

    
    public void setIsThermostatEnabled( boolean enabled ) {
        m_thermostatEnabled = enabled;
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
//            yugga;
//            initializeDiatomic(m_currentMolecule);
            break;
        case NEON:
            initializeMonotomic(m_currentMolecule);
            break;
        case ARGON:
            initializeMonotomic(m_currentMolecule);
            break;
        case WATER:
//            yugga;
//            initializeTriatomic(m_currentMolecule);
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
            solidifyParticles();
            break;
            
        case PHASE_LIQUID:
            liquifyParticles();
            break;
            
        case PHASE_GAS:
            gasifyParticles();
            break;
            
        default:
            System.err.println("Error: Invalid state specified.");
            // Treat is as a solid.
            solidifyParticles();
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
     * Inject a new particle of the current type into the model.  This uses
     * the current temperature to assign an initial velocity.
     */
    public void injectParticle(){
        
        if ( m_numberOfAtoms < MAX_NUM_ATOMS ){
            // Add particle and its velocity and forces to normalized set.
            m_atomPositions[m_numberOfAtoms] = 
                new Point2D.Double(m_normalizedContainerWidth * INJECTION_POINT_HORIZ_PROPORTION,
                    m_normalizedContainerHeight * INJECTION_POINT_VERT_PROPORTION);
            m_atomVelocities[m_numberOfAtoms] = new Vector2D.Double( -INJECTED_MOLECULE_VELOCITY, 0 );
            m_atomForces[m_numberOfAtoms] = new Vector2D.Double();
            m_nextAtomForces[m_numberOfAtoms] = new Vector2D.Double();
            m_numberOfAtoms++;
            
            // Add particle to model set.
            StatesOfMatterAtom particle = new StatesOfMatterAtom(0, 0, m_particleDiameter/2, 10);
            m_particles.add( particle );
            syncParticlePositions();
            notifyParticleAdded( particle );
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
            verlet();
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
    
    private void initializeMonotomic(int moleculeID){
        
        // Verify that a valid molecule ID was provided.
        assert (moleculeID == NEON) || (moleculeID == ARGON);
        
        // Determine the number of molecules to create.  This will be a cube
        // (really a square, since it's 2D, but you get the idea) that takes
        // up a fixed amount of the bottom of the container.
        double particleDiameter;
        if (moleculeID == NEON){
            particleDiameter = NeonAtom.RADIUS * 2;
        }
        else{
            particleDiameter = ArgonAtom.RADIUS * 2;
        }
        
        m_numberOfAtoms = (int)Math.pow( StatesOfMatterConstants.CONTAINER_BOUNDS.width / 
                (( particleDiameter + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL ) * 3), 2);
        
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
            StatesOfMatterAtom atom;
            if (moleculeID == NEON){
                atom = new NeonAtom(0, 0);
            }
            else{
                atom = new ArgonAtom(0, 0);
            }
            m_particles.add( atom );
            notifyParticleAdded( atom );
        }
        
        // Initialize the particle positions.
        solidifyParticles();
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
    private static final int MAX_PLACEMENT_ATTEMPTS = 500;
    private void gasifyParticles(){
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
            for (int j = 0; j < 100; j++){
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
     * Set the particles to be in a liquid state.
     */
    private void liquifyParticles(){
        
        // TODO: JPB TBD - This is faked for now and needs to be complete.
        gasifyParticles();
        setTemperature( LIQUID_TEMPERATURE );
        syncParticlePositions();
    }
    
    /**
     * Create positions corresponding to a hexagonal 2d "crystal" structure
     * for a set of particles.  Note that this assumes a normalized value
     * of 1.0 for the diameter of the particles.
     * @param diatomic
     */
    private void solidifyParticles(){

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
                if ((m_atomsPerMolecule == 2) && (j % 2 != 0)){
                    // We are adding a partner to an atom to create a diatomic pair.
                    Point2D prevParticlePos = m_atomPositions[(i * particlesPerLayer) + (j - 1)];
                    xPos = prevParticlePos.getX() + DISTANCE_BETWEEN_DIATOMIC_PAIRS;
                    StatesOfMatterAtom particleA = (StatesOfMatterAtom)(m_particles.get( (i * particlesPerLayer) + (j - 1) ));
                    StatesOfMatterAtom particleB = (StatesOfMatterAtom)(m_particles.get( (i * particlesPerLayer) + (j) ));
                    particleA.setDiatomicPartner( particleB );
                    particleB.setDiatomicPartner( particleA );
                }
                else{
                    xPos = startingPosX + j + (j * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
                    if (i % 2 != 0){
                        // Every other row is shifted a bit to create hexagonal pattern.
                        xPos += (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL) / 2;
                    }
                }
                yPos = startingPosY + (double)i * (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL)* 0.7071;
                m_atomPositions[(i * particlesPerLayer) + j].setLocation( xPos, yPos );
                particlesPlaced++;

                // Assign each particle an initial velocity.
                m_atomVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
                        temperatureSqrt * rand.nextGaussian() );
            }
        }
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
    private void verlet(){
        
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
        
        // Calculate the forces created through interactions with other
        // particles.
        Vector2D force = new Vector2D.Double();
        StatesOfMatterAtom particle1, particle2;
        double particle1NormalizedPosX, particle1NormalizedPosY;
        for (int i = 0; i < m_numberOfAtoms; i++){
            particle1 = (StatesOfMatterAtom)m_particles.get( i );
            particle1NormalizedPosX = m_atomPositions[i].getX();
            particle1NormalizedPosY = m_atomPositions[i].getY();
            for (int j = i + 1; j < m_numberOfAtoms; j++){
                
                double dx = particle1NormalizedPosX - m_atomPositions[j].getX();
                double dy = particle1NormalizedPosY - m_atomPositions[j].getY();
                double distanceSqrd = (dx * dx) + (dy * dy);
                // TODO: JPB TBD - Limit the max forces to prevent weird behavior.  Is this
                // worth keeping?
                double minDistanceSquared = 0.8;
                if (distanceSqrd == 0){
                    // Handle the special case where the particles are right
                    // on top of each other by assigning an arbitrary
                    // artificial spacing.
                    dx = 1;
                    dy = 1;
                    distanceSqrd = 2;
                }
                else {
                    while (distanceSqrd < minDistanceSquared){
                        dx *= 1.1;
                        dy *= 1.1;
                        distanceSqrd = (dx * dx) + (dy * dy);
                    }
                }
                
                double distance = Math.sqrt( distanceSqrd );
                // End JPB TBD.
                
                particle2 = (StatesOfMatterAtom)m_particles.get( j );
                if (particle1.getDiatomicPartner() == particle2){
                    // This is a diatomic pair of particles, so calculate the
                    // force accordingly.  Basically, this acts as though
                    // there is a spring between the two particles.
                    double springDistance = distance - DISTANCE_BETWEEN_DIATOMIC_PAIRS;
                    force.setX( springDistance * DIATOMIC_FORCE_CONSTANT * dx / distance);
                    force.setY( springDistance * DIATOMIC_FORCE_CONSTANT * dy / distance);
                    m_nextAtomForces[i].subtract( force );
                    m_nextAtomForces[j].add( force );
                }
                else if (distanceSqrd < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // This pair of particles is close enough to one another
                    // that we consider them in the calculation.
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
        
        if (m_thermostatEnabled){
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
        
        // Replace the new forces with the old ones.
        for (int i = 0; i < m_numberOfAtoms; i++){
            m_atomForces[i].setComponents( m_nextAtomForces[i].getX(), m_nextAtomForces[i].getY() );
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
