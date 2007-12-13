package edu.colorado.phet.statesofmatter.model.engine.lj;

import edu.colorado.phet.common.phetcommon.math.VectorToDoubleFunction;

public class LennardJonesPotential extends AbstractLennardJonesFunction implements VectorToDoubleFunction {
    public static final LennardJonesPotential TEST = new LennardJonesPotential(1.0, 1.0);

    private final double A;
    private final double B;

    public LennardJonesPotential(double epsilon, double rMin) {
        super(epsilon, rMin);

        this.A = epsilon * Math.pow(rMin, 12);
        this.B = epsilon * 2 * Math.pow(rMin, 6);
    }

    public double evaluate(double rx, double ry) {
        double dist = Math.sqrt(rx * rx + ry * ry);

        if (dist == 0.0) return Double.MAX_VALUE;

        return A / Math.pow(dist, 12) - B / Math.pow(dist, 6);
    }

    public double evaluate(double[] args) {
        return evaluate(args[0], args[1]);
    }

}
