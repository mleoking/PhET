package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A crystal represents 0 or more (usually 1 or more) constituents which can be put into solution.  It is constructed from a lattice.
 *
 * @author Sam Reid
 */
public class Crystal extends Compound {
    public Crystal( ImmutableVector2D position ) {
        super( position );
    }
}