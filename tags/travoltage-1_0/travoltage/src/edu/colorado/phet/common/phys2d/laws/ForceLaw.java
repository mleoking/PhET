package edu.colorado.phet.common.phys2d.laws;

import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;

public interface ForceLaw {
    public DoublePoint getForce( Particle a, Particle b );
}
