// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.core;

import java.util.Arrays;
import java.util.Vector;

import edu.colorado.phet.efield.electron.phys2d_efield.Particle;

// Referenced classes of package edu.colorado.phet.efield.electron.core:
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
