// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public interface Calculator {
    void calculate(StatesOfMatterAtom p, double[] forces);
}
