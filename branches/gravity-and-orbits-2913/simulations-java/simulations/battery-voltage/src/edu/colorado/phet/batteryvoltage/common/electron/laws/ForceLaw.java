// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.electron.laws;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

public interface ForceLaw {
    public DoublePoint getForce( Particle a, Particle b );
}
