package edu.colorado.phet.ohm1d.volt;

import java.util.Vector;

import edu.colorado.phet.ohm1d.common.wire1d.Force1d;
import edu.colorado.phet.ohm1d.common.wire1d.Propagator1d;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.gui.VoltageListener;

public class BatteryForcePropagator implements Propagator1d, VoltageListener {
    Vector forces = new Vector();
    double minSpeed;
    double maxSpeed;
    private double desiredVoltage;

    public BatteryForcePropagator( double minSpeed, double maxSpeed ) {
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }

    public void setMinSpeed( double vMin ) {
        this.minSpeed = vMin;
    }

    public void addForce( Force1d f ) {
        forces.add( f );
    }

    public void propagate( WireParticle wp, double dt ) {
        double f = 0;
        for ( int i = 0; i < forces.size(); i++ ) {
            f += ( (Force1d) forces.get( i ) ).getForce( wp );
        }
        double m = wp.getMass();
        double v = wp.getVelocity();
        double x = wp.getPosition();
        //util.Debug.traceln("Started propagate,: x="+x+", v="+v+", a="+a+",this="+this);
        //System.err.println("Force="+force);
        double a = f / m;
        //v=v+a*dt;
        v = v + a * dt;
        double vpre = v;
        if ( desiredVoltage < 0 ) //going clockwise... Positive velocity required.
        {
            if ( v > maxSpeed ) {
                v = maxSpeed;
            }
            else if ( v < minSpeed ) {
                v = minSpeed;
            }
        }
        else {
            if ( v < -maxSpeed ) {
                v = -maxSpeed;
            }
            else if ( v > -minSpeed ) {
                v = -minSpeed;
            }
            //v=Math.max(-
        }
        // System.err.println("min="+minSpeed+", max="+maxSpeed+", vpre="+vpre+", v="+v);
        wp.setVelocity( v );
        //util.Debug.traceln("After abs and min,: x="+x+", v="+v+", a="+a+",this="+this);
        double newX = x + wp.getVelocity() * dt;
        //util.Debug.traceln("Propagating wire particle: x="+x+", v="+v+", a="+a+",this="+this);
        //double newX = x;
        //wp.setVelocity(v);
        wp.setPosition( newX );
    }

    public void valueChanged( double val ) {
        this.desiredVoltage = val;

        setMinSpeed( Math.abs( val * .7 ) );//val);
        //System.err.println("Set min speed="+minSpeed);
    }
}
