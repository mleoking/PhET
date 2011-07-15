package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Member in a lattice, including the particle and its relative offset
 *
 * @author Sam Reid
 */
public class LatticeConstituent {
    public final ImmutableVector2D location;
    public final Particle particle;

    public LatticeConstituent( Particle particle, ImmutableVector2D location ) {
        this.location = location;
        this.particle = particle;
    }
}