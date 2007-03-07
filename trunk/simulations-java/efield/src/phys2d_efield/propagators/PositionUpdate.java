// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package phys2d_efield.propagators;

import phys2d_efield.*;

public class PositionUpdate
    implements Propagator
{

    public PositionUpdate()
    {
    }

    public void propagate(double d, Particle particle)
    {
        DoublePoint doublepoint = particle.getVelocity();
        DoublePoint doublepoint1 = particle.getPosition();
        DoublePoint doublepoint2 = doublepoint.multiply(d);
        DoublePoint doublepoint3 = doublepoint1.add(doublepoint2);
        particle.setPosition(doublepoint3);
    }

}
