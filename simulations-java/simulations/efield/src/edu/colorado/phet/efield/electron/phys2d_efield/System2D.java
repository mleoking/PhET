// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.phys2d_efield;

import java.util.Vector;

// Referenced classes of package phys2d:
//            Law, PropagatorLaw, Particle, Propagator

public class System2D {

    public System2D() {
        particles = new Vector();
        laws = new Vector();
    }

    public void remove( Particle particle ) {
        particles.remove( particle );
    }

    public String toString() {
        return "particles=" + particles + ", laws=" + laws;
    }

    public void addParticle( Particle particle ) {
        particles.add( particle );
    }

    public void addLaw( Law law ) {
        if ( !( law instanceof Law ) ) {
            throw new RuntimeException( "What?!" );
        }
        else {
            laws.add( law );
            return;
        }
    }

    public void addLaw( Propagator propagator ) {
        PropagatorLaw propagatorlaw = new PropagatorLaw( propagator );
        addLaw( ( (Law) ( propagatorlaw ) ) );
    }

    public int numLaws() {
        return laws.size();
    }

    public Law lawAt( int i ) {
        return (Law) laws.get( i );
    }

    public int numParticles() {
        return particles.size();
    }

    public Particle particleAt( int i ) {
        return (Particle) particles.get( i );
    }

    public void iterate( double d ) {
        for ( int i = 0; i < numLaws(); i++ ) {
            lawAt( i ).iterate( d, this );
        }

    }

    Vector particles;
    Vector laws;
}
