package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.electron.wire1d.Propagator1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;

import java.util.Vector;

public class RegionSelector implements Propagator1d {
    Vector regions = new Vector();
    Vector exregions = new Vector();

    public RegionSelector() {
    }

    public void addRegion( double start, double end, Propagator1d p ) {
        regions.add( new Region( start, end, p ) );
    }

    public void propagate( WireParticle wp, double dt ) {
        double x = wp.getPosition();
        for( int i = 0; i < exregions.size(); i++ ) {
            Region r = (Region)exregions.get( i );
            if( x >= r.start && x <= r.end ) {
                r.p.propagate( wp, dt );
                return;
            }
        }
        for( int i = 0; i < regions.size(); i++ ) {
            Region r = (Region)regions.get( i );
            if( x >= r.start && x <= r.end ) {
                r.p.propagate( wp, dt );
            }
        }
    }

    public static class Region {
        double start;
        double end;
        Propagator1d p;

        public Region( double start, double end, Propagator1d p ) {
            this.start = start;
            this.end = end;
            this.p = p;
        }
    }
}
