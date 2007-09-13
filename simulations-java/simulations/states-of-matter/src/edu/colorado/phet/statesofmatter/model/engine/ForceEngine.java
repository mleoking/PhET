package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public interface ForceEngine {
    ForceComputation compute(StatesOfMatterParticle[] particles, EngineConfig descriptor);

    double getPotentialEnergy();
}
