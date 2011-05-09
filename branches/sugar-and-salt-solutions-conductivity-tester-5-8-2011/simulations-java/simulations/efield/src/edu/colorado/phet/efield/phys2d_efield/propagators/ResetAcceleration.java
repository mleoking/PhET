// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.Propagator;

public class ResetAcceleration
        implements Propagator {

    public ResetAcceleration() {
    }

    public void propagate( double d, Particle particle ) {
        DoublePoint doublepoint = new DoublePoint();
        particle.setAcceleration( doublepoint );
    }
}
