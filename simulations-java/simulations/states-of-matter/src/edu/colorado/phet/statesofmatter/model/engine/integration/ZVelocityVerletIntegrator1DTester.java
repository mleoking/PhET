package edu.colorado.phet.statesofmatter.model.engine.integration;

import junit.framework.TestCase;

public class ZVelocityVerletIntegrator1DTester extends TestCase {
    private static final double DELTA_T = 1.0;
    private static final double EPS     = 0.000001;

    private volatile Integrator1D i;

    public void setUp() {
        i = new VelocityVerletIntegrator1D(DELTA_T);
    }

    public void testNoPositionChangeWhenNoVelecityOrAcceleration() {
        assertEquals(0, i.nextPosition(0, 0, 0), EPS);
    }

    public void testPositionUpdatingWithConstantVelocityNoAcceleration() {
        assertEquals(1.0, i.nextPosition(0, 1.0, 0.0), EPS);
    }

    public void testPositionUpdatingWithConstantVelocityAndAcceleration() {
        assertEquals(1.5, i.nextPosition(0.0, 1.0, 1.0), EPS);
    }

    public void testNoVelocityChangeWhenNoVelocityOrAcceleration() {
        assertEquals(0.0, i.nextVelocity(0.0, 0.0, 0.0), EPS);
    }

    public void testVelocityDoesNotChangeWithNoAcceleration() {
        assertEquals(1.0, i.nextVelocity(1.0, 0.0, 0.0), EPS);
    }

    public void testVelocityChangesWithAcceleration() {
        assertEquals(1.0, i.nextVelocity(0.0, 1.0, 1.0), EPS);
    }
}
