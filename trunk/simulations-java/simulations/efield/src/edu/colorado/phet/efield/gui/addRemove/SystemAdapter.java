// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

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
