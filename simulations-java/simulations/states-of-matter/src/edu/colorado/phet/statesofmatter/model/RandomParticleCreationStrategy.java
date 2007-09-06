package edu.colorado.phet.statesofmatter.model;

import java.util.List;

public class RandomParticleCreationStrategy implements ParticleCreationStrategy {
    public StatesOfMatterParticle createNewParticle(List particles) {
        return new StatesOfMatterParticle(Math.random(), Math.random());
    }
}
