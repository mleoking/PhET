package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;

public class RemovalPropagator implements Propagator {
    CompositeParticleContainer cpc;

    public RemovalPropagator( CompositeParticleContainer cpc ) {
        this.cpc = cpc;
    }

    public void propagate( double dt, Particle p ) {
        cpc.remove( p );
    }
}
