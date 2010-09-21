package edu.colorado.phet.signalcircuit.electron.wire1d.propagators;

import edu.colorado.phet.signalcircuit.electron.wire1d.Propagator1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;
import edu.colorado.phet.signalcircuit.electron.wire1d.WirePatch;

public class WireEndPropagator implements Propagator1d {
    WirePatch patch;

    public WireEndPropagator( WirePatch patch ) {
        this.patch = patch;
    }

    public void propagate( WireParticle wp, double dt ) {
        double dist = patch.getLength();
        double x = wp.getPosition();
        double newX = x;
        double newV = wp.getVelocity();
        if( x < 0 ) {
            newX = 0;
            newV = 0;
        }
        else if( x >= dist ) {
            newX = dist;
            newV = 0;
        }
        wp.setPosition( newX );
        wp.setVelocity( newV );
    }
}
