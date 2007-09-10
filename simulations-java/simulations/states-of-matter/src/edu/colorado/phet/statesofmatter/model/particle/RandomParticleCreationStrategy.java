package edu.colorado.phet.statesofmatter.model.particle;

import java.util.List;

public class RandomParticleCreationStrategy implements ParticleCreationStrategy {
    public StatesOfMatterParticle createNewParticle(List particles, double radius, double mass) {
        return new StatesOfMatterParticle(Math.random(), Math.random(), radius, mass);
    }
}
