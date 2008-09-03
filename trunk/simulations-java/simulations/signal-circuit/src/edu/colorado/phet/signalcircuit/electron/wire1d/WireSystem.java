package edu.colorado.phet.signalcircuit.electron.wire1d;

import edu.colorado.phet.signalcircuit.phys2d.Law;
import edu.colorado.phet.signalcircuit.phys2d.System2D;

import java.util.Vector;

public class WireSystem implements Law {
    Vector particles;

    public WireSystem() {
        particles = new Vector();
    }

    public void add( WireParticle wp ) {
        particles.add( wp );
    }

    public void iterate( double dt, System2D sys ) {
        for( int i = 0; i < particles.size(); i++ ) {
            //o.O.p("i="+i);
            ( (WireParticle)particles.get( i ) ).propagate( dt );
        }
    }

    public int numParticles() {
        return particles.size();
    }

    public WireParticle particleAt( int i ) {
        return (WireParticle)particles.get( i );
    }
}
