package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.Propagator1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;
import edu.colorado.phet.signalcircuit.electron.wire1d.WirePatch;

public class InsidePropagator implements Propagator1d {
    double v;
    WirePatch patch;

    public InsidePropagator( double v, WirePatch patch ) {
        this.v = v;
        this.patch = patch;
    }

    public void propagate( WireParticle wp, double dt ) {
        double len = patch.getLength();
        double pos = wp.getPosition();
        double x = ( pos + v * dt );
        if( x > len )//+endPadding
        {
            x = x - len;//+endPadding;
        }
        wp.setPosition( x );
        wp.setVelocity( v );
    }
}
