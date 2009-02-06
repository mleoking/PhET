package edu.colorado.phet.ohm1d.common.wire1d.propagators;

import java.util.Vector;

import edu.colorado.phet.ohm1d.common.wire1d.Propagator1d;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;

public class CompositePropagator1d implements Propagator1d {
    Vector v = new Vector();

    public void addPropagator( Propagator1d f ) {
        v.add( f );
    }

    public void propagate( WireParticle wp, double dt ) {
        for ( int i = 0; i < v.size(); i++ ) {
            ( (Propagator1d) v.get( i ) ).propagate( wp, dt );
        }
    }
}
