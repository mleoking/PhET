package edu.colorado.phet.ohm1d.oscillator2d;

import edu.colorado.phet.ohm1d.phet_ohm1d.phys2d.DoublePoint;
import edu.colorado.phet.ohm1d.phet_ohm1d.phys2d.PropagatingParticle;

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
