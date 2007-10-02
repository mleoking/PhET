package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ConstantCalculator implements Calculator {
    private final double[] forces;

    public ConstantCalculator(double[] forces) {
        this.forces = forces;
    }

    public void calculate(StatesOfMatterParticle p, double[] forces) {
        System.arraycopy(this.forces, 0, forces, 0, this.forces.length);
    }
}
