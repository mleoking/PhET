// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

/**
 * @author Sam Reid
 */
public class Mass {
    public final Shape shape;
    public final boolean dragging;

    public Mass( final Shape shape, final boolean dragging ) {
        this.shape = shape;
        this.dragging = dragging;
    }

    public Mass withDragging( final boolean b ) { return new Mass( shape, b ); }

    public Mass translate( Dimension2D dim ) {
        return new Mass( AffineTransform.getTranslateInstance( dim.getWidth(), dim.getHeight() ).createTransformedShape( shape ), dragging );
    }
}