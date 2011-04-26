// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Immutable Rectangle2D class, suitable for usage in Property<ImmutableRectangle2D> pattern, which also adds convenience methods missing from awt's Rectangle2D.
 *
 * @author Sam Reid
 */
public class ImmutableRectangle2D {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    //Create a rectangle with x=y=0
    public ImmutableRectangle2D( double width, double height ) {
        this( 0, 0, width, height );
    }

    public ImmutableRectangle2D( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Create a rectangle from the bounding box of the specified shape
    public ImmutableRectangle2D( Shape shape ) {
        this( shape.getBounds2D().getX(), shape.getBounds2D().getY(), shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight() );
    }

    public ImmutableVector2D getCenter() {
        return new ImmutableVector2D( x + width / 2, y + height / 2 );
    }

    public boolean contains( Point2D point2D ) {
        return new Rectangle2D.Double( x, y, width, height ).contains( point2D );
    }

    @Override public String toString() {
        return "x=" + x + ", y=" + y + ", width = " + width + ", height = " + height;
    }

    public Shape toShape() {
        return new Rectangle2D.Double( x, y, width, height );
    }

    //Find a point in the rectangle closest to the specified point.  Used for making sure a dragged object doesn't get outside the visible play area
    public Point2D getClosestPoint( Point2D point ) {
        Point2D.Double newPoint = new Point2D.Double( point.getX(), point.getY() );
        if ( newPoint.getX() < x ) { newPoint.x = x; }
        if ( newPoint.getX() > x + width ) { newPoint.x = x + width; }
        if ( newPoint.getY() < y ) { newPoint.y = y; }
        if ( newPoint.getY() > y + height ) { newPoint.y = y + height; }
        return newPoint;
    }
}
