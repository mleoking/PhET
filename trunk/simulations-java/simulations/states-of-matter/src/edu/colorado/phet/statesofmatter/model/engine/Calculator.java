package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public interface Calculator {
    void calculate(StatesOfMatterParticle p, double[] forces);
}
