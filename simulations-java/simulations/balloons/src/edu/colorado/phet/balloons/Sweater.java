package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.phys2d.DoublePoint;

public class Sweater {
    int n;
    DoublePoint ctr;

    public Sweater( int n, DoublePoint ctr ) {
        this.n = n;
        this.ctr = ctr;
    }

    public int getN() {
        return n;
    }

    public DoublePoint getCenter() {
        return ctr;
    }
}
