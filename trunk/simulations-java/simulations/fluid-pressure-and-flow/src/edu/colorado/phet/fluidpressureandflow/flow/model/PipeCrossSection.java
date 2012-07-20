// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * The cross section of a pipe, representing a vertical slice and the top and bottom points,
 * which can be moved by the user.  This class supports movement in x and y directions, but the user interface
 * only permits movement in the vertical (y) direction.
 *
 * @author Sam Reid
 */
public class PipeCrossSection {

    //Top and bottom end points on the cross section
    public final Property<Vector2D> top;
    public final Property<Vector2D> bottom;
    public final IUserComponent component;

    public PipeCrossSection( IUserComponent component, double x, double yBottom, double yTop ) {
        this.component = component;
        top = new Property<Vector2D>( new Vector2D( x, yTop ) );
        bottom = new Property<Vector2D>( new Vector2D( x, yBottom ) );
    }

    public Point2D getTop() {
        return top.get().toPoint2D();
    }

    public Point2D getBottom() {
        return bottom.get().toPoint2D();
    }

    //Observe both the top and bottom position of the pipe cross section
    public void addObserver( SimpleObserver simpleObserver ) {
        top.addObserver( simpleObserver );
        bottom.addObserver( simpleObserver );
    }

    public void translateTop( double dx, double dy ) {
        top.set( new Vector2D( getTop().getX() + dx, getTop().getY() + dy ) );
    }

    public void translateBottom( double dx, double dy ) {
        bottom.set( new Vector2D( getBottom().getX() + dx, getBottom().getY() + dy ) );
    }

    public double getX() {
        return top.get().getX();
    }

    public double getHeight() {
        return getTop().getY() - getBottom().getY();
    }

    public void reset() {
        top.reset();
        bottom.reset();
    }

    public double getCenterY() {
        return ( top.get().getY() + bottom.get().getY() ) / 2;
    }

    //Translate both top and bottom parts of the pipe
    public void translate( double dx, double dy ) {
        translateTop( dx, dy );
        translateBottom( dx, dy );
    }
}