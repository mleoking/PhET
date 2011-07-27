package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Member in a compound, including the particle and its relative offset
 *
 * @author Sam Reid
 */
public class Constituent<T extends Particle> {
    public final ImmutableVector2D location;
    public final T particle;

    public Constituent( T particle, ImmutableVector2D location ) {
        this.location = location;
        this.particle = particle;
    }
}