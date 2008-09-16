package edu.colorado.phet.ohm1d.volt;

import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.common.wire1d.WireSystem;

public class SmoothBatt extends Batt {
    WireRegion plus;
    WireRegion minus;
    WireSystem sys;
    double desiredVolts;

    //    DoubleSeries ds;
    public void valueChanged( double val ) {
        //System.err.println("Set desired volts="+val);
        this.desiredVolts = val;
    }

    public void coreCountChanged( int val ) {
    }

    public SmoothBatt( WireRegion plus, WireRegion minus, WireSystem sys, double v, double desiredVolts, int numPoints ) {
        super( plus, minus, sys, v, desiredVolts );
//        this.ds = new DoubleSeries( numPoints );
        this.desiredVolts = desiredVolts;
        this.plus = plus;
        this.minus = minus;
        this.sys = sys;
    }

    public int countLeft() {
        int sum = 0;
        for ( int i = 0; i < sys.numParticles(); i++ ) {
            if ( plus.contains( sys.particleAt( i ) ) ) {
                sum++;
            }
        }
        return sum;
    }

    public int countRight() {
        int sum = 0;
        for ( int i = 0; i < sys.numParticles(); i++ ) {
            if ( minus.contains( sys.particleAt( i ) ) ) {
                sum++;
            }
        }
        return sum;
    }

    public void propagate( WireParticle wp, double dt ) {
        double speed = getSpeed();
        //System.err.println("Got speed="+speed);
        //abs+=1.2;
        //We want this to be faster at lower speeds.
        //double speed = -10 * Math.pow(Math.abs(desiredVolts), 1.7) / numCores * sign;;
        wp.setVelocity( speed );
        double x = wp.getPosition();
        double xnew = x + wp.getVelocity() * dt;
        wp.setPosition( xnew );
    }

    public double getSpeed() {
        //System.err.println("Getting speed for des="+desiredVolts);
        double sign = 1;
        if ( desiredVolts < 0 ) {
            sign = -1;
        }
        double abs = Math.abs( desiredVolts );
        if ( abs <= .1 ) {
            return 0;
        }
        else if ( abs <= .3 ) {
            return -4 * sign;
        }
        else if ( abs <= .5 ) {
            return -6 * sign;
        }
        else if ( abs <= .7 ) {
            return -8 * sign;
        }
        else if ( abs <= .9 ) {
            return -10 * sign;
        }
        else if ( abs <= 1.1 ) {
            return -12 * sign;
        }
        else if ( abs < 1.3 ) {
            return -14 * sign;
        }
        else if ( abs < 1.7 ) {
            return -16 * sign;
        }
        else if ( abs < 1.9 ) {
            return -18 * sign;
        }
        else if ( abs < 3.1 ) {
            return -20 * sign;
        }
        else if ( abs < 5.1 ) {
            return -22 * sign;
        }
        else if ( abs < 7.1 ) {
            return -24 * sign;
        }
        else if ( abs < 9.1 ) {
            return -26 * sign;
        }
        else {
            return -28 * sign;
        }
        //double speed = -20 * Math.pow(abs, 1.5) / numCores * sign;
        //return speed;

    }
}

