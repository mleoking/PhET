package edu.colorado.phet.common.phetcommon.math;

public abstract class AbstractVectorToVectorFunction implements VectorToVectorFunction {
    public void evaluateInPlace(double[] args, double[] output) {
        double[] eval = evaluate(args);

        System.arraycopy(eval, 0, output, 0, args.length);
    }

    public double[] evaluate(double[] args) {
        double[] output = new double[args.length];

        evaluateInPlace(args, output);

        return output;
    }
}
