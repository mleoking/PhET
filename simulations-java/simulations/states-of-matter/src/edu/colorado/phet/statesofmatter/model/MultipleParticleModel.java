package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleParticleModel extends BaseModel implements ClockListener {
    private final List particles = new ArrayList();

    public static final MultipleParticleModel TEST = new MultipleParticleModel(ConstantDtClock.TEST);

    private double totalEnergy;
    private EngineFacade engineFacade;

    public MultipleParticleModel(IClock clock) {
        clock.addClockListener(this);

        initialize();
    }

    public void initialize() {
        ParticleCreationStrategy strategy = new PackedHexagonalParticleCreationStrategy(StatesOfMatterConfig.ICE_CUBE_BOUNDS, StatesOfMatterConfig.PARTICLE_MASS, StatesOfMatterConfig.PARTICLE_RADIUS, StatesOfMatterConfig.PARTICLE_CREATION_CUSHION, StatesOfMatterConfig.ICE_CUBE_DIST_FROM_FLOOR);

        particles.clear();

        strategy.createParticles(particles, StatesOfMatterConfig.INITIAL_MAX_PARTICLE_COUNT);

        engineFacade = new EngineFacade(particles, EngineConfig.TEST);

        double targetKineticEnergy = StatesOfMatterConfig.INITIAL_TOTAL_ENERGY_PER_PARTICLE * getNumParticles() - engineFacade.measurePotentialEnergy();

        KineticEnergyAdjuster adjuster = new KineticEnergyAdjuster();

        adjuster.adjust(particles, targetKineticEnergy);

        totalEnergy = engineFacade.measureKineticEnergy() + engineFacade.measurePotentialEnergy();
    }

    public List getParticles() {
        return Collections.unmodifiableList(particles);
    }

    public int getNumParticles() {
        return particles.size();
    }

    public StatesOfMatterParticle getParticle(int i) {
        return (StatesOfMatterParticle)particles.get(i);
    }

    public synchronized void clockTicked(ClockEvent clockEvent) {
        for (int i = 0; i < StatesOfMatterConfig.COMPUTATIONS_PER_RENDER; i++) {
            ForceComputation computation = engineFacade.step(clockEvent.getSimulationTimeChange());

            computation.apply(particles);

            // Cap the kinetic energy:
            capKineticEnergy();
        }

        // Readjust to conserve total energy:
        conserveTotalEnergy();
    }

    private void conserveTotalEnergy() {
        double curKE = engineFacade.measureKineticEnergy();
        double curTotalEnergy = curKE + engineFacade.measurePotentialEnergy();

        double energyDiff = curTotalEnergy - totalEnergy;

        double targetKE = curKE - energyDiff;

        if (targetKE > 0) {
            new KineticEnergyAdjuster().adjust(particles, targetKE);
        }
    }

    private void capKineticEnergy() {
        new KineticEnergyCapper(particles).cap(StatesOfMatterConfig.PARTICLE_MAX_KE);
    }

    public void clockStarted(ClockEvent clockEvent) {
    }

    public void clockPaused(ClockEvent clockEvent) {
    }

    public void simulationTimeChanged(ClockEvent clockEvent) {
    }

    public void simulationTimeReset(ClockEvent clockEvent) {
    }

    public ParticleContainer getParticleContainer() {
        return new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS);
    }

    public synchronized double getKineticEnergy() {
        return engineFacade.measureKineticEnergy();
    }

    public synchronized double getPotentialEnergy() {
        return engineFacade.measurePotentialEnergy();
    }

    public synchronized double getTotalEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }
}
