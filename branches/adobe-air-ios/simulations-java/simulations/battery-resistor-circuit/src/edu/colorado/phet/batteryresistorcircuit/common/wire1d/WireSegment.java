// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.common.wire1d;

import edu.colorado.phet.batteryresistorcircuit.common.phys2d.DoublePoint;

public class WireSegment {
    DoublePoint start;
    DoublePoint finish;
    double length;
    double scalarStart;
    double scalarFinish;

    public WireSegment( DoublePoint start, DoublePoint finish, double scalarStart ) {
        this.start = start;
        this.finish = finish;
        this.start = start;
        this.length = finish.subtract( start ).length();
        this.scalarStart = scalarStart;
        this.scalarFinish = scalarStart + length;
    }

    public DoublePoint getStart() {
        return start;
    }

    public double getFinishScalar() {
        return scalarFinish;
    }

    public String toString() {
        return "Start=" + start + ", finish=" + finish + ", length=" + length + ", scalarStart=" + scalarStart + ", scalarFinish=" + scalarFinish;
    }

    public DoublePoint getFinish() {
        return finish;
    }

    public boolean contains( double dist ) {
        return dist >= scalarStart && dist <= scalarFinish;
    }

    public DoublePoint getPosition( double dist ) {
        double rel = dist - scalarStart;
        DoublePoint dx = finish.subtract( start ).normalize();
        return dx.multiply( rel ).add( start );
    }

    public double length() {
        return length;
    }
}
