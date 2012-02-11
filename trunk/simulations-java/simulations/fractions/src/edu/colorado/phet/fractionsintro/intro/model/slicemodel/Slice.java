// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A single slice, used for pie cells or draggable pieces.
 *
 * @author Sam Reid
 */
@lombok.Data public class Slice {
    public final ImmutableVector2D tip;
    public final double angle;//in radians
    public final double extent;//in radians
    public final double radius;
    public final boolean dragging;
    public final AnimationTarget animationTarget;

    public Slice translate( ImmutableVector2D delta ) { return translate( delta.getX(), delta.getY() ); }

    public Slice translate( double dx, double dy ) { return new Slice( tip.plus( dx, dy ), angle, extent, radius, dragging, animationTarget ); }

    public Slice dragging( boolean dragging ) { return new Slice( tip, angle, extent, radius, dragging, animationTarget ); }

    public Slice angle( double angle ) { return new Slice( tip, angle, extent, radius, dragging, animationTarget ); }

    public Slice tip( ImmutableVector2D tip ) { return new Slice( tip, angle, extent, radius, dragging, animationTarget ); }

    public Slice animationTarget( AnimationTarget animationTarget ) { return new Slice( tip, angle, extent, radius, dragging, animationTarget ); }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public Shape shape() {
        double epsilon = 1E-6;
        return extent >= Math.PI * 2 - epsilon ?
               new Ellipse2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2 ) :
               new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
    }

    public ImmutableVector2D center() {return new ImmutableVector2D( shape().getBounds2D().getCenterX(), shape().getBounds2D().getCenterY() );}

    public Slice stepAnimation() {
        return stepTranslation().stepRotation();
    }

    private Slice stepTranslation() {
        if ( animationTarget == null ) { return this; }
        return translate( getVelocity() ).checkFinishAnimation();
    }

    private Slice stepRotation() {
        if ( animationTarget == null ) { return this; }
        return angle( 0.0 ).checkFinishRotation();
    }

    //TODO: implement
    private Slice checkFinishRotation() {
        return this;
    }

    private ImmutableVector2D getVelocity() {return animationTarget.position.minus( tip ).getInstanceOfMagnitude( 30 );}

    private Slice checkFinishAnimation() {
        if ( tip.distance( animationTarget.position ) < getVelocity().getMagnitude() ) {
            return tip( animationTarget.position ).animationTarget( null );
        }
        return this;
    }
}