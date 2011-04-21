// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Model coordinates of what is visible on the screen, or None if not yet set (has to be set by canvas after canvas is constructed).
 * This is a convenience subclass that adds a method or 2.
 *
 * @author Sam Reid
 */
public class ModelBounds extends Property<Option<ImmutableRectangle2D>> {
    public ModelBounds() {
        super( new Option.None<ImmutableRectangle2D>() );
    }

    public ModelBounds( Option<ImmutableRectangle2D> value ) {
        super( value );
    }

    public Point2D getClosestPoint( Point2D point ) {
        if ( getValue().isNone() ) {
            return point;
        }
        else {
            return getValue().get().getClosestPoint( point );
        }
    }
}
