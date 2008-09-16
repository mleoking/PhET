// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.phys2d_efield;

// Referenced classes of package phys2d:
//            DoublePoint

public class Particle {

    public Particle() {
        this( new DoublePoint(), new DoublePoint(), new DoublePoint(), 1.0D );
    }

    public Particle( DoublePoint doublepoint, DoublePoint doublepoint1, DoublePoint doublepoint2, double d ) {
        this( doublepoint, doublepoint1, doublepoint2, d, 0.0D );
    }

    public Particle( DoublePoint doublepoint, DoublePoint doublepoint1, DoublePoint doublepoint2, double d, double d1 ) {
        x = doublepoint;
        v = doublepoint1;
        a = doublepoint2;
        mass = d;
        charge = d1;
    }

    public void setCharge( double d ) {
        charge = d;
    }

    public double getCharge() {
        return charge;
    }

    public String toString() {
        return "Position=" + x + ", Velocity=" + v + ", Acceleration=" + a + ", Mass=" + mass + ", Charge=" + charge;
    }

    public void setMass( double d ) {
        mass = d;
    }

    public double getMass() {
        return mass;
    }

    public void setAcceleration( DoublePoint doublepoint ) {
        a = doublepoint;
    }

    public DoublePoint getAcceleration() {
        return a;
    }

    public DoublePoint getVelocity() {
        return v;
    }

    public void setVelocity( DoublePoint doublepoint ) {
        v = doublepoint;
    }

    public DoublePoint getPosition() {
        return x;
    }

    public void setPosition( DoublePoint doublepoint ) {
        x = doublepoint;
    }

    DoublePoint x;
    DoublePoint v;
    DoublePoint a;
    double mass;
    double charge;
}
