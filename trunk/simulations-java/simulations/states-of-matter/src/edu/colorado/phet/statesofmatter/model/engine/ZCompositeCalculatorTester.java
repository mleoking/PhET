package edu.colorado.phet.statesofmatter.model.engine;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ZCompositeCalculatorTester extends TestCase {
    private volatile double[] forces = new double[2];

    public void setUp() {
        for (int i = 0; i < forces.length; i++) {
            forces[i] = i;
        }
    }
    
    public void testCompositeWithNoEntriesDoesNothing() {
        CompositeCalculator c = new CompositeCalculator(new Calculator[0]);

        c.calculate(null, forces);

        assertEquals(0.0, forces[0], 0.0);
        assertEquals(1.0, forces[1], 0.0);
    }

    public void testCompositeWithArray() {
        ConstantCalculator c1 = new ConstantCalculator(new double[]{1.0, 2.0});
        ConstantCalculator c2 = new ConstantCalculator(new double[]{-1.0, -2.0});

        CompositeCalculator c = new CompositeCalculator(new Calculator[]{c1, c2});

        c.calculate(null, forces);

        assertEquals(0.0, forces[0], 0.0);
        assertEquals(0.0, forces[1], 0.0);
    }

    public void testCompositeWithCollection() {
        ConstantCalculator c1 = new ConstantCalculator(new double[]{1.0, 2.0});
        ConstantCalculator c2 = new ConstantCalculator(new double[]{2.0, 4.0});

        CompositeCalculator c = new CompositeCalculator(Arrays.asList(new Calculator[]{c1, c2}));

        c.calculate(null, forces);

        assertEquals(3.0, forces[0], 0.0);
        assertEquals(6.0, forces[1], 0.0);
    }

    public void testGetCalculators() {
        ConstantCalculator c1 = new ConstantCalculator(new double[]{1.0, 2.0});
        ConstantCalculator c2 = new ConstantCalculator(new double[]{2.0, 4.0});

        List list = Arrays.asList(new Calculator[]{c1, c2});

        assertSame(list, new CompositeCalculator(list).getCalculators());
    }
}
