// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.Propagator;

public class ResetAcceleration
        implements Propagator {

    public ResetAcceleration() {
    }

    public void propagate( double d, Particle particle ) {
        DoublePoint doublepoint = new DoublePoint();
        particle.setAcceleration( doublepoint );
    }
}
