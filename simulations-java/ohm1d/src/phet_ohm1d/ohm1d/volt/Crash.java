package phet_ohm1d.ohm1d.volt;

import phet_ohm1d.ohm1d.Electron;
import phet_ohm1d.wire1d.Propagator1d;
import phet_ohm1d.wire1d.WireParticle;

public class Crash implements Propagator1d {
    public void propagate( WireParticle wp, double dt ) {
        Electron e = (Electron)wp;
        if( e.isCollided() ) {
            e.setVelocity( 0 );
            e.setCollided( false );
        }
    }
}
