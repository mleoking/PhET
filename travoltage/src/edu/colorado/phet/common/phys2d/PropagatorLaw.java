package edu.colorado.phet.common.phys2d;

public class PropagatorLaw implements Law {
    Propagator p;

    public PropagatorLaw( Propagator p ) {
        this.p = p;
    }

    public Propagator getPropagator() {
        return p;
    }

    public void iterate( double dt, System2D sys ) {
        for( int i = 0; i < sys.numParticles(); i++ ) {
            p.propagate( dt, sys.particleAt( i ) );
        }
    }
}
