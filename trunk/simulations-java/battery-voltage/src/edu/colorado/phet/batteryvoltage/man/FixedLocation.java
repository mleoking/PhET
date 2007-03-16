package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.common.batteryvoltage.electron.man.motions.Location;
import edu.colorado.phet.common.batteryvoltage.phys2d.DoublePoint;

public class FixedLocation implements Location {
    DoublePoint dp;

    public FixedLocation( DoublePoint dp ) {
        this.dp = dp;
    }

    public DoublePoint getPosition() {
        return dp;
    }
}
