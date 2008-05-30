package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class ZLennardJonesForceCalculatorTester extends TestCase {
    static final double RMIN    = StatesOfMatterConstants.RMIN;
    static final double EPSILON = StatesOfMatterConstants.EPSILON;

    private static final LennardJonesForce LJF = new LennardJonesForce(EPSILON, RMIN);

    private static final StatesOfMatterParticle P1 = new StatesOfMatterParticle( 2,  2, 1, 1);
    private static final StatesOfMatterParticle P2 = new StatesOfMatterParticle(-2, -2, 1, 1);

    private static final StatesOfMatterParticle P3 = new StatesOfMatterParticle(0, 0, 1, 1);
    private static final StatesOfMatterParticle P4 = new StatesOfMatterParticle(0, 0.5 * RMIN, 1, 1);

    private static final StatesOfMatterParticle P5 = new StatesOfMatterParticle(0, 0, 1, 1);
    private static final StatesOfMatterParticle P6 = new StatesOfMatterParticle(0, 1.5 * RMIN, 1, 1);

    private static final StatesOfMatterParticle P7 = new StatesOfMatterParticle(0, 0, 1, 1);
    private static final StatesOfMatterParticle P8 = new StatesOfMatterParticle(0, RMIN, 1, 1);

    private final double[] f1 = new double[2];
    private final double[] f2 = new double[2];

    public void setUp() {
        for (int i = 0; i < 2; i++) {
            f1[i] = 0;
            f2[i] = 0;
        }
    }

    public void testForcesAreNonZeroOnTwoParticles() {
        calculate(P1, P2);

        assertTrue(f1[0] * f1[0] + f1[1] * f1[1] > 0);
        assertTrue(f2[0] * f2[0] + f2[1] * f2[1] > 0);
    }

    public void testForcesOnTwoParticlesEqualAndOpposite() {
        calculate(P1, P2);

        assertEquals(f1[0], -f2[0], 0.00001);
        assertEquals(f1[1], -f2[1], 0.00001);
    }

    public void testForcesOnParticlesCloserThanRMinRepulsive() {
        calculate(P3, P4);

        assertTrue(f1[1] < 0);
        assertTrue(f2[1] > 0);
    }

    public void testForcesOnParticlesFurtherThanRMinAttractive() {
        calculate(P5, P6);

        assertTrue(f1[1] > 0);
        assertTrue(f2[1] < 0);
    }

    public void testForcesOnParticlesSeparatedByRMinZero() {
        calculate(P7, P8);

        assertEquals(f1[1], 0.0, 0.00001);
        assertEquals(f2[1], 0.0, 0.00001);
    }

    private void calculate(StatesOfMatterParticle p1, StatesOfMatterParticle p2) {
        LennardJonesForceCalculator c = new LennardJonesForceCalculator(LJF, particles(p1, p2));

        c.calculate(p1, f1);
        c.calculate(p2, f2);
    }

    private List particles(StatesOfMatterParticle p1, StatesOfMatterParticle p2) {
        return Arrays.asList(new StatesOfMatterParticle[]{p1, p2});
    }
}
