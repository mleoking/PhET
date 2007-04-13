package edu.colorado.phet.ohm1d.regions;

import edu.colorado.phet.ohm1d.volt.WireRegion;
import phet_ohm1d.wire1d.WireParticle;
import phet_ohm1d.wire1d.WirePatch;

public class SimplePatch implements WireRegion {
    WirePatch patch;

    public SimplePatch( WirePatch patch ) {
        this.patch = patch;
    }

    public boolean contains( WireParticle wp ) {
        return wp.getWirePatch() == patch;
    }
}
