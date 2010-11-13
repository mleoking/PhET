package edu.colorado.phet.fluidpressureandflow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class PipePosition {
    private final Property<ImmutableVector2D> top;
    private final Property<ImmutableVector2D> bottom;

    public PipePosition( double x, double yBottom, double yTop ) {
        top = new Property<ImmutableVector2D>( new ImmutableVector2D( x, yTop ) );
        bottom = new Property<ImmutableVector2D>( new ImmutableVector2D( x, yBottom ) );
    }

    public Point2D getTop() {
        return top.getValue().toPoint2D();
    }

    public Point2D getBottom() {
        return bottom.getValue().toPoint2D();
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        top.addObserver( simpleObserver );
        bottom.addObserver( simpleObserver );
    }

    public void translateTop( double dx, double dy ) {
        top.setValue( new ImmutableVector2D( getTop().getX() + dx, getTop().getY() + dy ) );
    }

    public void translateBottom( double dx, double dy ) {
        bottom.setValue( new ImmutableVector2D( getBottom().getX() + dx, getBottom().getY() + dy ) );
    }
}
