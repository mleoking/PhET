package edu.colorado.phet.common;

import edu.colorado.phet.common.phys2d.Particle;

public interface ParticleContainer //an adapter.
{
    public void add( Particle e );

    public void remove( Particle e );

    public int numParticles();

    public Particle particleAt( int i );
}
