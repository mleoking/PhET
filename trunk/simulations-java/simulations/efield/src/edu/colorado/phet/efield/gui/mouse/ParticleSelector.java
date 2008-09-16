// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.mouse;

import java.awt.*;

import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.phys2d_efield.Particle;

public class ParticleSelector {

    public ParticleSelector( ParticlePanel particlepanel ) {
        pp = particlepanel;
    }

    public Particle selectAt( Point point ) {
        for ( int i = 0; i < pp.numParticles(); i++ ) {
            Particle particle = pp.particleAt( i );
            ParticlePainter particlepainter = pp.painterAt( i );
            if ( particlepainter.contains( particle, point ) ) {
                return particle;
            }
        }

        return null;
    }

    ParticlePanel pp;
}
