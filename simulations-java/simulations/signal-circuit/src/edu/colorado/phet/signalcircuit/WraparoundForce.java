package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.Force1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;
import edu.colorado.phet.signalcircuit.electron.wire1d.WirePatch;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireSystem;

public class WraparoundForce implements Force1d, SwitchListener {
    double k;
    double power;
    WireSystem sys;
    double minDist;
    double maxDist;
    WirePatch patch;
    boolean closed = false;

    public WraparoundForce( double k, double power, WireSystem sys, WirePatch wp ) {
        this.patch = wp;
        this.sys = sys;
        this.k = k;
        this.power = power;
        maxDist = Double.POSITIVE_INFINITY;
    }

    public void setSwitchClosed( boolean closed ) {
        this.closed = closed;
    }

    public void setMinDistance( double d ) {
        this.minDist = d;
    }

    public void setMaxDistance( double d ) {
        this.maxDist = d;
    }

    public double getPositionDifference( double target, double source, double dist ) {
        //return target-source;
        //Normal case.
        if( !closed ) {
            return target - source;
        }
        double normalDx = Math.abs( target - source );
        double leftDx = Math.abs( target - source + dist );
        double rightDx = Math.abs( target - source - dist );
        double min = Math.min( normalDx, leftDx );
        min = Math.min( rightDx, min );
        if( min == normalDx ) {
            return target - source;
        }
        else if( min == leftDx ) {
            return target - source + dist;
        }
        else {
            return target - source - dist;
        }
    }

    public double getForce( WireParticle wp ) {
        int x;
        //double wireLength=patch.getLength();
        double sum = 0;
        for( int i = 0; i < sys.numParticles(); i++ ) {
            WireParticle p = sys.particleAt( i );
            //o.O.d("i="+i+", p="+p+", wp="+wp);
            if( p != wp ) {
                //o.O.d("Differenct reference.");
                double dx = getPositionDifference( p.getPosition(), wp.getPosition(), patch.getLength() );
                double r = Math.abs( dx );
                if( r < minDist ) {
                    r = minDist;
                }
                //o.O.p("i="+i+", r="+r);
                double term = 0;
                if( r > maxDist && maxDist != 0 ) {
                    //o.O.p("Too far.");
                    x = 0; //dummy command for jikes.
                }
                else {
                    //o.O.p("Close enough..");
                    term = k * Math.pow( r, power );
                    if( dx > 0 ) {
                        term *= -1;
                    }
                    //o.O.d("particle["+i+"] r="+r+", term"+term+", sum="+sum);
                }
                sum += term;
                //o.O.d("term="+term);
            }
        }
        return sum;
    }
}
