// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional2;

import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class Circle2 {
    public final Vector2D position;
    public final double radius;
    public final boolean dragging;

    public Circle2 withDragging( final boolean dragging ) {
        return new Circle2( position, radius, dragging );
    }

    public Circle2 withPosition( final Vector2D position ) {
        return new Circle2( position, radius, dragging );
    }
}