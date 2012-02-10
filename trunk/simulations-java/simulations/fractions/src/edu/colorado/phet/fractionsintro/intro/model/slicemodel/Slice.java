// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import lombok.Data;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
@Data public class Slice {
    public final ImmutableVector2D tip;
    public final double angle;//in radians
    public final double extent;//in radians
    public final double radius;
    public final boolean dragging;

    public Slice translate( ImmutableVector2D delta ) { return translate( delta.getX(), delta.getY() ); }

    public Slice translate( double dx, double dy ) { return new Slice( tip.plus( dx, dy ), angle, extent, radius, dragging ); }

    public Slice dragging( boolean dragging ) { return new Slice( tip, angle, extent, radius, dragging ); }

    public Slice angle( double angle ) { return new Slice( tip, angle, extent, radius, dragging ); }

    public Slice tip( ImmutableVector2D tip ) { return new Slice( tip, angle, extent, radius, dragging ); }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public Shape shape() {
        double epsilon = 1E-6;
        return extent >= Math.PI * 2 - epsilon ?
               new Ellipse2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2 ) :
               new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
    }

    public ImmutableVector2D center() {return new ImmutableVector2D( shape().getBounds2D().getCenterX(), shape().getBounds2D().getCenterY() );}
}