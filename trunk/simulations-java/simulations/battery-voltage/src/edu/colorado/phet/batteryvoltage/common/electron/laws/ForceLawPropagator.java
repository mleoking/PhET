package edu.colorado.phet.batteryvoltage.common.electron.laws;

import edu.colorado.phet.batteryvoltage.common.electron.core.ParticleList;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;

public class ForceLawPropagator extends ParticleList implements Propagator {
    ForceLaw law;

    public ForceLawPropagator( ForceLaw law ) {
        this( new Particle[0], law );
    }

    public ForceLawPropagator( Particle[] ch, ForceLaw law ) {
        addAll( ch );
        this.law = law;
    }

    public DoublePoint getForce( double dt, Particle p ) {
        DoublePoint force = new DoublePoint();
        //util.Debug.traceln("Num sources="+sources.size());
        for ( int j = 0; j < numParticles(); j++ ) {
            Particle x = particleAt( j );
            if ( x == p ) {
            }//ignore
            else {
                force = force.add( law.getForce( x, p ) );
            }
        }
        return force;
    }

    /**
     * Apply the force law to particle p, ignoring the self-force (by reference).
     */
    public void propagate( double dt, Particle p ) {
        //util.Debug.traceln(force);
        //f=ma
        DoublePoint force = getForce( dt, p );
        double mass = p.getMass();
        DoublePoint fOverM = force.multiply( 1.0 / mass );
        DoublePoint oldAcc = p.getAcceleration();
        DoublePoint newAcc = oldAcc.add( fOverM );
        //util.Debug.traceln("oldacc="+oldAcc+", newacc="+newAcc);
        p.setAcceleration( newAcc );
    }
}
