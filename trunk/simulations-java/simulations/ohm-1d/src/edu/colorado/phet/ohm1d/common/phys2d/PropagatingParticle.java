package edu.colorado.phet.ohm1d.common.phys2d;

import edu.colorado.phet.ohm1d.common.phys2d.propagators.NullPropagator;

public class PropagatingParticle extends Particle {
    Propagator p;

    public PropagatingParticle() {
        this( new NullPropagator() );
    }

    public PropagatingParticle( Propagator p ) {
        this.p = p;
    }

    public void setPropagator( Propagator p ) {
        this.p = p;
    }

    public Propagator getPropagator() {
        return p;
    }
}
