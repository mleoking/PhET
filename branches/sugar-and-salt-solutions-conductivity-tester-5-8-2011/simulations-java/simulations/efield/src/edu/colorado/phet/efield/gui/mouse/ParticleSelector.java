// Copyright 2002-2011, University of Colorado

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
