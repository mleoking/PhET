package edu.colorado.phet.electron.laws;

import edu.colorado.phet.phys2d.DoublePoint;
import edu.colorado.phet.phys2d.Particle;

public interface ForceLaw {
    public DoublePoint getForce( Particle a, Particle b );
}
