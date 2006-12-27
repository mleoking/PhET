package edu.colorado.phet.common;

import edu.colorado.phet.common.gui.ParticlePainter;
import edu.colorado.phet.common.gui.ParticlePanel;
import edu.colorado.phet.common.phys2d.Particle;

public class PanelAdapter implements ParticleContainer {
    ParticlePanel pan;
    ParticlePainter pp;

    public PanelAdapter( ParticlePanel pan, ParticlePainter pp ) {
        this.pan = pan;
        this.pp = pp;
    }

    public void setPainter( ParticlePainter pp ) {
        this.pp = pp;
    }

    public void add( Particle p ) {
        pan.add( p, pp );
    }

    public void remove( Particle p ) {
        pan.remove( p );
    }

    public int numParticles() {
        return pan.numParticles();
    }

    public Particle particleAt( int i ) {
        return pan.particleAt( i );
    }
}
