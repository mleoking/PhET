// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.batteryvoltage.common.electron.man.motions.Location;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class FixedLocation implements Location {
    DoublePoint dp;

    public FixedLocation( DoublePoint dp ) {
        this.dp = dp;
    }

    public DoublePoint getPosition() {
        return dp;
    }
}
