package edu.colorado.phet.ohm1d.volt;

import edu.colorado.phet.ohm1d.Electron;
import edu.colorado.phet.ohm1d.common.wire1d.Propagator1d;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;

public class Crash implements Propagator1d {
    public void propagate( WireParticle wp, double dt ) {
        Electron e = (Electron) wp;
        if ( e.isCollided() ) {
            e.setVelocity( 0 );
            e.setCollided( false );
        }
    }
}
