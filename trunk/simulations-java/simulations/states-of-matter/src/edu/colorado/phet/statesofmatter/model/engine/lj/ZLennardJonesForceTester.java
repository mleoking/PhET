package edu.colorado.phet.statesofmatter.model.engine.lj;

import java.util.Arrays;

public class ZLennardJonesForceTester extends ZAbstractLennardJonesFunctionTester {
    private volatile LennardJonesForce ljf;

    public void setUp() {
        ljf = new LennardJonesForce(EPSILON, RMIN);
    }

    public void testForceIsInfiniteAtZero() {
        assertTrue(
            Arrays.equals(
                new double[]{
                    Double.MAX_VALUE,
                    Double.MAX_VALUE
                },
                ljf.evaluate(0.0, 0.0)
            )
        );
    }

    public void testForceIsZeroAtRmin() {
        double[] force = ljf.evaluate(RMIN, 0);

        assertTrue("Force should be zero but is " + force[0] + ", " + force[1],
                   Arrays.equals(
                       new double[]{
                           0.0,
                           0.0
                       },
                       force
                   )
        );
    }
}
