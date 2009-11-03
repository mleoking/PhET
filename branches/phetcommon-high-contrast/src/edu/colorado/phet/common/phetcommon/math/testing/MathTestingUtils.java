package edu.colorado.phet.common.phetcommon.math.testing;

import edu.colorado.phet.common.phetcommon.math.VectorToDoubleFunction;
import edu.colorado.phet.common.phetcommon.math.VectorToVectorFunction;
import edu.colorado.phet.common.phetcommon.math.VectorToVectorFunctionAdapter;
import junit.framework.TestCase;

public class MathTestingUtils {
    private static final double SIGNIFICANCE = 1.0E-10;
    private static final int DEFAULT_STEPS = 100;

    public static void testHasMinimum(VectorToDoubleFunction function, double[] min, double[] max, double[] minimum) {
        testHasMinimum(function, min, max, minimum, DEFAULT_STEPS);
    }

    public static void testHasMinimum(VectorToDoubleFunction function, double[] min, double[] max, double[] minimum, int steps) {
        testHasMinimum(new VectorToVectorFunctionAdapter(function), min, max, minimum, steps);
    }

    public static void testHasMinimum(VectorToVectorFunction function, double[] min, double[] max, double[] minimum) {
        testHasMinimum(function, min, max, minimum, DEFAULT_STEPS);
    }

    public static void testHasMinimum(VectorToVectorFunction function, double[] min, double[] max, double[] minimum, int steps) {
        assert min.length == max.length;

        double[] step = new double[min.length], cur = new double[min.length];

        for (int i = 0; i < min.length; i++) {
            step[i] = (max[i] - min[i]) / steps;
            cur[i]  = min[i];

            assert min[i] <= max[i];
        }

        double[] valueAtMinimum = function.evaluate(minimum);

        for (int stepIndex = 0; stepIndex < steps; stepIndex++) {
            for (int i = 0; i < min.length; i++) {
                   cur[i] += step[i];
            }

            double[] curValue = function.evaluate(cur);

            for (int i = 0; i < min.length; i++) {
                if ((curValue[i] + SIGNIFICANCE) < valueAtMinimum[i]) {
                    TestCase.fail("The function " + function + " has a lower minimum than specified: diff = " + Math.abs(curValue[i] - valueAtMinimum[i]));
                }
            }
        }
    }
}
