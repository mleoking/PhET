// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package phys2d.propagators;

import phys2d.*;

public class ResetAcceleration
    implements Propagator
{

    public ResetAcceleration()
    {
    }

    public void propagate(double d, Particle particle)
    {
        DoublePoint doublepoint = new DoublePoint();
        particle.setAcceleration(doublepoint);
    }
}
