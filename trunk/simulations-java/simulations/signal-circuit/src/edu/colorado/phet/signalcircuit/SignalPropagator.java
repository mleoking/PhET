package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.*;

import java.util.Vector;

public class SignalPropagator implements Propagator1d {
    Vector forces = new Vector();
    double vMax;
    boolean switchClosed = false;
    WirePatch patch;

    public SignalPropagator( double vMax, WirePatch wp ) {
        this.patch = wp;
        this.vMax = vMax;
    }

    public void setMaxVelocity( double vMax ) {
        this.vMax = vMax;
    }

    public void addForce( Force1d f ) {
        forces.add( f );
    }

    public void propagate( WireParticle wp, double dt ) {
        double dist = patch.totalDistance();
        double m = wp.getMass();
        double v = wp.getVelocity();
        double x = wp.getPosition();
        //edu.colorado.phet.util.Debug.traceln("Started propagate,: x="+x+", v="+v+", a="+a+",this="+this);

        double force = 0;
        for( int i = 0; i < forces.size(); i++ ) {
            //o.O.d("Force["+i+"]="+forces.get(i)+", force="+force);
            force += ( (Force1d)forces.get( i ) ).getForce( wp );
        }

        //o.O.d("Force="+force);
        double a = force / m;

        //v=v+a*dt;
        v = v + a * dt;
        //edu.colorado.phet.util.Debug.traceln("After accel,: x="+x+", v="+v+", a="+a+",this="+this);
        boolean positive = v >= 0;
        v = Math.abs( v );
        v = Math.min( vMax, v );
        if( !positive ) {
            v *= -1;
        }
        //edu.colorado.phet.util.Debug.traceln("After abs and min,: x="+x+", v="+v+", a="+a+",this="+this);
        x = x + v * dt;
        //edu.colorado.phet.util.Debug.traceln("Propagating wire particle: x="+x+", v="+v+", a="+a+",this="+this);
        double newX = x;
        if( switchClosed ) {
            if( x <= 0 ) {
                newX = dist - Math.abs( x );
            }
            else if( x >= dist ) {
                newX = x - dist;
            }
        }
        else {
            if( x < 0 ) {
                newX = 0;
                v = 0;
            }
            else if( x >= dist ) {
                newX = dist;
                v = 0;
            }
        }
        wp.setVelocity( v );
        wp.setPosition( newX );
    }
}
