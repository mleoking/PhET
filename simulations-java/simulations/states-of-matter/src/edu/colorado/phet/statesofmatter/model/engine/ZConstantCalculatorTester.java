package edu.colorado.phet.statesofmatter.model.engine;

import junit.framework.TestCase;

public class ZConstantCalculatorTester extends TestCase {
    public void testConstantCalculator() {
        ConstantCalculator c = new ConstantCalculator(new double[]{1.0, 2.0});

        double[] a = new double[2];

        c.calculate(null, a);

        assertEquals(1.0, a[0], 0.0);
        assertEquals(2.0, a[1], 0.0);
    }
}
