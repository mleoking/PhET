package edu.colorado.phet.batteryvoltage.common.phys2d;

import java.util.Vector;

public class CompositePropagator implements Propagator {
    Vector x = new Vector();

    public Propagator propagatorAt( int i ) {
        return (Propagator) x.get( i );
    }

    public void add( Propagator p ) {
        x.add( p );
    }

    public int numPropagators() {
        return x.size();
    }

    public void propagate( double dt, Particle p ) {
        for ( int i = 0; i < numPropagators(); i++ ) {
            propagatorAt( i ).propagate( dt, p );
        }
    }
}
