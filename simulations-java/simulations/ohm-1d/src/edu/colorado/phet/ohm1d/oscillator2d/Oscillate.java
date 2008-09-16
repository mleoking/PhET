package edu.colorado.phet.ohm1d.oscillator2d;

import edu.colorado.phet.ohm1d.common.phys2d.DoublePoint;
import edu.colorado.phet.ohm1d.common.phys2d.Particle;
import edu.colorado.phet.ohm1d.common.phys2d.Propagator;

public class Oscillate implements Propagator {
    DoublePoint x0;
    double amplitude;
    double freq;
    double amplitudeScale;
    DoublePoint axis;
    double t = 0;

    public Oscillate( DoublePoint x0, double amplitude, double freq, double amplitudeScale, DoublePoint axis ) {
        this.axis = axis.normalize();
        //System.err.println("Axis="+axis);
        if ( axis.getX() == 1 && axis.getY() == 1 ) {
            throw new RuntimeException();
        }
        //throw new RuntimeException();
        this.amplitude = amplitude;
        this.x0 = x0;
        this.freq = freq;
        this.amplitudeScale = amplitudeScale;
    }

    public void setAmplitude( double a ) {
        this.amplitude = a;
    }

    public void propagate( double dt, Particle p ) {
        t += dt;
        amplitude = amplitude * amplitudeScale;
        //System.err.println("ampli="+amplitude);
        //double xnew=x0+amplitude*Math.sin(t*freq);
        double scale = amplitude * Math.sin( t * freq );
        DoublePoint pos = x0.add( axis.multiply( scale ) );
        p.setPosition( pos );//wp.setPosition(xnew);
        //System.err.println("Axis="+axis);
    }

    public double getAmplitude() {
        return amplitude;
    }
}
