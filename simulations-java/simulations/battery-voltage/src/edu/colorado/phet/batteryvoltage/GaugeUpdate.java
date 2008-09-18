package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.batteryvoltage.common.electron.components.Gauge;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.PropagatingParticle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;
import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;

public class GaugeUpdate implements ParticleMoveListener {
    Gauge g;
    System2D sys;
    Propagator right;
    Propagator left;

    public GaugeUpdate( Gauge g, int numElectrons, System2D sys, Propagator right, Propagator left ) {
        this.left = left;
        this.sys = sys;
        this.right = right;
        this.g = g;
    }

    public void particleMoved( Battery source, Particle p ) {
        int num = 0;
        for ( int i = 0; i < sys.numParticles(); i++ ) {
            PropagatingParticle pp = (PropagatingParticle) sys.particleAt( i );
            if ( pp.getPropagator() == right ) {
                num++;
            }
            if ( pp.getPropagator() == left ) {
                num--;
            }
        }
        //int num=numRight*2-numElectrons;
        g.setValue( num );
    }
}
