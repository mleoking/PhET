// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.volt;

import edu.colorado.phet.batteryresistorcircuit.common.wire1d.Propagator1d;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireParticle;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireSystem;
import edu.colorado.phet.batteryresistorcircuit.gui.CoreCountListener;
import edu.colorado.phet.batteryresistorcircuit.gui.VoltageListener;

public class Batt implements Propagator1d, VoltageListener, CoreCountListener {
    WireRegion plus;
    WireRegion minus;
    WireSystem sys;
    double v;
    double desiredVolts;

    public void valueChanged( double val ) {
        //System.err.println("Set desired volts="+val);
        this.desiredVolts = val;
    }

    public Batt( WireRegion plus, WireRegion minus, WireSystem sys, double v, double desiredVolts ) {
        this.desiredVolts = desiredVolts;
        this.v = v;
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
        int left = countLeft();
        int right = countRight();
        double volts = right - left;
//  	if (count++%100==0)
//  	    System.err.println("volts="+volts+", right="+right+", isLeft="+isLeft);
        if ( volts < desiredVolts ) //go right.
        {
            wp.setVelocity( -v );
        }
        else if ( volts > desiredVolts ) //go isLeft.
        {
            wp.setVelocity( v );
        }
        else {
            return;
        }
        double x = wp.getPosition();
        double xnew = x + wp.getVelocity() * dt;
        wp.setPosition( xnew );
    }

    public void coreCountChanged( int val ) {
    }
}

