package phet.wire1d.forces;

import phet.wire1d.Force1d;
import phet.wire1d.WireParticle;
import phet.wire1d.WireSystem;

public class CoulombForce implements Force1d {
    WireSystem sys;
    CoulombForceParameters params;

    public CoulombForce( CoulombForceParameters params, WireSystem sys ) {
        this.sys = sys;
        this.params = params;
    }

    public double getForce( WireParticle wp ) {
        double sum = 0;
        for( int i = 0; i < sys.numParticles(); i++ ) {
            WireParticle p = sys.particleAt( i );
            //System.err.println("i="+i+", p="+p+", wp="+wp);
            if( p != wp ) {
                if( p.getWirePatch() == wp.getWirePatch() ) {
                    sum += params.getForce( p, wp );
                }
                else {
                    //System.err.println("Different patches.");
                }
            }
        }
        return sum;
    }
}
