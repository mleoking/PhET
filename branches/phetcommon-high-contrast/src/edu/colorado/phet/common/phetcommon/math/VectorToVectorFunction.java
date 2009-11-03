package edu.colorado.phet.common.phetcommon.math;

public interface VectorToVectorFunction {
    double[] evaluate(double[] args);

    void evaluateInPlace(double[] args, double[] output);
}
