package edu.colorado.phet.batteryvoltage.common.phys2d;

/**
 * Reads the Propagator from each PropagatingParticle in the system, and apply it to the particle.
 */
public class ParticleLaw implements Law {
    public void iterate( double dt, System2D sys ) {
        for ( int i = 0; i < sys.numParticles(); i++ ) {
            Particle p = sys.particleAt( i );
            if ( p instanceof PropagatingParticle ) {
                Propagator prop = ( (PropagatingParticle) p ).getPropagator();
                //util.Debug.traceln("Read propagator for particle["+i+"]: "+prop);
                prop.propagate( dt, p );
            }
        }
    }
}
