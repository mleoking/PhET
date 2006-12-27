package edu.colorado.phet.common;

import edu.colorado.phet.common.phys2d.Particle;

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

    public Particle remove( int i ) {
        return (Particle)v.remove( i );
    }

    public void remove( Particle e ) {
        v.remove( e );
    }

    public int numParticles() {
        return v.size();
    }

    public Particle particleAt( int i ) {
        return (Particle)v.get( i );
    }
}
