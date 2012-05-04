// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.F;
import lombok.Data;

import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.fractionsintro.intro.model.pieset.AnimationTarget.animateToSlice;

/**
 * A single slice, used for pie cells or draggable pieces.
 *
 * @author Sam Reid
 */
@Data public class Slice {

    //Position of the slice.  Center for circles or squares.  For pie slices, it is the tip.  For a half-circle, it is the center of the line edge.
    public final Vector2D position;
    public final double angle;//in radians
    public final boolean dragging;
    public final AnimationTarget animationTarget;

    //Left as a function instead of a field so we don't eagerly compute it until necessary
    //This toShape function should use Lombok (or something else) to generate an equals() function for equality testing of Slices.
    //Equality should be correct for Slices because they are put in a map
    public final F<Slice, Shape> toShape;
    public final Color color;

    //Copy methods:
    public Slice withDragging( boolean dragging ) { return new Slice( position, angle, dragging, animationTarget, toShape, color ); }

    public Slice withAngle( double angle ) { return new Slice( position, angle, dragging, animationTarget, toShape, color ); }

    public Slice withPosition( Vector2D position ) { return new Slice( position, angle, dragging, animationTarget, toShape, color ); }

    public Slice withAnimationTarget( AnimationTarget animationTarget ) { return new Slice( position, angle, dragging, animationTarget, toShape, color ); }

    //Update methods

    public Slice translate( Vector2D delta ) { return translate( delta.x, delta.y ); }

    public Slice translate( double dx, double dy ) { return new Slice( position.plus( dx, dy ), angle, dragging, animationTarget, toShape, color ); }

    public Slice animationTarget( Slice target ) { return withAnimationTarget( animateToSlice( target ) );}

    public Vector2D getCenter() {return new Vector2D( getShape().getBounds2D().getCenterX(), getShape().getBounds2D().getCenterY() );}

    public Slice stepAnimation() { return stepTranslation().stepRotation(); }

    public Shape getShape() { return toShape.f( this ); }

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
        final double newAngle = s.angle + delta / 4.0;//This final term tuned so that pieces are rotated before they reach their destination

        return withAngle( newAngle );
    }

    private Vector2D getVelocity() {return animationTarget.position.minus( position ).getInstanceOfMagnitude( 30 );}

    private Slice checkFinishAnimation() {
        if ( position.distance( animationTarget.position ) < getVelocity().getMagnitude() ) {
            return withPosition( animationTarget.position ).withAnimationTarget( (AnimationTarget) null ).withAngle( animationTarget.angle );
        }
        return this;
    }

    public boolean movingToward( Slice cell ) { return animationTarget != null && animationTarget.position.equals( cell.position ) && animationTarget.angle == cell.angle; }

    public boolean positionAndAngleEquals( Slice cell ) { return cell.position.equals( position ) && cell.angle == angle; }

    public Slice moveTo( Slice target ) { return withDragging( false ).withAngle( target.angle ).withPosition( target.position );}

    //Determine which cell would be occupied based on the angle, used to rotate the cake slice images
    public int cell( int denominator ) {
        double anglePerSlice = Math.PI * 2 / denominator;
        long cell = Math.round( canonical( angle ) / anglePerSlice );
        final int intCell = (int) cell;
        final int result = intCell % denominator;

        //Unlike circular pies, cake is horizontal (vertical split) for d=2, so have to switch pieces
        if ( denominator == 2 ) {
            return ( result + 1 ) % 2;
        }
        return result;
    }

    //Make sure the angle lies between 0 and 2PI
    private double canonical( double angle ) {
        while ( angle < 0 ) { angle += Math.PI * 2; }
        while ( angle > Math.PI * 2 ) {angle -= Math.PI * 2;}
        return angle;
    }
}