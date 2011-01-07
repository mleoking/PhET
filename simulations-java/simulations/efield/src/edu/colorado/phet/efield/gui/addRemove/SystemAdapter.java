// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.gui.addRemove;

import edu.colorado.phet.efield.core.ParticleContainer;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.System2D;

public class SystemAdapter
        implements ParticleContainer {

    public SystemAdapter( System2D system2d ) {
        sys = system2d;
    }

    public void add( Particle particle ) {
        sys.addParticle( particle );
    }

    public void remove( Particle particle ) {
        sys.remove( particle );
    }

    public int numParticles() {
        return sys.numParticles();
    }

    public Particle particleAt( int i ) {
        return sys.particleAt( i );
    }

    System2D sys;
}
