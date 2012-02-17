// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * A single slice, used for pie cells or draggable pieces.
 *
 * @author Sam Reid
 */
@Data public class Slice {

    //TODO: Different internal representations for slices (square slices don't have angle)
    public final ImmutableVector2D tip;
    public final double angle;//in radians
    public final double radius;
    public final boolean dragging;
    public final AnimationTarget animationTarget;

    //Left as a function instead of a field so we don't eagerly compute it when not necessary
    public final Function1<Slice, Shape> toShape;

    public Slice translate( ImmutableVector2D delta ) { return translate( delta.getX(), delta.getY() ); }

    public Slice translate( double dx, double dy ) { return new Slice( tip.plus( dx, dy ), angle, radius, dragging, animationTarget, toShape ); }

    public Slice dragging( boolean dragging ) { return new Slice( tip, angle, radius, dragging, animationTarget, toShape ); }

    public Slice angle( double angle ) { return new Slice( tip, angle, radius, dragging, animationTarget, toShape ); }

    public Slice tip( ImmutableVector2D tip ) {
        if ( Double.isNaN( tip.getX() ) ) {
            throw new RuntimeException( "NANA" );
        }
        return new Slice( tip, angle, radius, dragging, animationTarget, toShape );
    }

    public Slice animationTarget( AnimationTarget animationTarget ) { return new Slice( tip, angle, radius, dragging, animationTarget, toShape ); }

    public ImmutableVector2D center() {return new ImmutableVector2D( shape().getBounds2D().getCenterX(), shape().getBounds2D().getCenterY() );}

    public Slice stepAnimation() { return stepTranslation().stepRotation(); }

    public Shape shape() { return toShape.apply( this ); }

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

    public boolean movingToward( Slice cell ) { return animationTarget != null && animationTarget.position.equals( cell.tip ) && animationTarget.angle == cell.angle; }

    public boolean positionAndAngleEquals( Slice cell ) { return cell.tip.equals( tip ) && cell.angle == angle; }

    public Slice moveTo( Slice target ) { return dragging( false ).angle( target.angle ).tip( target.tip );}
}