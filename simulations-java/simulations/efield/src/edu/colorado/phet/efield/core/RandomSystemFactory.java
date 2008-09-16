// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.core;

import java.util.Random;
import java.util.Vector;

import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.gui.RepaintLaw;
import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Law;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.System2D;
import edu.colorado.phet.efield.phys2d_efield.propagators.FourBounds;
import edu.colorado.phet.efield.phys2d_efield.propagators.PositionUpdate;
import edu.colorado.phet.efield.phys2d_efield.propagators.ResetAcceleration;
import edu.colorado.phet.efield.phys2d_efield.propagators.VelocityUpdate;

public class RandomSystemFactory implements SystemFactory {

    int seed;
    int numParticles;
    int minX;
    int minY;
    int width;
    int height;
    Vector forceLaws;

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
        for ( int i = 0; i < numParticles; i++ ) {
            int j = random.nextInt( width ) + minX;
            int k = random.nextInt( height ) + minY;
            Particle particle = newParticle( j, k, -1D );
            system2d.addParticle( particle );
            vector.add( particle );
        }

        Particle aparticle[] = (Particle[]) vector.toArray( new Particle[vector.size()] );
        system2d.addLaw( new ResetAcceleration() );
        system2d.addLaw( new FourBounds( minX, minY, width, height, 1.2D ) );
        for ( int l = 0; l < forceLaws.size(); l++ ) {
            Law law = (Law) forceLaws.get( l );
            system2d.addLaw( law );
            if ( law instanceof ParticleContainer ) {
                for ( int i1 = 0; i1 < aparticle.length; i1++ ) {
                    ( (ParticleContainer) law ).add( aparticle[i1] );
                }

            }
        }

        system2d.addLaw( new VelocityUpdate( 100D ) );
        system2d.addLaw( new PositionUpdate() );
        return system2d;
    }

    public void updatePanel( ParticlePanel particlepanel, System2D system2d, ParticlePainter particlepainter ) {
        particlepanel.removeAll();
        particlepanel.addAll( system2d, particlepainter );
        system2d.addLaw( new RepaintLaw( particlepanel ) );
    }

}
