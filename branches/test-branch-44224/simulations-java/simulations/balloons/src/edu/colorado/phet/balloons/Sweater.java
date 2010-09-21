package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.phys2d.DoublePoint;

public class Sweater {
    private DoublePoint ctr;

    public Sweater( DoublePoint ctr ) {
        this.ctr = ctr;
    }

    public DoublePoint getCenter() {
        return ctr;
    }
}
