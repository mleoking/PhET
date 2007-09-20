package edu.colorado.phet.statesofmatter.model.engine;

import junit.framework.TestCase;

public class ZConstantMeasurableTester extends TestCase {
    public void testMeasuringIsConstant() {
        ConstantMeasurable measurable = new ConstantMeasurable(0.3);

        assertEquals(0.3, measurable.measure(), 0.0);
        assertEquals(0.3, measurable.measure(), 0.0);
        assertEquals(0.3, measurable.measure(), 0.0);
    }
}
