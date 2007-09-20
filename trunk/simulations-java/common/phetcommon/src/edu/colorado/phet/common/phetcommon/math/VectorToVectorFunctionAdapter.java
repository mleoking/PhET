package edu.colorado.phet.common.phetcommon.math;

public class VectorToVectorFunctionAdapter extends AbstractVectorToVectorFunction {
    private final VectorToDoubleFunction function;

    public VectorToVectorFunctionAdapter(VectorToDoubleFunction function) {
        this.function = function;
    }

    public void evaluateInPlace(double[] args, double[] output) {
        double eval = function.evaluate(args);
        
        for (int i = 0; i < args.length; i++) {
            output[i] = eval;
        }
    }
}
