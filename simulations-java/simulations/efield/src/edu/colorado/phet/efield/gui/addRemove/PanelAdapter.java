// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.addRemove;

import edu.colorado.phet.efield.core.ParticleContainer;
import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.phys2d_efield.Particle;

public class PanelAdapter
        implements ParticleContainer {

    public PanelAdapter( ParticlePanel particlepanel, ParticlePainter particlepainter ) {
        pan = particlepanel;
        pp = particlepainter;
    }

    public void add( Particle particle ) {
        pan.add( particle, pp );
    }

    public void remove( Particle particle ) {
        pan.remove( particle );
    }

    public int numParticles() {
        return pan.numParticles();
    }

    public Particle particleAt( int i ) {
        return pan.particleAt( i );
    }

    ParticlePanel pan;
    ParticlePainter pp;
}
