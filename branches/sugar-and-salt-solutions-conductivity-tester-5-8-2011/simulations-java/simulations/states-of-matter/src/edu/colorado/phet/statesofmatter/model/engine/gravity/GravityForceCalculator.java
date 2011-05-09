// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.model.engine.gravity;

import edu.colorado.phet.statesofmatter.model.engine.Calculator;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class GravityForceCalculator implements Calculator {
    private final double g;

    public GravityForceCalculator(double g) {
        this.g = g;
    }

    public void calculate(StatesOfMatterAtom p, double[] forces) {
        forces[0] = 0.0;
        forces[1] = -p.getMass() * g;
    }
}
