package edu.colorado.phet.signalcircuit.electron.wire1d;

import edu.colorado.phet.signalcircuit.phys2d.DoublePoint;

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

    public void setFinish( DoublePoint finish ) {
        this.finish = finish;
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
