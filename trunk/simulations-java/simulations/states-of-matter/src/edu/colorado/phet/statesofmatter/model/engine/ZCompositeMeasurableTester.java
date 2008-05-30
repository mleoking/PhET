package edu.colorado.phet.statesofmatter.model.engine;

import java.util.Arrays;

import junit.framework.TestCase;

public class ZCompositeMeasurableTester extends TestCase {
    public void testSumOfOne() {
        performTest(new double[]{1.0});
    }

    public void testSumOfThree() {
        performTest(new double[]{1.0, 2.0, 3.0});
    }

    public void testGetMeasurables() {
        double[] numbers = new double[]{1,2,3,4};

        Measurable[] measurables = new Measurable[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            measurables[i] = new ConstantMeasurable(numbers[i]);
        }

        assertEquals(numbers.length, new CompositeMeasurable(measurables).getMeasurables().size());
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
