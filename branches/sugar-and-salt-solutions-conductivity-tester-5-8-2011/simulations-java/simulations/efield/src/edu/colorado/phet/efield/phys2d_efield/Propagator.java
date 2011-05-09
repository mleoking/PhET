// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield;

// Referenced classes of package phys2d:
//            Particle

public interface Propagator {

    public abstract void propagate( double d, Particle particle );
}
