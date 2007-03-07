package phet_ohm1d.ohm1d.oscillator2d;

import phet_ohm1d.phys2d.DoublePoint;
import phet_ohm1d.phys2d.PropagatingParticle;

public class Core extends PropagatingParticle {
    DoublePoint origin;
    double scalarPosition;

    public Core( DoublePoint origin, double scalarPosition ) {
        this.scalarPosition = scalarPosition;
        this.origin = origin;
    }

    public double getScalarPosition() {
        return scalarPosition;
    }

    public DoublePoint getOrigin() {
        return origin;
    }
}
