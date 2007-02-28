// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.addRemove;

import electron.core.ParticleContainer;
import electron.gui.ParticlePainter;
import electron.gui.ParticlePanel;
import phys2d.Particle;

public class PanelAdapter
    implements ParticleContainer
{

    public PanelAdapter(ParticlePanel particlepanel, ParticlePainter particlepainter)
    {
        pan = particlepanel;
        pp = particlepainter;
    }

    public void add(Particle particle)
    {
        pan.add(particle, pp);
    }

    public void remove(Particle particle)
    {
        pan.remove(particle);
    }

    public int numParticles()
    {
        return pan.numParticles();
    }

    public Particle particleAt(int i)
    {
        return pan.particleAt(i);
    }

    ParticlePanel pan;
    ParticlePainter pp;
}
