// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class MutableVector2D extends Property<ImmutableVector2D> {
    public MutableVector2D( double x, double y ) {
        this( new ImmutableVector2D( x, y ) );
    }

    public MutableVector2D( ImmutableVector2D value ) {
        super( value );
    }

    public ImmutableVector2D times( double scale ) {
        return get().getScaledInstance( scale );
    }
}
