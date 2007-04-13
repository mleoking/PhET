package edu.colorado.phet.common.batteryvoltage.electron.laws;

import edu.colorado.phet.common.batteryvoltage.phys2d.DoublePoint;
import edu.colorado.phet.common.batteryvoltage.phys2d.Particle;

public interface ForceLaw {
    public DoublePoint getForce( Particle a, Particle b );
}
