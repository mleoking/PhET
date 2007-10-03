package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyMeasurer;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesPotential;

import java.util.List;

/**
 * A simple interface to the model engine, which feeds different components
 * the shared requirements.
 */
public class EngineFacade {
    private final ForceEngine forceEngine;
    private final List particles;
    private final LennardJonesPotential ljp;
    private final EngineConfig descriptor;

    public EngineFacade(List particles, EngineConfig descriptor) {
        this.descriptor  = descriptor;
        this.particles   = particles;
        this.forceEngine = new VerletForceEngine();
        this.ljp         = new LennardJonesPotential(descriptor.epsilon, descriptor.rMin);
    }

    /**
     * Retrieves the total potential energy in the model.
     *
     * @return The total potential energy.
     */
    public double getPotentialEnergy() {
        EnginePotentialEnergyMeasurer measurer = new EnginePotentialEnergyMeasurer(particles, StatesOfMatterConfig.CONTAINER_BOUNDS.getMaxY(), descriptor.gravity, ljp);

        return measurer.measure();
    }

    /**
     * Retrieves the total kinetic energy in the model.
     *
     * @return The total kinetic energy.
     */
    public double getKineticEnergy() {
        KineticEnergyMeasurer measurer = new KineticEnergyMeasurer(particles);

        return measurer.measure();
    }

    /**
     * Steps the model forward one instant in time.
     *
     * @return The force computation.
     */
    public ForceComputation step() {
        return forceEngine.compute(particles, descriptor);
    }
}
