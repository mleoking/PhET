// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.Propagator;

// Referenced classes of package phys2d.propagators:
//            NorthBounce, SouthBounce, EastBounce, WestBounce

public class FourBounds
        implements Propagator {

    public FourBounds( double d, double d1, double d2, double d3, double d4 ) {
        n = new NorthBounce( d1, d4 );
        s = new SouthBounce( d1 + d3, d4 );
        e = new EastBounce( d + d2, d4 );
        w = new WestBounce( d, d4 );
    }

    public void propagate( double d, Particle particle ) {
        n.propagate( d, particle );
        e.propagate( d, particle );
        w.propagate( d, particle );
        s.propagate( d, particle );
    }

    NorthBounce n;
    SouthBounce s;
    EastBounce e;
    WestBounce w;
}
