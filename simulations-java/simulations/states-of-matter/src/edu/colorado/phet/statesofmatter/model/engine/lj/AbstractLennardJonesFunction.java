package edu.colorado.phet.statesofmatter.model.engine.lj;

public abstract class AbstractLennardJonesFunction {
    protected final double epsilon;
    protected final double rMin;
    protected final double sigma;

    public AbstractLennardJonesFunction(double epsilon, double rMin) {
        this.rMin    = rMin;
        this.epsilon = epsilon;
        this.sigma   = rMin / Math.pow(2.0, 1.0/6.0);
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getRMin() {
        return rMin;
    }

    public double getSigma() {
        return sigma;
    }
}
