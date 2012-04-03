// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;

/**
 * Immutable model object for a single mass.
 *
 * @author Sam Reid
 */
public class Mass {
    public final Shape shape;
    public final boolean dragging;
    public final double velocity;
    public final double mass;

    public Mass( final Shape shape, final boolean dragging, final double velocity, final double mass ) {
        this.shape = shape;
        this.dragging = dragging;
        this.velocity = velocity;
        this.mass = mass;
    }

    public Mass withDragging( final boolean b ) { return new Mass( shape, b, velocity, mass ); }

    public Mass translate( Dimension2D dim ) {
        return new Mass( AffineTransform.getTranslateInstance( dim.getWidth(), dim.getHeight() ).createTransformedShape( shape ), dragging, velocity, mass );
    }

    public double getMinY() {
        return shape.getBounds2D().getMinY();
    }

    public Mass withMinY( final double minY ) {
        return translate( new Dimension2DDouble( 0, minY - getMinY() ) );
    }

    public Mass withVelocity( final double newVelocity ) {
        return new Mass( shape, dragging, newVelocity, mass );
    }

    public Mass withCenterX( final double centerX ) {
        return translate( new Dimension2DDouble( centerX - shape.getBounds2D().getCenterX(), 0 ) );
    }

    public double getMaxY() {
        return shape.getBounds2D().getMaxY();
    }
}