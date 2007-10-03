package edu.colorado.phet.statesofmatter.model.engine.integration;

public interface Integrator1D {
    double nextPosition(double lastX, double lastV, double lastA);

    double nextVelocity(double lastV, double lastA, double curA);
}
