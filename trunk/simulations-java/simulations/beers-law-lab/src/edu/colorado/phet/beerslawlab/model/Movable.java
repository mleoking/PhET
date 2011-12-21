// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A movable model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Movable {

    public final Property<ImmutableVector2D> location;
    private final PBounds locationBounds;

    public Movable( ImmutableVector2D location, PBounds locationBounds ) {
        this.location = new Property<ImmutableVector2D>( location );
        this.locationBounds = locationBounds;
    }

    // Gets the bounds that define allowed locations. Null means there are no constraints on location.
    public PBounds getLocationBounds() {
        if ( locationBounds == null ) {
            return null;
        }
        else {
            return new PBounds( locationBounds );
        }
    }

    public void reset() {
        location.reset();
    }
}
