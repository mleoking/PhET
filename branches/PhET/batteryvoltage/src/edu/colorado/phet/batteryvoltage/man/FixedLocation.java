package edu.colorado.phet.batteryvoltage.man;

import electron.man.motions.Location;
import phys2d.DoublePoint;

public class FixedLocation implements Location {
    DoublePoint dp;

    public FixedLocation( DoublePoint dp ) {
        this.dp = dp;
    }

    public DoublePoint getPosition() {
        return dp;
    }
}
