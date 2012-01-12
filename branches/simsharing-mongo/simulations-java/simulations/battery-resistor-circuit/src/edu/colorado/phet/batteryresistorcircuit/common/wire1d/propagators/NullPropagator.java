// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.common.wire1d.propagators;

import edu.colorado.phet.batteryresistorcircuit.common.wire1d.Propagator1d;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireParticle;

public class NullPropagator implements Propagator1d {
    public void propagate( WireParticle wp, double dt ) {
    }
}
