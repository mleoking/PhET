package edu.colorado.phet.signalcircuit.phys2d.gui;

public class Range {
    double a;
    double b;

    public Range( double a, double b ) {
        this.a = a;
        this.b = b;
    }

    public double getLength() {
        return a - b;
    }

    /*Convert the specified value from this range to the input range.*/
    public double convertTo( Range r, double value ) {
        double scale = r.getLength() / getLength();
        return ( value - a ) * scale + r.a;
    }
}
