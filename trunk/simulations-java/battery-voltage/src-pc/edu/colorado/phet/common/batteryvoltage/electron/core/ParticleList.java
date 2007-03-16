package edu.colorado.phet.common.batteryvoltage.electron.core;

import edu.colorado.phet.common.batteryvoltage.phys2d.Particle;

import java.util.Arrays;
import java.util.Vector;

public class ParticleList implements ParticleContainer //an adapter.
{
    Vector v;

    public ParticleList() {
        v = new Vector();
    }

    public void addAll( Particle[] p ) {
        v.addAll( Arrays.asList( p ) );
    }

    public void add( Particle e ) {
        v.add( e );
    }

    public int numParticles() {
        return v.size();
    }

    public Particle particleAt( int i ) {
        return (Particle)v.get( i );
    }
}
