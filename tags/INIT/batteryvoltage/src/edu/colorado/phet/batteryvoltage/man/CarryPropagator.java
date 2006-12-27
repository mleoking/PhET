package edu.colorado.phet.batteryvoltage.man;

import electron.man.Man;
import phys2d.Particle;
import phys2d.Propagator;

public class CarryPropagator implements Propagator {
    Man m;

    public CarryPropagator( Man m ) {
        this.m = m;
    }

    public void propagate( double dt, Particle p ) {
        p.setPosition( m.getNeck().getPosition() );
    }
}
