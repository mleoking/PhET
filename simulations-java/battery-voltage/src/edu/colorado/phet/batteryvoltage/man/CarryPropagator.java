package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.common.batteryvoltage.electron.man.Man;
import edu.colorado.phet.common.batteryvoltage.phys2d.Particle;
import edu.colorado.phet.common.batteryvoltage.phys2d.Propagator;

public class CarryPropagator implements Propagator {
    Man m;

    public CarryPropagator( Man m ) {
        this.m = m;
    }

    public void propagate( double dt, Particle p ) {
        p.setPosition( m.getNeck().getPosition() );
    }
}
