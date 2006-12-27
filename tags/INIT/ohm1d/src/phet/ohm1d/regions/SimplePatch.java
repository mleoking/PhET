package phet.ohm1d.regions;

import phet.ohm1d.volt.WireRegion;
import phet.wire1d.WireParticle;
import phet.wire1d.WirePatch;

public class SimplePatch implements WireRegion {
    WirePatch patch;

    public SimplePatch( WirePatch patch ) {
        this.patch = patch;
    }

    public boolean contains( WireParticle wp ) {
        return wp.getWirePatch() == patch;
    }
}
