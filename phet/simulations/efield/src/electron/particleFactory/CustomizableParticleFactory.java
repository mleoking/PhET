// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.particleFactory;

import electron.core.ParticleFactory;
import java.awt.Rectangle;
import java.util.Random;
import phys2d.DoublePoint;
import phys2d.Particle;
import util.Debug;

// Referenced classes of package electron.particleFactory:
//            ParticlePropertyListener

public class CustomizableParticleFactory
    implements ParticleFactory, ParticlePropertyListener
{

    public CustomizableParticleFactory(int i, int j, int k, int l, Particle particle)
    {
        this(new Rectangle(i, j, k, l), particle);
    }

    public CustomizableParticleFactory(Rectangle rectangle, Particle particle)
    {
        bounds = rectangle;
        properties = particle;
    }

    public void propertiesChanged(Particle particle)
    {
        properties = particle;
    }

    public Particle newParticle()
    {
        Particle particle = new Particle();
        int i = rand.nextInt(bounds.width) + bounds.x;
        int j = rand.nextInt(bounds.height) + bounds.y;
        particle.setPosition(new DoublePoint(i, j));
        particle.setCharge(properties.getCharge());
        particle.setMass(properties.getMass());
        Debug.traceln("created: " + particle);
        return particle;
    }

    static Random rand = new Random();
    Rectangle bounds;
    Particle properties;

}
