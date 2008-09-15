package edu.colorado.phet.balloons.common.phys2d;

public class Particle {
    private DoublePoint x;
    private DoublePoint v;
    private DoublePoint a;
    private double mass;
    private double charge;

    /**
     * Constructs a stationary particle with mass 1.
     */
    public Particle() {
        this( new DoublePoint(), new DoublePoint(), new DoublePoint(), 1 );
    }

    public Particle( DoublePoint x, DoublePoint v, DoublePoint a, double mass ) {
        this( x, v, a, mass, 0.0 );
    }

    public Particle( DoublePoint x, DoublePoint v, DoublePoint a, double mass, double charge ) {
        this.x = x;
        this.v = v;
        this.a = a;
        this.mass = mass;
        this.charge = charge;
    }

    public void setCharge( double ch ) {
        this.charge = ch;
    }

    public double getCharge() {
        return charge;
    }

    public String toString() {
        return "Position=" + x + ", Velocity=" + v + ", Acceleration=" + a + ", Mass=" + mass + ", Charge=" + charge;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setAcceleration( DoublePoint a ) {
        this.a = a;
    }

    public void setAcceleration( double x, double y ) {
        this.a = new DoublePoint( x, y );
    }

    public DoublePoint getAcceleration() {
        return a;
    }

    public DoublePoint getVelocity() {
        return v;
    }

    public double getSpeed() {
        return v.getLength();
    }

    public void setVelocity( DoublePoint v ) {
        this.v = v;
    }

    public DoublePoint getPosition() {
        return x;
    }

    public void setPosition( DoublePoint x ) {
        this.x = x;
    }
}
