package phet.ohm1d.volt;

import phet.ohm1d.gui.VoltageListener;
import phet.wire1d.WireParticle;

import java.util.Random;

public class Accel implements phet.wire1d.Propagator1d, VoltageListener {
    double g;
    double vmax;
    double accelScale;

    public Accel( double g, double vmax, double accelScale ) {
        this.vmax = vmax;
        this.g = g;
        this.accelScale = accelScale;
    }

    public void valueChanged( double val ) {
        this.g = -val * accelScale;
    }

    public void propagate( WireParticle wp, double dt ) {
        //double rand=randy.nextDouble()-.5;
        double v = wp.getVelocity() + g * dt;

        wp.setVelocity( v );
        if( v < 0 && g > 0 )  //don't go backwards
        {
            wp.setVelocity( 0 );
        }
        else if( v > 0 && g < 0 ) {
            wp.setVelocity( 0 );
        }
        v = Math.min( v, vmax );
        double x = wp.getPosition();
        double xnew = x + wp.getVelocity() * dt;
        wp.setPosition( xnew );
    }
}

