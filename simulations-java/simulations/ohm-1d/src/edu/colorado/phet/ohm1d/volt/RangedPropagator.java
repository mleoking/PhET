package edu.colorado.phet.ohm1d.volt;

import java.util.Vector;

import edu.colorado.phet.ohm1d.common.wire1d.Propagator1d;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;

public class RangedPropagator implements Propagator1d {
    private static class RangeProp {
        WireRegion wr;
        Propagator1d prop;

        public RangeProp( WireRegion wr, Propagator1d prop ) {
            this.wr = wr;
            this.prop = prop;
        }
    }

    Vector v = new Vector();
    Vector inv = new Vector();

    public void addInverse( WireRegion out, Propagator1d prop ) {
        inv.add( new RangeProp( out, prop ) );
    }

    public void addPropagator( WireRegion range, Propagator1d prop ) {
        v.add( new RangeProp( range, prop ) );
    }

    public void propagate( WireParticle wp, double dt ) {
        for ( int i = 0; i < v.size(); i++ ) {
            RangeProp pr = ( (RangeProp) v.get( i ) );
            if ( pr.wr.contains( wp ) ) {
                pr.prop.propagate( wp, dt );
            }
        }
        for ( int i = 0; i < inv.size(); i++ ) {
            RangeProp pr = ( (RangeProp) inv.get( i ) );
            if ( !pr.wr.contains( wp ) ) {
                pr.prop.propagate( wp, dt );
            }
        }
    }

}
