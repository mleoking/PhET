package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.electron.man.motions.Location;
import edu.colorado.phet.phys2d.DoublePoint;
import edu.colorado.phet.phys2d.Particle;

public class ParticleLocation implements Location {
    Particle target; //going toward or carrying.

    public ParticleLocation( Particle p ) {
        this.target = p;
    }

    public DoublePoint getPosition() {
        return target.getPosition();
    }
}
