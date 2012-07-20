// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.workenergy.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class MutableVector2D extends Property<Vector2D> {
    public MutableVector2D( double x, double y ) {
        this( new Vector2D( x, y ) );
    }

    public MutableVector2D( Vector2D value ) {
        super( value );
    }

    public Vector2D times( double scale ) {
        return get().times( scale );
    }
}
