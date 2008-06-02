/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus.Listener;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import edu.colorado.phet.statesofmatter.model.engine.EngineConfig;
import edu.colorado.phet.statesofmatter.model.engine.EngineFacade;
import edu.colorado.phet.statesofmatter.model.engine.ForceComputation;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyAdjuster;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyCapper;
import edu.colorado.phet.statesofmatter.model.particle.PackedHexagonalParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.ParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

/**
 * This class represents the main class for the particle model.  It makes use
 * of several other classes to implement the overall model behavior.
 *
 * @author John Blanco
 */
public class MultipleParticleModel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final MultipleParticleModel TEST = new MultipleParticleModel(ConstantDtClock.TEST);
    public static final double OXYGEN_MOLECULE_DIAMETER = 120;  // Picometers.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final List m_particles = new ArrayList();
    private double m_totalEnergy;
    private EngineFacade m_engineFacade;
    IClock m_clock;
    private ArrayList _listeners = new ArrayList();

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
    
    public void reset() {
        // TODO: JPB TBD - Add a single oxygen particle.
        m_particles.clear();
        StatesOfMatterParticle particle = new StatesOfMatterParticle(0, 0, OXYGEN_MOLECULE_DIAMETER, 10);
        particle.setVx( 20 );
        particle.setVy( 10 );
        m_particles.add( particle );
        
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
        
        // TODO: JPB TBD - Temp computation for testing.
//        for ( Iterator iterator = m_particles.iterator(); iterator.hasNext(); ) {
//            StatesOfMatterParticle particle = (StatesOfMatterParticle) iterator.next();
//            particle.setX(particle.getX() + particle.getVx());
//            particle.setY(particle.getY() + particle.getVy());
//        }
        
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
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Adapters
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the positions of the particles have changed.
         */
        void particlesMoved();
    }
}
