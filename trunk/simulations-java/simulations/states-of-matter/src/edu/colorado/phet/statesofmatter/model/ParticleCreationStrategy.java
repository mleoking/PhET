package edu.colorado.phet.statesofmatter.model;

import java.util.List;

public interface ParticleCreationStrategy {
    /**
     * Creates a new particle, given the list of existing particles.
     *
     * @param particles The list of existing particles.
     *
     * @return The new particle.
     */
    StatesOfMatterParticle createNewParticle(List particles);
}
