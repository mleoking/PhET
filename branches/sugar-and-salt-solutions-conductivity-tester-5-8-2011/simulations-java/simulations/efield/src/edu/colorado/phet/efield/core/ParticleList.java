// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.core;

import java.util.Arrays;
import java.util.Vector;

import edu.colorado.phet.efield.phys2d_efield.Particle;

// Referenced classes of package edu.colorado.phet.efield.core:
//            ParticleContainer

public class ParticleList implements ParticleContainer {

    public ParticleList() {
        v = new Vector();
    }

    public void addAll( Particle aparticle[] ) {
        v.addAll( Arrays.asList( aparticle ) );
    }

    public void add( Particle particle ) {
        v.add( particle );
    }

    public void remove( Particle particle ) {
        v.remove( particle );
    }

    public int numParticles() {
        return v.size();
    }

    public Particle particleAt( int i ) {
        return (Particle) v.get( i );
    }

    Vector v;
}
