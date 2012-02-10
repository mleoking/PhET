// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * This class is solely to keep track of the container that the slice is in, if any.
 * I'm not sure that this is the best design pattern, maybe we should just keep a map of key->value.
 *
 * @author Sam Reid
 */
public class MovableSlice {
    public final Slice slice;
    public final Slice container;//Null if not in a container
    public final boolean dragging;
    public final ImmutableVector2D center;
    public final double angle;
    public final Shape shape;

    public MovableSlice( Slice slice, Slice container ) {
        this.container = container;
        this.slice = slice;
        this.dragging = slice.dragging;
        this.center = slice.center;
        this.angle = slice.angle;
        this.shape = slice.shape;
    }

    public MovableSlice angle( double v ) {
        return new MovableSlice( slice.angle( v ), container );
    }

    public MovableSlice translate( ImmutableVector2D minus ) {
        return new MovableSlice( slice.translate( minus ), container );
    }

    public MovableSlice tip( ImmutableVector2D tip ) {
        return new MovableSlice( slice.tip( tip ), container );
    }

    public MovableSlice dragging( boolean b ) {
        return new MovableSlice( slice.dragging( b ), container );
    }

    public MovableSlice translate( double width, double height ) {
        return new MovableSlice( slice.translate( width, height ), container );
    }

    public MovableSlice container( Slice closest ) {
        return new MovableSlice( slice, closest );
    }
}