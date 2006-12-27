/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.physics1d;


/**
 * Represents a 1-d netwonian particle.
 */
public class Particle1d {
    double x;
    double v;
    double mass = 1;
    Force1d force;

    public void stepInTime( double dt ) {
        double a = 0;
        if( force != null ) {
            a = force.getForce( this ) / mass;
        }
        double vInit = v;
        v = a * dt + v;
        x = x + ( ( vInit + v ) / 2.0 ) * dt;
    }

    public double getPosition() {
        return x;
    }

    public void setForce( Force1d force1d ) {
        force = force1d;
    }

    public double getKineticEnergy() {
        return .5 * v * v * mass;
    }

    public double getMass() {
        return mass;
    }

    public double getVelocity() {
        return v;
    }

    public void setVelocity( double v ) {
        this.v = v;
    }

    public void setPosition( double x ) {
        this.x = x;
    }

    public void translate( double dx ) {
        this.x += dx;
    }

}
