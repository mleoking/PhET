// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield;

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
