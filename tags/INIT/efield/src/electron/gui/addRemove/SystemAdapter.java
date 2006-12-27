// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.addRemove;

import electron.core.ParticleContainer;
import phys2d.Particle;
import phys2d.System2D;

public class SystemAdapter
    implements ParticleContainer
{

    public SystemAdapter(System2D system2d)
    {
        sys = system2d;
    }

    public void add(Particle particle)
    {
        sys.addParticle(particle);
    }

    public void remove(Particle particle)
    {
        sys.remove(particle);
    }

    public int numParticles()
    {
        return sys.numParticles();
    }

    public Particle particleAt(int i)
    {
        return sys.particleAt(i);
    }

    System2D sys;
}
