package edu.colorado.phet.physics;

import edu.colorado.phet.physics.body.PhysicalEntity;


public class PropagatorLaw implements Law {
    Force p;

    public PropagatorLaw( Force p ) {
        this.p = p;
    }

    public Force getPropagator() {
        return p;
    }

    public void apply( float  dt, PhysicalSystem sys ) {
        for( int i = 0; i < sys.numParticles(); i++ ) {
            p.act( sys.particleAt( i ) );
        }
    }

    public void apply( PhysicalEntity body1, PhysicalEntity body2 ) {
    }
}
