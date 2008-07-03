package edu.colorado.phet.statesofmatter.model.particle;

import java.util.Collection;

public interface ParticleCreationStrategy {
    /**
     * Creates a new particle.
     *
     * @return The new particle, or <code>null</code> if the strategy
     *         cannot create any new particles.
     */
    StatesOfMatterAtom createParticle();

    /**
     * Creates a collection of particles, up to the specified maximum.
     * Different strategies may have different limitations regarding how
     * many particles they can create.
     *
     * @param destination   Where to place the particles.
     * @param maximum       The maximum of particles.
     *
     * @return The number of particles actually created.
     */
    int createParticles(Collection destination, int maximum);
}
