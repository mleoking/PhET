// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * The cross section of a pipe, representing a vertical slice and the top and bottom points,
 * which can be moved by the user.  This class supports movement in x and y directions, but the user interface
 * only permits movement in the vertical (y) direction.
 *
 * @author Sam Reid
 */
public class CrossSection {
    public final Property<ImmutableVector2D> top;
    public final Property<ImmutableVector2D> bottom;

    public CrossSection( double x, double yBottom, double yTop ) {
        top = new Property<ImmutableVector2D>( new ImmutableVector2D( x, yTop ) );
        bottom = new Property<ImmutableVector2D>( new ImmutableVector2D( x, yBottom ) );
    }

    public Point2D getTop() {
        return top.getValue().toPoint2D();
    }

    public Point2D getBottom() {
        return bottom.getValue().toPoint2D();
    }

    //Observe both the top and bottom position of the pipe cross section
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

    public double getX() {
        return top.getValue().getX();
    }

    public double getHeight() {
        return getTop().getY() - getBottom().getY();
    }

    public void reset() {
        top.reset();
        bottom.reset();
    }

    public double getCenterY() {
        return ( top.getValue().getY() + bottom.getValue().getY() ) / 2;
    }
}
