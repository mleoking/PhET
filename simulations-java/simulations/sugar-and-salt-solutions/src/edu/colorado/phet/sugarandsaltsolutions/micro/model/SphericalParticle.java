package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class SphericalParticle extends Particle {
    public final double radius;
    public final Color color;

    public SphericalParticle( double radius, ImmutableVector2D position, Color color ) {
        super( position );
        this.radius = radius;
        this.color = color;
    }
}