package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.Propagator1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;

public class SignalPropagator3 implements Propagator1d, SwitchListener {
    boolean switchClosed;
    Propagator1d open;
    Propagator1d closed;

    public SignalPropagator3( Propagator1d open, Propagator1d closed, boolean switchClosed ) {
        this.switchClosed = switchClosed;
        this.open = open;
        this.closed = closed;
    }

    public void setSwitchClosed( boolean c ) {
        this.switchClosed = c;

    }

    public void propagate( WireParticle wp, double dt ) {
        if( switchClosed ) {
            closed.propagate( wp, dt );
        }
        else {
            open.propagate( wp, dt );
        }
    }
}
