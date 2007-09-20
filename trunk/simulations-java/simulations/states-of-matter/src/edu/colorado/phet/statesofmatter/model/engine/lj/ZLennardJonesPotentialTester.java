package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.common.phetcommon.math.testing.MathTestingUtils;

public class ZLennardJonesPotentialTester extends ZAbstractLennardJonesFunctionTester {
    private volatile LennardJonesPotential ljp;

    public void setUp() {
        ljp = new LennardJonesPotential(EPSILON, RMIN);
    }

    public void testMaxIsAtZero() {
        assertEquals(Double.MAX_VALUE, ljp.evaluate(0, 0), 0.0);
    }

    public void testMinimumIsAtRmin() {
        MathTestingUtils.testHasMinimum(ljp,
            new double[]{0, 0},
            new double[]{2 * RMIN, 0},
            new double[]{RMIN, 0}
        );
    }
    
    public void testMinimumIsEpsilon() {
        assertEquals(-EPSILON, ljp.evaluate(RMIN, 0), 0.0001);
    }

    public void testSigmaIsAsExpected() {
        double sigma = RMIN / Math.pow(2.0, 1.0/6.0);

        assertEquals(sigma, ljp.getSigma(), 0.000001);
    }
}
