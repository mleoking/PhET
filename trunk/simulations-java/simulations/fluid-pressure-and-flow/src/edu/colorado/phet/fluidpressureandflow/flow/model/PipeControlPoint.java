// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * A point that the user can drag to change the shape of the Pipe.
 *
 * @author Sam Reid
 */
public class PipeControlPoint {
    public final Property<Vector2D> point;
    public final IUserComponent component;

    public PipeControlPoint( final IUserComponent component, Property<Vector2D> point ) {
        this.component = component;
        this.point = point;
    }

    public Point2D getPoint() {
        return point.get().toPoint2D();
    }
}