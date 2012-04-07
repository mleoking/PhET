// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional.model;

import lombok.Data;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class Circle {
    public final Vector2D position;
    public final double radius;
    public final boolean dragging;//True if the user is dragging the circle

    public Shape toShape() {
        return new Ellipse2D.Double( position.x - radius, position.y - radius, radius * 2, radius * 2 );
    }

    public Circle withDragging( final boolean dragging ) {
        return new Circle( position, radius, dragging );
    }

    public Circle translate( final Dimension2D delta ) {
        return withPosition( position.plus( delta ) );
    }

    private Circle withPosition( final Vector2D position ) {
        return new Circle( position, radius, dragging );
    }
}