package edu.colorado.phet.common.gui.grabber;

import edu.colorado.phet.common.gui.ParticlePainter;
import edu.colorado.phet.common.gui.ParticlePanel;
import edu.colorado.phet.common.phys2d.Particle;

import java.awt.*;

public class ParticleSelector {
    ParticlePanel pp;

    public ParticleSelector( ParticlePanel pc ) {
        this.pp = pc;
    }

    /**
     * Grab the topmost edu.colorado.phet.common under the grabber, if any.
     */
    public Particle selectAt( Point pt ) {
        for( int i = 0; i < pp.numParticles(); i++ ) {
            Particle p = pp.particleAt( i );
            ParticlePainter painter = pp.painterAt( i );
            if( painter.contains( p, pt ) ) {
                return p;
            }
        }
        return null;
    }
}
