package edu.colorado.phet.statesofmatter.model.engine.gravity;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ZGravityForceCalculatorTester extends TestCase {
    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle(0.0, 0.0, 1.0, 1.0);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(0.0, 0.0, 1.0, 2.0);

    private static final double G = 2.0;

    private volatile GravityForceCalculator c;
    private volatile double[] forces = new double[2];

    public void setUp() {
        c = new GravityForceCalculator(G);
    }
    
    public void testForcePullsDown() {
        calc(P1);

        assertTrue(yForce() < 0);
    }

    public void testForceStrictlyVertical() {
        calc(P1);

        assertEquals(xForce(), 0.0, 0.0);
    }

    public void testForceIsProportionalToMassOfParticle() {
        calc(P1);

        double f1 = yForce();

        calc(P2);

        double f2 = yForce();

        assertTrue(Math.abs(f2) > Math.abs(f1));
    }

    private double xForce() {
        return forces[0];
    }

    private double yForce() {
        return forces[1];
    }

    private void calc(StatesOfMatterParticle p) {
        c.calculate(p, forces);
    }
}
