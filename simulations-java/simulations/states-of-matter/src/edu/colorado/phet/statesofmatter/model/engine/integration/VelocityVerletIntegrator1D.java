package edu.colorado.phet.statesofmatter.model.engine.integration;

public class VelocityVerletIntegrator1D implements Integrator1D {
    private final double deltaT;
    private final double deltaTSquared;

    public VelocityVerletIntegrator1D(double deltaT) {
        this.deltaT = deltaT;
        this.deltaTSquared = deltaT * deltaT;
    }
    
    public double nextPosition(double lastX, double lastV, double lastA) {
        return lastX + lastV * deltaT + 0.5 * lastA * deltaTSquared;
    }

    public double nextVelocity(double lastV, double lastA, double curA) {
        return lastV + (lastA + curA) / 2.0 * deltaT;
    }
}
