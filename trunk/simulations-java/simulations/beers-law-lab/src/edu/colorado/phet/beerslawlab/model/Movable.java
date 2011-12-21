// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A movable model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Movable {

    public final Property<ImmutableVector2D> location;

    public Movable() {
        this( new ImmutableVector2D() );
    }

    public Movable( ImmutableVector2D location ) {
        this.location = new Property<ImmutableVector2D>( location );
    }
}
