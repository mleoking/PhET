package edu.colorado.phet.common;

import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.System2D;

public class SystemAdapter implements ParticleContainer {
    System2D sys;

    public SystemAdapter( System2D sys ) {
        this.sys = sys;
    }

    public void add( Particle p ) {
        sys.addParticle( p );
    }

    public void remove( Particle p ) {
        sys.remove( p );
    }

    public int numParticles() {
        return sys.numParticles();
    }

    public Particle particleAt( int i ) {
        return sys.particleAt( i );
    }
}
