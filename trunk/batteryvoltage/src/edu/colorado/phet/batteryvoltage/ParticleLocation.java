package edu.colorado.phet.batteryvoltage;

import electron.man.motions.Location;
import phys2d.DoublePoint;
import phys2d.Particle;

public class ParticleLocation implements Location {
    Particle target; //going toward or carrying.

    public ParticleLocation( Particle p ) {
        this.target = p;
    }

    public DoublePoint getPosition() {
        return target.getPosition();
    }
}
