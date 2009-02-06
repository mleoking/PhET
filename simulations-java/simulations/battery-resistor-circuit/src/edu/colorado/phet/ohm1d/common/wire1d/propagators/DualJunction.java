package edu.colorado.phet.ohm1d.common.wire1d.propagators;

import edu.colorado.phet.ohm1d.common.wire1d.Propagator1d;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.common.wire1d.WirePatch;

/**
 * This matches the high end of a and the low end of b.
 */
public class DualJunction implements Propagator1d {
    WirePatch a;
    WirePatch b;

    public DualJunction( WirePatch a, WirePatch b ) {
        this.a = a;
        this.b = b;
    }

    public void propagate( WireParticle p, double dt ) {
        if ( p.getWirePatch() == a && p.getPosition() >= a.getLength() ) {
            p.setWirePatch( b );
            p.setPosition( 1 );//This should maybe be b.getScalarStart().
        }
        else if ( p.getWirePatch() == b && p.getPosition() <= 0 ) {
            p.setWirePatch( a );
            p.setPosition( a.getLength() - 4 );
        }
    }
}
