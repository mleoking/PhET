package edu.colorado.phet.statesofmatter.model.particle;

import java.util.List;

public interface ParticleCreationStrategy {
    /**
     * Creates a new particle, given the list of existing particles, the
     * desired radius, and the mass.
     *
     * @param particles The list of existing particles.
     *
     * @param radius    The desired radius of the new particle.
     *
     * @param mass      The mass of the particle.
     *
     * @return The new particle.
     */
    StatesOfMatterParticle createNewParticle(List particles, double radius, double mass);
}
