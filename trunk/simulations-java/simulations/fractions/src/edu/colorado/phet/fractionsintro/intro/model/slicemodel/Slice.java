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

    public ImmutableVector2D center() {return new ImmutableVector2D( shape().getBounds2D().getCenterX(), shape().getBounds2D().getCenterY() );}

    public Slice stepAnimation() { return stepTranslation().stepRotation(); }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public Shape shape() {
        double epsilon = 1E-6;
        return extent >= Math.PI * 2 - epsilon ?
               new Ellipse2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2 ) :
               new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
    }

    private Slice stepTranslation() { return animationTarget == null ? this : translate( getVelocity() ).checkFinishAnimation();}

    private Slice stepRotation() { return animationTarget == null ? this : rotateTowardTarget( animationTarget.angle );}

    //Account for winding number and use the Zeno paradox to slow down
    public Slice rotateTowardTarget( double targetAngle ) {
        Slice s = this;
        //Account for winding number so it will go to the nearest angle without going around the long way
        if ( Math.abs( targetAngle - s.angle ) > Math.PI ) {
            if ( targetAngle > s.angle ) { targetAngle -= 2 * Math.PI; }
            else if ( targetAngle < s.angle ) { targetAngle += 2 * Math.PI; }
        }
        double delta = targetAngle - s.angle;
        final double newAngle = s.angle + delta / 6 * 30 * 1.0 / 30.0;

        return angle( newAngle );
    }

    private ImmutableVector2D getVelocity() {return animationTarget.position.minus( tip ).getInstanceOfMagnitude( 30 );}

    private Slice checkFinishAnimation() {
        if ( tip.distance( animationTarget.position ) < getVelocity().getMagnitude() ) {
            return tip( animationTarget.position ).animationTarget( null ).angle( animationTarget.angle );
        }
        return this;
    }

    public boolean movingToward( Slice cell ) {
        return animationTarget != null && animationTarget.position.equals( cell.tip ) && animationTarget.angle == cell.angle;
    }
}