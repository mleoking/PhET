// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.particleFactory;

import java.awt.*;
import java.util.Random;

import edu.colorado.phet.efield.electron.core.ParticleFactory;
import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;

public class CustomizableParticleFactory
        implements ParticleFactory, ParticlePropertyListener {

    public CustomizableParticleFactory( int i, int j, int k, int l, Particle particle ) {
        this( new Rectangle( i, j, k, l ), particle );
    }

    public CustomizableParticleFactory( Rectangle rectangle, Particle particle ) {
        bounds = rectangle;
        properties = particle;
    }

    public void propertiesChanged( Particle particle ) {
        properties = particle;
    }

    public Particle newParticle() {
        Particle particle = new Particle();
        int i = rand.nextInt( bounds.width ) + bounds.x;
        int j = rand.nextInt( bounds.height ) + bounds.y;
        particle.setPosition( new DoublePoint( i, j ) );
        particle.setCharge( properties.getCharge() );
        particle.setMass( properties.getMass() );
        return particle;
    }

    static Random rand = new Random();
    Rectangle bounds;
    Particle properties;

}
