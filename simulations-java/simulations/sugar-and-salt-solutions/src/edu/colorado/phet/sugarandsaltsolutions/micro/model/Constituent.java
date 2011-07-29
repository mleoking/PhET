package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Member in a compound, including the particle and its relative offset
 *
 * @author Sam Reid
 */
public class Constituent<T extends Particle> {

    //Relative location within the compound
    public final ImmutableVector2D relativePosition;

    //Particle within the compound
    public final T particle;

    public Constituent( T particle, ImmutableVector2D relativePosition ) {
        this.relativePosition = relativePosition;
        this.particle = particle;
    }
}