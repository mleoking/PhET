// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.core;

import phys2d.Particle;

public interface ParticleContainer {

    public abstract void add( Particle particle );

    public abstract void remove( Particle particle );

    public abstract int numParticles();

    public abstract Particle particleAt( int i );
}
