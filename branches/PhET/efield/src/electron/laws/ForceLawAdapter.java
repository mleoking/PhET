// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.laws;

import electron.core.ParticleList;
import phys2d.*;

// Referenced classes of package electron.laws:
//            ForceLaw

public class ForceLawAdapter extends ParticleList
    implements Law
{

    public ForceLawAdapter(Particle aparticle[], ForceLaw forcelaw)
    {
        addAll(aparticle);
        law = forcelaw;
        if(forcelaw == null)
            throw new RuntimeException("Law is null.");
        else
            return;
    }

    public String toString()
    {
        return getClass().getName() + ", law=" + law + ", numCharges=" + numParticles();
    }

    public void iterate(double d, System2D system2d)
    {
        for(int i = 0; i < numParticles(); i++)
        {
            DoublePoint doublepoint = new DoublePoint();
            for(int j = 0; j < numParticles(); j++)
                if(i != j)
                    doublepoint = doublepoint.add(law.getForce(particleAt(j), particleAt(i)));

            double d1 = particleAt(i).getMass();
            DoublePoint doublepoint1 = doublepoint.multiply(1.0D / d1);
            DoublePoint doublepoint2 = particleAt(i).getAcceleration();
            DoublePoint doublepoint3 = doublepoint2.add(doublepoint1);
            particleAt(i).setAcceleration(doublepoint3);
        }

    }

    ForceLaw law;
}
