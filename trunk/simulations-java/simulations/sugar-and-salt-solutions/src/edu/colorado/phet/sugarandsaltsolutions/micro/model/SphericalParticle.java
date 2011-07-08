package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * This particle represents a single indivisible spherical particle.
 *
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

    @Override public Shape getShape() {
        return new Ellipse2D.Double( position.get().getX() - radius, position.get().getY() - radius, radius * 2, radius * 2 );
    }
}