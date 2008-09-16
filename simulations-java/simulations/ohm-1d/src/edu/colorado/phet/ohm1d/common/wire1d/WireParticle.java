package edu.colorado.phet.ohm1d.common.wire1d;

import edu.colorado.phet.ohm1d.common.wire1d.propagators.NullPropagator;

/**
 * A WireParticle has a painter and propagator.
 */
public class WireParticle {
    double x;
    double v;
    double m;
    double q;
    Propagator1d p;
    WirePatch patch;

    public WireParticle() {
        this( new NullPropagator(), null );
    }

    public WireParticle( Propagator1d p, WirePatch patch ) {
        this.patch = patch;
        this.p = p;
        m = 1;
        q = -1;
    }

    public WirePatch getWirePatch() {
        return patch;
    }

    public void setWirePatch( WirePatch patch ) {
        this.patch = patch;
    }

    public void propagate( double dt ) {
        p.propagate( this, dt );
    }

    public double getPosition() {
        return x;
    }

    public double getVelocity() {
        return v;
    }

    public void setPosition( double x ) {
        this.x = x;
    }

    public void setVelocity( double v ) {
        this.v = v;
        //util.Debug.traceln("vel="+v);
    }

    public double getCharge() {
        return q;
    }

    public double getMass() {
        return m;
    }
}
