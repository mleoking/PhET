// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.regions;

import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireParticle;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WirePatch;
import edu.colorado.phet.batteryresistorcircuit.volt.WireRegion;

public class PatchRegion implements WireRegion {
    double min;
    double max;
    WirePatch patch;

    public PatchRegion( double min, double max, WirePatch patch ) {
        this.min = min;
        this.max = max;
        this.patch = patch;
    }

    public boolean contains( WireParticle wp ) {
        return wp.getWirePatch() == patch && max >= wp.getPosition() && min <= wp.getPosition();
    }
}
