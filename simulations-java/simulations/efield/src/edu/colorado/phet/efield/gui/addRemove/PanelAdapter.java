// Copyright 2002-2011, University of Colorado

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
