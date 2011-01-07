// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class ControlPoint {
    public final Property<ImmutableVector2D> point;
    public final boolean isTop;

    public ControlPoint( Property<ImmutableVector2D> point, boolean top ) {
        this.point = point;
        isTop = top;
    }

    public Point2D getPoint() {
        return point.getValue().toPoint2D();
    }
}
