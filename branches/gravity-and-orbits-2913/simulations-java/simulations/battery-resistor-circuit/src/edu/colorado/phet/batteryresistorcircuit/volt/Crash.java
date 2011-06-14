// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.volt;

import edu.colorado.phet.batteryresistorcircuit.Electron;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.Propagator1d;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireParticle;

public class Crash implements Propagator1d {
    public void propagate( WireParticle wp, double dt ) {
        Electron e = (Electron) wp;
        if ( e.isCollided() ) {
            e.setVelocity( 0 );
            e.setCollided( false );
        }
    }
}
