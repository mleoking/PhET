package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Marker class to signify which compounds are crystals vs noncrystals.
 *
 * @author Sam Reid
 */
public class Crystal<T extends Particle> extends Compound<T> {
    public Crystal( ImmutableVector2D position, double spacing, Function2<Component, Double, T> componentMaker, Lattice<? extends Lattice> lattice ) {
        super( position, spacing, componentMaker, lattice );
    }
}