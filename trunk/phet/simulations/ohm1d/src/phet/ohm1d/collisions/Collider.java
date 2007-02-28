package phet.ohm1d.collisions;

import phet.ohm1d.Electron;
import phet.ohm1d.oscillator2d.Core;
import phet.phys2d.Law;
import phet.phys2d.Particle;
import phet.phys2d.System2D;
import phet.wire1d.WirePatch;
import phet.wire1d.WireSystem;

public class Collider implements Law {
    WireSystem ws;
    CollisionEvent ce;
    WirePatch patch;

    public Collider( WireSystem ws, CollisionEvent ce, WirePatch patch ) {
        this.patch = patch;
        this.ws = ws;
        this.ce = ce;
    }

    public void iterate( double dt, System2D sys ) {
        for( int i = 0; i < sys.numParticles(); i++ ) {
            Particle wp = sys.particleAt( i );
            if( wp instanceof Core ) {
                Core c = (Core)wp;
                for( int k = 0; k < ws.numParticles(); k++ ) {
                    //System.err.println("Colliding.");
                    Electron other = (Electron)ws.particleAt( k );
                    if( other.getWirePatch() == patch ) {
                        ce.collide( c, other );
                    }
                }
            }
        }
    }
}
