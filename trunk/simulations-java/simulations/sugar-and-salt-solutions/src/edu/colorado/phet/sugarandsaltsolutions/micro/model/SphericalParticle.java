package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class SphericalParticle extends Particle {
    public final double radius;

    public SphericalParticle( double radius, ImmutableVector2D position ) {
        super( position );
        this.radius = radius;
    }
}