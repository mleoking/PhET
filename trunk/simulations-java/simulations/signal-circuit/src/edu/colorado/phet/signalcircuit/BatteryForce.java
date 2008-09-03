package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.Force1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireSystem;
import edu.colorado.phet.signalcircuit.phys2d.Law;
import edu.colorado.phet.signalcircuit.phys2d.System2D;

public class BatteryForce implements Force1d, Law {
    double f;
    double min;
    double max;
    WireSystem ws;
    double numOver;
    double maxOver;

    public BatteryForce( double f, double min, double max, WireSystem ws, double maxOver ) {
        this.maxOver = maxOver;
        this.f = f;
        this.min = min;
        this.max = max;
        this.ws = ws;
    }

    public void iterate( double dt, System2D sys ) {
        int x = 0;
        for( int i = 0; i < ws.numParticles(); i++ ) {
            if( ws.particleAt( i ).getPosition() > min + max / 2 - min / 2 ) {
                x++;
            }
        }
        this.numOver = x;
        //o.O.p("Num over="+numOver);
    }

    public double getForce( WireParticle wp ) {
        double diff = max - min;
        double width = diff / 2;
        double mid = min + width;
        double pos = wp.getPosition();
        if( pos < min || pos > max ) {
            return 0;
        }
        double fToUse = f;
        if( numOver >= maxOver ) {
            fToUse /= 3;
        }
        double x = Math.abs( pos - mid );
        double slope = -fToUse / width;
        double force = slope * x + fToUse;
        //o.O.p("pos="+pos+", width="+width+", x="+x+", force="+force);
        return force;
    }
}
