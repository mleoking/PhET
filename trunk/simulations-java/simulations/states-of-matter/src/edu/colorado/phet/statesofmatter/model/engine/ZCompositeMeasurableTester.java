package edu.colorado.phet.statesofmatter.model.engine;

import junit.framework.TestCase;

import java.util.Arrays;

public class ZCompositeMeasurableTester extends TestCase {
    public void testSumOfOne() {
        performTest(new double[]{1.0});
    }

    public void testSumOfThree() {
        performTest(new double[]{1.0, 2.0, 3.0});
    }
    
    private void performTest(double[] numbers) {
        double sum = 0.0;

        Measurable[] measurables = new Measurable[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            measurables[i] = new ConstantMeasurable(numbers[i]);

            sum += numbers[i];
        }

        assertEquals(sum, new CompositeMeasurable(measurables).measure(), 0.00001);
        assertEquals(sum, new CompositeMeasurable(Arrays.asList(measurables)).measure(), 0.00001);
    }
}
