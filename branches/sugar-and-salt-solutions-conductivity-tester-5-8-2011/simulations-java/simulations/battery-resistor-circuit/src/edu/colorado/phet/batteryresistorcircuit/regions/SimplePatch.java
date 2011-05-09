// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.regions;

import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireParticle;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WirePatch;
import edu.colorado.phet.batteryresistorcircuit.volt.WireRegion;

public class SimplePatch implements WireRegion {
    WirePatch patch;

    public SimplePatch( WirePatch patch ) {
        this.patch = patch;
    }

    public boolean contains( WireParticle wp ) {
        return wp.getWirePatch() == patch;
    }
}
