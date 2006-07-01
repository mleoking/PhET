package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;

public class ShockElectron extends Particle {
    Propagator propagator;

    public ShockElectron( Propagator propagator ) {
        this.propagator = propagator;
    }

    public Propagator getPropagator() {
        return propagator;
    }

    public void setPropagator( Propagator p ) {
        this.propagator = p;
    }
}
