package phys2d.propagators;

import phys2d.DoublePoint;
import phys2d.Particle;
import phys2d.Propagator;

public class ResetAcceleration implements Propagator {
    public void propagate( double time, Particle p ) {
        DoublePoint zero = new DoublePoint();
        p.setAcceleration( zero );
    }
}
