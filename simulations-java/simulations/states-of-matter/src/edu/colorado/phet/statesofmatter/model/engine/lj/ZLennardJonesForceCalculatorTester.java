package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ZLennardJonesForceCalculatorTester extends TestCase {
    static final double RMIN    = StatesOfMatterConstants.RMIN;
    static final double EPSILON = StatesOfMatterConstants.EPSILON;

    private static final LennardJonesForce LJF = new LennardJonesForce(EPSILON, RMIN);

    private static final StatesOfMatterAtom P1 = new StatesOfMatterAtom( 2,  2, 1, 1);
    private static final StatesOfMatterAtom P2 = new StatesOfMatterAtom(-2, -2, 1, 1);

    private static final StatesOfMatterAtom P3 = new StatesOfMatterAtom(0, 0, 1, 1);
    private static final StatesOfMatterAtom P4 = new StatesOfMatterAtom(0, 0.5 * RMIN, 1, 1);

    private static final StatesOfMatterAtom P5 = new StatesOfMatterAtom(0, 0, 1, 1);
    private static final StatesOfMatterAtom P6 = new StatesOfMatterAtom(0, 1.5 * RMIN, 1, 1);

    private static final StatesOfMatterAtom P7 = new StatesOfMatterAtom(0, 0, 1, 1);
    private static final StatesOfMatterAtom P8 = new StatesOfMatterAtom(0, RMIN, 1, 1);

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

    private void calculate(StatesOfMatterAtom p1, StatesOfMatterAtom p2) {
        LennardJonesForceCalculator c = new LennardJonesForceCalculator(LJF, particles(p1, p2));

        c.calculate(p1, f1);
        c.calculate(p2, f2);
    }

    private List particles(StatesOfMatterAtom p1, StatesOfMatterAtom p2) {
        return Arrays.asList(new StatesOfMatterAtom[]{p1, p2});
    }
}
