// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.phys2d_efield;

// Referenced classes of package phys2d:
//            Law, System2D, Propagator

public class PropagatorLaw
        implements Law {

    public PropagatorLaw( Propagator propagator ) {
        p = propagator;
    }

    public void iterate( double d, System2D system2d ) {
        for ( int i = 0; i < system2d.numParticles(); i++ ) {
            p.propagate( d, system2d.particleAt( i ) );
        }

    }

    Propagator p;
}
