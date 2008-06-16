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
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

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

    public static final double OXYGEN_MOLECULE_DIAMETER = 120;  // Picometers.
    
    // TODO: JPB TBD - These constants are here as a result of the first attempt
    // to integrate Paul Beale's IDL implementation of the Verlet algorithm.
    // Eventually some or all of them will be moved.
    public static final int NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL = 6;
    public static final int NUMBER_OF_PARTICLES = 
        1 + (2 * NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL) * (NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL - 1);
    public static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.3;  // In particle diameters.
    public static final double TIME_STEP = Math.pow( 0.5, 6.0 );
    public static final double INITIAL_TEMPERATURE = 0.2;
    public static final double MAX_TEMPERATURE = 4.0;
    public static final double MIN_TEMPERATURE = 0.01;
    public static final double TEMPERATURE_STEP = -0.1;
    private static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    private static final double INITIAL_GRAVITATIONAL_ACCEL = 0.3;
    private static final double MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT = 0.025;
    private static final int    TICKS_PER_TEMP_ADJUSTEMENT = 10; // JPB TBD - I'm not sure if this is a reasonable
                                                                 // way to do this (i.e. that it is based on the
                                                                 // number of ticks).  Should it instead be based on
                                                                 // the time step defined above?

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
    private Point2D [] m_particlePositions;
    private Vector2D [] m_particleVelocities;
    private Vector2D [] m_particleForces;
    private Vector2D [] m_nextParticleForces;

    private double m_normalizedContainerWidth = StatesOfMatterConstants.CONTAINER_BOUNDS.width / OXYGEN_MOLECULE_DIAMETER;
    private double m_normalizedContainerHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.height / OXYGEN_MOLECULE_DIAMETER;
    private double m_potentialEnergy;
    private Random m_rand = new Random();
    private double m_temperature;
    private double m_gravitationalAcceleration;
    private double m_heatingCoolingAmount;
    private int    m_tempAdjustTickCounter;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public MultipleParticleModel(IClock clock) {
        
        m_clock = clock;
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        });

        reset();
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

    public StatesOfMatterParticle getParticle(int i) {
        return (StatesOfMatterParticle)m_particles.get(i);
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
        m_temperature = newTemperature;
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

    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {
        
        // Get rid of any existing particles.
        for ( Iterator iter = m_particles.iterator(); iter.hasNext(); ) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle) iter.next();
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
        
        // Initialize the vectors that define the normalized particle attributes.
        m_particlePositions  = new Point2D [NUMBER_OF_PARTICLES];
        m_particleVelocities = new Vector2D [NUMBER_OF_PARTICLES];
        m_particleForces     = new Vector2D [NUMBER_OF_PARTICLES];
        m_nextParticleForces = new Vector2D [NUMBER_OF_PARTICLES];
        
        for (int i = 0; i < NUMBER_OF_PARTICLES; i++){
            
            // Add particle and its velocity and forces to normalized set.
            m_particlePositions[i] = new Point2D.Double();
            m_particleVelocities[i] = new Vector2D.Double();
            m_particleForces[i] = new Vector2D.Double();
            m_nextParticleForces[i] = new Vector2D.Double();
            
            // Add particle to model set.
            StatesOfMatterParticle particle = new StatesOfMatterParticle(0, 0, OXYGEN_MOLECULE_DIAMETER/2, 10);
            m_particles.add( particle );
            notifyParticleAdded( particle );
        }
        insertCrystal( NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL, NUMBER_OF_PARTICLES, m_particlePositions,
                m_normalizedContainerWidth, m_normalizedContainerHeight );
        
        // Initialize particle velocities.
        for (int i = 0; i < NUMBER_OF_PARTICLES; i++){
            double temperatureSqrt = Math.sqrt( m_temperature );
            m_particleVelocities[i].setComponents( temperatureSqrt * m_rand.nextGaussian() , 
                    temperatureSqrt * m_rand.nextGaussian() );
        }
        syncParticlePositions();
        
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
     * Sets the amount of heating or cooling that the system is undergoing.
     * 
     * @param heatingCoolingAmount - Normalized amount of heating or cooling
     * that the system is undergoing, ranging from -1 to +1.
     */
    public void setHeatingCoolingAmount(double heatingCoolingAmount){
        assert (heatingCoolingAmount <= 1.0) && (heatingCoolingAmount >= -1.0);
        
        m_heatingCoolingAmount = heatingCoolingAmount;
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
            verlet( NUMBER_OF_PARTICLES, m_particlePositions, m_particleVelocities, m_normalizedContainerWidth, 
                    m_normalizedContainerHeight, m_gravitationalAcceleration, m_particleForces, TIME_STEP, 
                    m_temperature );
        }
        syncParticlePositions();
        
        // Adjust the temperature.
        m_tempAdjustTickCounter++;
        if ((m_tempAdjustTickCounter > TICKS_PER_TEMP_ADJUSTEMENT) && m_heatingCoolingAmount != 0){
            m_tempAdjustTickCounter = 0;
            m_temperature += m_heatingCoolingAmount * MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT;
            if (getTemperature() >= MAX_TEMPERATURE){
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
    
    private void notifyResetOccurred(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).resetOccurred();
        }        
    }

    private void notifyParticleAdded(StatesOfMatterParticle particle){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).particleAdded( particle );
        }        
    }

    private void notifyTemperatureChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).temperatureChanged();
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
     * Create positions corresponding to a hexagonal 2d "crystal" structure
     * for a set of particles.  Note that this assumes a normalized value
     * of 1.0 for the diameter of the particles.
     * 
     * @param numLayers
     * @param numParticles
     * @param particlePositions
     * @param normalizedContainerWidth
     * @param normalizedContainerHeight
     */
    private void insertCrystal( int numLayers, int numParticles, Point2D [] particlePositions,
            double normalizedContainerWidth, double normalizedContainerHeight ){
        
        int particlesPerLayer = (int)(numParticles / numLayers);
        double startingPosX = (normalizedContainerWidth / 2) - (double)(particlesPerLayer / 2) - 
                ((particlesPerLayer / 2) * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
        double startingPosY = 2.0 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL;
        
        int particlesPlaced = 0;
        for (int i = 0; particlesPlaced < numParticles; i++){ // One iteration per layer.
            for (int j = 0; (j < particlesPerLayer) && (particlesPlaced < numParticles); j++){
                double xPos = startingPosX + j + (j * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL) / 2;
                }
                double yPos = startingPosY + (double)i * (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL)* 0.7071;
                particlePositions[(i * particlesPerLayer) + j].setLocation( xPos, yPos );
                particlesPlaced++;
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
        double positionMultiplier = OXYGEN_MOLECULE_DIAMETER;
        for (int i = 0; i < NUMBER_OF_PARTICLES; i++){
            ((StatesOfMatterParticle)m_particles.get( i )).setPosition( 
                    m_particlePositions[i].getX() * positionMultiplier, 
                    m_particlePositions[i].getY() * positionMultiplier);
        }
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of particles.
     * 
     * @param numParticles
     * @param particlePositions
     * @param particleVelocities
     * @param containerWidth
     * @param containerHeight
     * @param time
     * @param timeStep
     */
    private void verlet(int numParticles, Point2D [] particlePositions, Vector2D [] particleVelocities,
            double containerWidth, double containerHeight, double gravitationalForce, Vector2D [] particleForces, 
            double timeStep, double temperature){
        
        double kineticEnergy = 0;
        
        // TODO: JPB TBD - For the sake of efficiency, this allocation should
        // be moved outside of this member function at some point, probably
        // made into a member var of the object that does the calculation.
//        for (int i = 0; i < numParticles; i++){
//            m_nextParticleForces[i] = new Vector2D.Double();
//        }
        
        double timeStepSqrHalf = timeStep * timeStep * 0.5;
        double timeStepHalf = timeStep / 2;
        
        // Update the positions of all particles based on their current
        // velocities and the forces acting on them.
        for (int i = 0; i < numParticles; i++){
            double xPos = particlePositions[i].getX() + (timeStep * particleVelocities[i].getX()) + 
                    (timeStepSqrHalf * particleForces[i].getX());
            double yPos = particlePositions[i].getY() + (timeStep * particleVelocities[i].getY()) + 
                    (timeStepSqrHalf * particleForces[i].getY());
            particlePositions[i].setLocation( xPos, yPos );
        }
        
        // Zero out potential energy.
        m_potentialEnergy = 0;
        
        // Calculate the forces exerted on the particles by the container
        // walls and by gravity.
        for (int i = 0; i < numParticles; i++){
            
            // Clear the previous calculation's particle forces.
            m_nextParticleForces[i].setComponents( 0, 0 );
            
            // Get the force values caused by the container walls.
            calculateWallForce(particlePositions[i], m_nextParticleForces[i], containerWidth, containerHeight);
            
            // Add in the effect of gravity.
            m_nextParticleForces[i].setY( m_nextParticleForces[i].getY() - gravitationalForce );
        }
        
        // Calculate the forces created through interactions with other
        // particles.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < numParticles; i++){
            for (int j = i + 1; j < numParticles; j++){
                double dx = particlePositions[i].getX() - particlePositions[j].getX();
                double dy = particlePositions[i].getY() - particlePositions[j].getY();
                double distanceSqrd = (dx * dx) + (dy * dy);
                
                if (distanceSqrd < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // This pair of particles is close enough to one another
                    // that we consider them in the calculation.
                    double r2inv = 1 / distanceSqrd;
                    double r6inv = r2inv * r2inv * r2inv;
                    double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
                    force.setX( dx * forceScaler );
                    force.setY( dy * forceScaler );
                    m_nextParticleForces[i].add( force );
                    m_nextParticleForces[j].subtract( force );
                    m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                }
            }
        }
        
        // Calculate the new velocities.
        Vector2D.Double velocityIncrement = new Vector2D.Double();
        for (int i = 0; i < numParticles; i++){
            velocityIncrement.setX( timeStepHalf * (particleForces[i].getX() + m_nextParticleForces[i].getX()));
            velocityIncrement.setY( timeStepHalf * (particleForces[i].getY() + m_nextParticleForces[i].getY()));
            particleVelocities[i].add( velocityIncrement );
            kineticEnergy += ((particleVelocities[i].getX() * particleVelocities[i].getX()) + 
                    (particleVelocities[i].getY() * particleVelocities[i].getY())) / 2;
        }
        
        // Isokinetic thermostat
        
        double temperatureScaleFactor;
        if (temperature == 0){
            temperatureScaleFactor = 0;
        }
        else{
            temperatureScaleFactor = Math.sqrt( temperature * numParticles / kineticEnergy );
        }
        kineticEnergy = 0;
        for (int i = 0; i < numParticles; i++){
            particleVelocities[i].setComponents( particleVelocities[i].getX() * temperatureScaleFactor, 
                    particleVelocities[i].getY() * temperatureScaleFactor );
            kineticEnergy += ((particleVelocities[i].getX() * particleVelocities[i].getX()) + 
                    (particleVelocities[i].getY() * particleVelocities[i].getY())) / 2;
        }
        
        // Replace the new forces with the old ones.
        for (int i = 0; i < numParticles; i++){
            particleForces[i].setComponents( m_nextParticleForces[i].getX(), m_nextParticleForces[i].getY() );
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
        
        // Calculate the force in the X direction.
        if (position.getX() < WALL_DISTANCE_THRESHOLD){
            // Close enough to the left wall to feel the force.
            resultantForce.setX( (48/(Math.pow(position.getX(), 13))) - (24/(Math.pow( position.getX(), 7))) );
            m_potentialEnergy += 4/(Math.pow(position.getX(), 12)) - 4/(Math.pow( position.getX(), 6)) + 1;
        }
        else if (containerWidth - position.getX() < WALL_DISTANCE_THRESHOLD){
            // Close enough to the right wall to feel the force.
            resultantForce.setX( -(48/(Math.pow(containerWidth - position.getX(), 13))) + 
                    (24/(Math.pow( containerWidth - position.getX(), 7))) );
            m_potentialEnergy += 4/(Math.pow(containerWidth - position.getX(), 12)) - 
                    4/(Math.pow( containerWidth - position.getX(), 6)) + 1;
        }
        
        // Calculate the force in the Y direction.
        if (position.getY() < WALL_DISTANCE_THRESHOLD){
            // Close enough to the bottom wall to feel the force.
            resultantForce.setY( 48/(Math.pow(position.getY(), 13)) - (24/(Math.pow( position.getY(), 7))) );
            m_potentialEnergy += 4/(Math.pow(position.getY(), 12)) - 4/(Math.pow( position.getY(), 6)) + 1;
        }
        else if (containerHeight - position.getY() < WALL_DISTANCE_THRESHOLD){
            resultantForce.setY( -48/(Math.pow(containerHeight - position.getY(), 13)) +
                    (24/(Math.pow( containerHeight - position.getY(), 7))) );
            m_potentialEnergy += 4/(Math.pow(containerHeight - position.getY(), 12)) - 
                    4/(Math.pow( containerHeight - position.getY(), 6)) + 1;
        }
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Adapters
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void resetOccurred();
        
        /**
         * Inform listeners that a new particle has been added to the model.
         */
        public void particleAdded(StatesOfMatterParticle particle);
        
        /**
         * Inform listeners that the temperature of the system has changed.
         */
        public void temperatureChanged();
    }
    
    public static class Adapter implements Listener {
        public void resetOccurred(){}
        public void particleAdded(StatesOfMatterParticle particle){}
        public void temperatureChanged(){}
    }
}
