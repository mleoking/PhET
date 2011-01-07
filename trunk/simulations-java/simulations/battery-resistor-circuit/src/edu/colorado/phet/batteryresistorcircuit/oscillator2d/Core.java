// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.oscillator2d;

import edu.colorado.phet.batteryresistorcircuit.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryresistorcircuit.common.phys2d.PropagatingParticle;

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
