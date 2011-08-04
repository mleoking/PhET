// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.UpdateStrategy.FREE_PARTICLE_SPEED;

/**
 * Pair of matching particles that could potentially form a crystal together, if they are close enough together.  Order within the pair is irrelevant.
 *
 * @author Sam Reid
 */
public class ParticlePair extends Pair<Particle, Particle> {
    public ParticlePair( Particle a, Particle b ) {
        super( a, b );
    }

    //Get the distance between the particles
    public double getDistance() {
        return _1.getPosition().getDistance( _2.getPosition() );
    }

    //Move the particles closer together at the free particle speed
    public void moveTogether( double dt ) {
        ImmutableVector2D unitVectorFromAToB = new ImmutableVector2D( _1.getPosition(), _2.getPosition() ).getNormalizedInstance();
        ImmutableVector2D velocity = unitVectorFromAToB.times( FREE_PARTICLE_SPEED );
        _1.velocity.set( velocity );
        _2.velocity.set( velocity.times( -1 ) );
        _1.stepInTime( ZERO, dt );
        _2.stepInTime( ZERO, dt );
    }
}