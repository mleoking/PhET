package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.ParticleContainer;
import edu.colorado.phet.common.phys2d.CompositePropagator;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;
import edu.colorado.phet.common.phys2d.laws.ForceLawPropagator;
import edu.colorado.phet.common.phys2d.propagators.PositionUpdate;
import edu.colorado.phet.common.phys2d.propagators.ResetAcceleration;
import edu.colorado.phet.common.phys2d.propagators.VelocityUpdate;

/*Normal bouncing within the Travolta.*/

//Maybe should implement ParticleContainer as well, to pass off to the coulomb law.

public class TravoltaPropagator implements Propagator, ParticleContainer//extends Composite, non?  Or just return (from static) a CompositePropagator
{
    CompositePropagator cp;
    ForceLawPropagator coulomb;

    public TravoltaPropagator( TravoltaImageBounce tib, ForceLawPropagator coulomb, double maxVel ) {
        this.coulomb = coulomb;
        this.cp = new CompositePropagator();
        cp.add( new ResetAcceleration() );
        //add wall bouncing.
        cp.add( tib );
        cp.add( coulomb );
        //add force laws.
        cp.add( new VelocityUpdate( maxVel ) );
        cp.add( new PositionUpdate() );
    }

    public int numParticles() {
        return coulomb.numParticles();
    }

    public Particle particleAt( int i ) {
        return coulomb.particleAt( i );
    }

    public void add( Particle p ) {
        coulomb.add( p );
    }

    public void remove( Particle p ) {
        coulomb.remove( p );
    }

    public void propagate( double dt, Particle p ) {
        //edu.colorado.phet.common.util.Debug.traceln("Propagating!");
        cp.propagate( dt, p );
    }
}
