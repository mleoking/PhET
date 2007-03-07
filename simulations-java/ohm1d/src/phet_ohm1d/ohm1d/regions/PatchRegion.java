package phet_ohm1d.ohm1d.regions;

import phet_ohm1d.ohm1d.volt.WireRegion;
import phet_ohm1d.wire1d.WireParticle;
import phet_ohm1d.wire1d.WirePatch;

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
