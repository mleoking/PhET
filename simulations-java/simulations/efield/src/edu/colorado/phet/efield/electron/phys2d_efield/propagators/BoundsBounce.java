// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.phys2d_efield.propagators;

import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;
import edu.colorado.phet.efield.electron.phys2d_efield.Propagator;

public abstract class BoundsBounce
        implements Propagator {

    public BoundsBounce() {
    }

    public void propagate( double d, Particle particle ) {
        DoublePoint doublepoint = particle.getVelocity();
        DoublePoint doublepoint1 = particle.getPosition();
        if ( isOutOfBounds( doublepoint1 ) ) {
            DoublePoint doublepoint2 = getNewVelocity( doublepoint );
            particle.setVelocity( doublepoint2 );
            particle.setPosition( getPointAtBounds( doublepoint1 ) );
            particle.setAcceleration( new DoublePoint() );
        }
    }

    public abstract DoublePoint getPointAtBounds( DoublePoint doublepoint );

    public abstract boolean isOutOfBounds( DoublePoint doublepoint );

    public abstract DoublePoint getNewVelocity( DoublePoint doublepoint );
}
