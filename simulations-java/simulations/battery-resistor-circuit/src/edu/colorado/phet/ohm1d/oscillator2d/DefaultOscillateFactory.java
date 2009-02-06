package edu.colorado.phet.ohm1d.oscillator2d;

import java.util.Random;

import edu.colorado.phet.ohm1d.common.phys2d.DoublePoint;

public class DefaultOscillateFactory implements OscillateFactory {
    Random random;
    double vToAScale;
    double decay;
    double freq;
    double aMax;
    DoublePoint axis;

    public DefaultOscillateFactory( Random random, double vToAScale, double decay, double freq, double aMax, DoublePoint axis ) {
        this.random = random;
        this.axis = axis;
        this.freq = freq;
        this.vToAScale = vToAScale;
        this.decay = decay;
        this.aMax = aMax;
    }

    public Oscillate newOscillate( double v, Core c ) {
        //Create a random axis of oscillation.
        double xVal = random.nextDouble() * 3 + .5;
        if ( random.nextBoolean() ) {
            xVal = -xVal;
        }
        axis = new DoublePoint( 1, xVal );
        double x = Math.abs( v * vToAScale );
        double amp = 0;
        if ( x < aMax ) {
            amp = v * vToAScale;
        }
        else if ( v < 0 ) {
            amp = -aMax;
        }
        else {
            amp = aMax;
        }
        return new Oscillate( c.getOrigin(), amp, freq, decay, axis );
    }
}
