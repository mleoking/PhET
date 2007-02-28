// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.mouse;

import electron.gui.ParticlePainter;
import electron.gui.ParticlePanel;
import java.awt.Point;
import phys2d.Particle;

public class ParticleSelector
{

    public ParticleSelector(ParticlePanel particlepanel)
    {
        pp = particlepanel;
    }

    public Particle selectAt(Point point)
    {
        for(int i = 0; i < pp.numParticles(); i++)
        {
            Particle particle = pp.particleAt(i);
            ParticlePainter particlepainter = pp.painterAt(i);
            if(particlepainter.contains(particle, point))
                return particle;
        }

        return null;
    }

    ParticlePanel pp;
}
