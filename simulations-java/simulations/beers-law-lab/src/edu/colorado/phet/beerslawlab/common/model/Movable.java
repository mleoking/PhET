// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A movable model element.
 * Units are determined by the client.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Movable implements Resettable {

    public final Property<ImmutableVector2D> location;
    private final PBounds dragBounds;

    public Movable( ImmutableVector2D location, PBounds dragBounds ) {
        this.location = new Property<ImmutableVector2D>( location );
        this.dragBounds = dragBounds;
    }

    // Gets the bounds that define allowed locations. Null means there are no constraints on location.
    public PBounds getDragBounds() {
        if ( dragBounds == null ) {
            return null;
        }
        else {
            return new PBounds( dragBounds );
        }
    }

    public double getX() {
        return location.get().getX();
    }

    public double getY() {
        return location.get().getY();
    }

    public void reset() {
        location.reset();
    }
}
