// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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

    //Check whether the specified point is within the defined region of this model bounds.  If the model bound is still not yet set, then the point is contained,
    //since all points should be legal before we have the bounds definition
    public boolean contains( ImmutableVector2D value ) {
        if ( getValue().isNone() ) {
            return true;//any point legal if no bounds defined yet
        }
        else {
            return getValue().get().contains( value.toPoint2D() );
        }
    }
}
