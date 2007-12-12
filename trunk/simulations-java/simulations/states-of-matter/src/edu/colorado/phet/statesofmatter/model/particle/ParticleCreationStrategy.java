package edu.colorado.phet.statesofmatter.model.particle;

public interface ParticleCreationStrategy {
    /**
     * Creates a new particle.
     *
     * @return The new particle.
     */
    StatesOfMatterParticle createNewParticle();
}
