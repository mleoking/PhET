// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.phys2d.propagators;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;

public class ResetAcceleration implements Propagator {
    public void propagate( double time, Particle p ) {
        DoublePoint zero = new DoublePoint();
        p.setAcceleration( zero );
    }
}
