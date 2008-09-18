package edu.colorado.phet.signalcircuit.electron.wire1d.propagators;

import edu.colorado.phet.signalcircuit.electron.wire1d.Force1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.Propagator1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;

import java.util.Vector;

public class ForcePropagator implements Propagator1d {
    Vector forces = new Vector();
    double vMax;

    public ForcePropagator( double vMax ) {
        this.vMax = vMax;
    }

    public void addForce( Force1d f ) {
        forces.add( f );
    }

    public void propagate( WireParticle wp, double dt ) {
        double f = 0;
        for( int i = 0; i < forces.size(); i++ ) {
            f += ( (Force1d)forces.get( i ) ).getForce( wp );
        }
        double m = wp.getMass();
        double v = wp.getVelocity();
        double x = wp.getPosition();
        //edu.colorado.phet.util.Debug.traceln("Started propagate,: x="+x+", v="+v+", a="+a+",this="+this);

        //o.O.d("Force="+force);
        double a = f / m;

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
        wp.setVelocity( v );
        wp.setPosition( newX );
    }
}
