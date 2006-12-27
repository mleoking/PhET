package electron.laws;

import phys2d.DoublePoint;
import phys2d.Particle;

public interface ForceLaw {
    public DoublePoint getForce( Particle a, Particle b );
}
