package edu.colorado.phet.ohm1d.regions;

import edu.colorado.phet.ohm1d.volt.WireRegion;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.common.wire1d.WirePatch;

public class SimplePatch implements WireRegion {
    WirePatch patch;

    public SimplePatch( WirePatch patch ) {
        this.patch = patch;
    }

    public boolean contains( WireParticle wp ) {
        return wp.getWirePatch() == patch;
    }
}
