package edu.colorado.phet.statesofmatter.model.engine.integration;

public class VelocityVerletIntegrator1D {
    public double nextPosition(double lastX, double lastV, double lastA, double deltaT) {
        return lastX + lastV * deltaT + 0.5 * lastA * deltaT * deltaT;
    }

    public double nextVelocity(double lastV, double lastA, double curA, double deltaT) {
        return lastV + (lastA + curA) / 2.0 * deltaT;
    }
}
