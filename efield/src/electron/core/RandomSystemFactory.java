// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.core;

import electron.gui.ParticlePainter;
import electron.gui.ParticlePanel;
import electron.gui.RepaintLaw;
import phys2d.DoublePoint;
import phys2d.Law;
import phys2d.Particle;
import phys2d.System2D;
import phys2d.propagators.FourBounds;
import phys2d.propagators.PositionUpdate;
import phys2d.propagators.ResetAcceleration;
import phys2d.propagators.VelocityUpdate;

import java.util.Random;
import java.util.Vector;

// Referenced classes of package electron.core:
//            ParticleContainer, SystemFactory

public class RandomSystemFactory
        implements SystemFactory {

    public RandomSystemFactory( int i, int j, int k, int l, int i1, int j1 ) {
        forceLaws = new Vector();
        seed = i;
        numParticles = j;
        minX = k;
        minY = l;
        width = i1;
        height = j1;
    }

    public void addForceLaw( Law law ) {
        forceLaws.add( law );
    }

    public static Particle newParticle( int i, int j, double d ) {
        Particle particle = new Particle();
        particle.setPosition( new DoublePoint( i, j ) );
        particle.setCharge( d );
        return particle;
    }

    public System2D newSystem() {
        Random random = new Random( seed );
        System2D system2d = new System2D();
        Vector vector = new Vector();
        for( int i = 0; i < numParticles; i++ ) {
            int j = random.nextInt( width ) + minX;
            int k = random.nextInt( height ) + minY;
            Particle particle = newParticle( j, k, -1D );
            system2d.addParticle( particle );
            vector.add( particle );
        }

        Particle aparticle[] = (Particle[])vector.toArray( new Particle[vector.size()] );
        system2d.addLaw( new ResetAcceleration() );
        system2d.addLaw( new FourBounds( minX, minY, width, height, 1.2D ) );
        for( int l = 0; l < forceLaws.size(); l++ ) {
            Law law = (Law)forceLaws.get( l );
            system2d.addLaw( law );
            if( law instanceof ParticleContainer ) {
                for( int i1 = 0; i1 < aparticle.length; i1++ ) {
                    ( (ParticleContainer)law ).add( aparticle[i1] );
                }

            }
        }

        system2d.addLaw( new VelocityUpdate( 100D ) );
        system2d.addLaw( new PositionUpdate() );
        return system2d;
    }

    public void updatePanel( ParticlePanel particlepanel, System2D system2d, ParticlePainter particlepainter ) {
        particlepanel.addAll( system2d, particlepainter );
        system2d.addLaw( new RepaintLaw( particlepanel ) );
    }

    int seed;
    int numParticles;
    int minX;
    int minY;
    int width;
    int height;
    Vector forceLaws;
}
