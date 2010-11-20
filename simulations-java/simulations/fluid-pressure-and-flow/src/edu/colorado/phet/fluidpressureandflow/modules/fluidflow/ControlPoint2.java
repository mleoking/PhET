package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

import com.jme.math.Vector2f;

/**
* @author Sam Reid
*/
public class ControlPoint2 {
    Property<ImmutableVector2D> point;
    boolean isTop;

    public ControlPoint2( Property<ImmutableVector2D> point, boolean top ) {
        this.point = point;
        isTop = top;
    }

    public Point2D getPoint() {
        return point.getValue().toPoint2D();
    }
}
