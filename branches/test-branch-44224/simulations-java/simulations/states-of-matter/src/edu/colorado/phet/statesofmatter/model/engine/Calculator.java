package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public interface Calculator {
    void calculate(StatesOfMatterAtom p, double[] forces);
}
