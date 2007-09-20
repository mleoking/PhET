package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class ZLennardJonesForceCalculatorTester extends TestCase {
    private static final double EPSILON = 1.0;
    private static final double RMIN    = 1.0;

    private static final LennardJonesForce LJF = new LennardJonesForce(EPSILON, RMIN);

    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle( 2,  2, 1, 1);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(-2, -2, 1, 1);

    private final double[] f1 = new double[2];
    private final double[] f2 = new double[2];

    public void setUp() {
        for (int i = 0; i < 2; i++) {
            f1[i] = 0;
            f2[i] = 0;
        }
    }

    public void testForcesAreNonZeroOnTwoParticles() {
        calculateP1AndP2();

        assertTrue(f1[0] * f1[0] + f1[1] * f1[1] > 0);
        assertTrue(f2[0] * f2[0] + f2[1] * f2[1] > 0);
    }

    public void testForcesOnTwoParticlesEqualAndOpposite() {
        calculateP1AndP2();

        assertEquals(f1[0], -f2[0], 0.00001);
        assertEquals(f1[1], -f2[1], 0.00001);
    }

    private void calculateP1AndP2() {
        LennardJonesForceCalculator c = new LennardJonesForceCalculator(LJF, particles(P1, P2));

        c.calculate(P1, f1);
        c.calculate(P2, f2);
    }

    private List particles(StatesOfMatterParticle p1, StatesOfMatterParticle p2) {
        return Arrays.asList(new StatesOfMatterParticle[]{p1, p2});
    }
}
