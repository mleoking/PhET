package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.Law;
import edu.colorado.phet.common.phys2d.System2D;

public class ShockElectronPropagator implements Law {
    public void iterate( double dt, System2D se ) {
        for( int i = 0; i < se.numParticles(); i++ ) {
            ShockElectron x = (ShockElectron)se.particleAt( i );
            x.getPropagator().propagate( dt, x );
        }
    }
}
