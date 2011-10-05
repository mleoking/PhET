// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

//REVIEW nothing specific to pipes here. Should this be part of Pipe in model?

/**
 * A point that the user can drag to change the shape of the Pipe.
 *
 * @author Sam Reid
 */
public class PipeControlPoint {
    public final Property<ImmutableVector2D> point;

    public PipeControlPoint( Property<ImmutableVector2D> point ) {
        this.point = point;
    }

    public Point2D getPoint() {
        return point.get().toPoint2D();
    }
}