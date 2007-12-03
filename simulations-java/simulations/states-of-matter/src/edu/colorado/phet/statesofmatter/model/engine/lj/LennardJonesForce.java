package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.common.phetcommon.math.VectorToVectorFunction;

public class LennardJonesForce extends AbstractLennardJonesFunction implements VectorToVectorFunction {
    public static final LennardJonesForce TEST = new LennardJonesForce(1.0, 1.0);

    private final double A;
    private final double B;
    
    public LennardJonesForce(double epsilon, double rMin) {
        super(epsilon, rMin);

        A = epsilon * 12.0 * Math.pow(rMin, 12.0);
        B = epsilon * 12.0 * Math.pow(rMin, 6);
    }

    public double[] evaluate(double[] args) {
        double[] output = new double[args.length];

        evaluateInPlace(args, output);

        return output;
    }

    public void evaluateInPlace(double[] args, double[] output) {
        double dist = Math.sqrt(args[0] * args[0] + args[1] * args[1]);

        if (dist == 0.0) {
            output[0] = Double.MAX_VALUE;
            output[1] = Double.MAX_VALUE;
        }
        else {
            double mag = ljf(dist);

            output[0] = mag * args[0]/dist;
            output[1] = mag * args[1]/dist;
        }
    }

    public double[] evaluate(double v, double v1) {
        return evaluate(new double[]{v, v1});
    }

    private double ljf(double r) {
        double rSecond     = r * r;
        double rThird      = rSecond * r;
        double rSixth      = rThird * rThird;
        double rSeventh    = rSixth * r;
        double rThirteenth = rSixth * rSeventh;

        return A / rThirteenth - B / rSeventh;
    }
}
