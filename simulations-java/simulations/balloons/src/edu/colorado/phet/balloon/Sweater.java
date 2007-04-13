package edu.colorado.phet.balloon;

import phet.phys2d.DoublePoint;

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
