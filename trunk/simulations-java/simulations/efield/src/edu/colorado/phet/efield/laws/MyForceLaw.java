// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.laws;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;

public interface MyForceLaw {

    public abstract DoublePoint getForce( Particle particle, Particle particle1 );
}
