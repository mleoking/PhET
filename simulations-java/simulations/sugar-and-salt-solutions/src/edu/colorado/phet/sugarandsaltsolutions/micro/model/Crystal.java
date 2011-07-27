package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Marker class to signify which compounds are crystals vs noncrystals.
 *
 * @author Sam Reid
 */
public class Crystal<T extends Particle> extends Compound<T> {
    public Crystal( ImmutableVector2D position ) {
        super( position );
    }
}