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
        (2 * NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL) * (NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL - 1);
    public static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.3;  // In particle diameters.
    public static final double TIME_STEP = 0.5E6;
    public static final double INITIAL_TEMPERATURE = 0.5;
    public static final double TEMPERATURE_STEP = -0.1;

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

        // TODO: JPB TBD - Add a set of moving particles.
        /*
        Random rand = new Random();
        for (int i=0; i<15; i++){
            double xPos = rand.nextDouble() * StatesOfMatterConstants.CONTAINER_BOUNDS.width;
            double yPos = rand.nextDouble() * StatesOfMatterConstants.CONTAINER_BOUNDS.height;
            double xVel = (rand.nextDouble() - 0.5) * 40;
            double yVel = (rand.nextDouble() - 0.5) * 40;
            StatesOfMatterParticle particle = new StatesOfMatterParticle(xPos, yPos, OXYGEN_MOLECULE_DIAMETER, 10);
            particle.setVx( xVel );
            particle.setVy( yVel );
            m_particles.add( particle );
            notifyParticleAdded( particle );
        }
        */
        
        // TODO: JPB TBD - First attempt to port Paul Beale's IDL code.
        m_particlePositions = new Point2D [NUMBER_OF_PARTICLES];
        m_particleVelocities = null;
        m_particleForces = null;
        for (int i = 0; i < NUMBER_OF_PARTICLES; i++){
            // Add particle to normalized set.
            m_particlePositions[i] = new Point2D.Double();
            
            // Add particle to model set.
            StatesOfMatterParticle particle = new StatesOfMatterParticle(0, 0, OXYGEN_MOLECULE_DIAMETER/2, 10);
            m_particles.add( particle );
            notifyParticleAdded( particle );
        }
        double normalizedContainerWidth = StatesOfMatterConstants.CONTAINER_BOUNDS.width / OXYGEN_MOLECULE_DIAMETER;
        double normalizedContainerHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.height / OXYGEN_MOLECULE_DIAMETER;
        insertCrystal( NUMBER_OF_LAYERS_IN_INITIAL_CRYSTAL, NUMBER_OF_PARTICLES, m_particlePositions,
                normalizedContainerWidth, normalizedContainerHeight );
        syncParticlePositions();
        
        
        /*
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
        
        /*
        // TODO: JPB TBD - Simple linear motion and bouncing algorithm for testing.
        for ( Iterator iterator = m_particles.iterator(); iterator.hasNext(); ) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle) iterator.next();
            
            // Bounce the particle if needed.
            if ((particle.getX() >= StatesOfMatterConstants.CONTAINER_BOUNDS.width) &&
                (particle.getVx() > 0)){
                particle.setVx( -particle.getVx() );
            }
            else if ((particle.getX() <= StatesOfMatterConstants.CONTAINER_BOUNDS.x) &&
                     (particle.getVx() < 0)){
                particle.setVx( -particle.getVx() );
            }
            if ((particle.getY() >= StatesOfMatterConstants.CONTAINER_BOUNDS.height) &&
                    (particle.getVy() > 0)){
                    particle.setVy( -particle.getVy() );
            }
            else if ((particle.getY() <= StatesOfMatterConstants.CONTAINER_BOUNDS.y) &&
                     (particle.getVy() < 0)){
                particle.setVy( -particle.getVy() );
            }
                
            particle.setPosition( particle.getX() + particle.getVx(), particle.getY() + particle.getVy());
        }
        */
        
        /*
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
        double startingPosY = 0.5 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL;
        
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
    }
    
    public static class Adapter implements Listener {
        public void resetOccurred(){}
        public void particleAdded(StatesOfMatterParticle particle){}
    }
}
