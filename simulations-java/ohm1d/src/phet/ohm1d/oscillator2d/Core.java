package phet.ohm1d.oscillator2d;

import phet.phys2d.DoublePoint;
import phet.phys2d.PropagatingParticle;

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
