package edu.colorado.phet.statesofmatter.model;

import java.util.List;

public class RandomParticleCreationStrategy implements ParticleCreationStrategy {
    public Particle createNewParticle(List particles) {
        return new Particle(Math.random(), Math.random());
    }
}
