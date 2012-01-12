// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.core;

import edu.colorado.phet.efield.phys2d_efield.Particle;

public interface ParticleContainer {

    public abstract void add( Particle particle );

    public abstract void remove( Particle particle );

    public abstract int numParticles();

    public abstract Particle particleAt( int i );
}
