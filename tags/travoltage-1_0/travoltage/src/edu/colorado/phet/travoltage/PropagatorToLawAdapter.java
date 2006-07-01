package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.Law;
import edu.colorado.phet.common.phys2d.Propagator;
import edu.colorado.phet.common.phys2d.System2D;

public class PropagatorToLawAdapter implements Law {
    public void iterate( double dt, System2D sys ) {
        for( int i = 0; i < sys.numParticles(); i++ ) {
            ShockElectron se = (ShockElectron)sys.particleAt( i );
            Propagator p = se.getPropagator();
            if( p == null ) {
                throw new RuntimeException( "Null propagator." );
            }
            p.propagate( dt, se );
        }
    }
}

