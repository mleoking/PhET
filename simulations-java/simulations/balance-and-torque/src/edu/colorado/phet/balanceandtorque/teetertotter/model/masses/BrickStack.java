// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Class that represents a stack of bricks in the model.  Note that a single
 * brick is represented as a stack of size 1.
 *
 * @author John Blanco
 */
public class BrickStack extends ShapeMass {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double BRICK_WIDTH = 0.2; // In meters.
    private static final double BRICK_HEIGHT = BRICK_WIDTH / 3;
    public static final double BRICK_MASS = 5; // In kg.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private int numBricks = 1;
    private Point2D position = new Point2D.Double( 0, 0 );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BrickStack( int numBricks, Point2D initialCenterBottom ) {
        super( numBricks * BRICK_MASS, generateShape( numBricks, initialCenterBottom, 0, 1 ), initialCenterBottom );
        position.setLocation( initialCenterBottom );
        this.numBricks = numBricks;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    // Generate the shape for this object.  This is static so that it can be
    // used in the constructor.
    private static Shape generateShape( int numBricks, Point2D centerBottom, double rotationAngle, double scale ) {
        Point2D brickOrigin = new Point2D.Double( 0, 0 );
        // Create a path that represents a stack of bricks.
        DoubleGeneralPath brickPath = new DoubleGeneralPath();
        for ( int i = 0; i < numBricks; i++ ) {
            // Draw an individual brick.
            brickPath.moveTo( brickOrigin.getX(), brickOrigin.getY() );
            brickPath.lineTo( brickOrigin.getX() + BRICK_WIDTH / 2, brickOrigin.getY() );
            brickPath.lineTo( brickOrigin.getX() + BRICK_WIDTH / 2, brickOrigin.getY() + BRICK_HEIGHT );
            brickPath.lineTo( brickOrigin.getX() - BRICK_WIDTH / 2, brickOrigin.getY() + BRICK_HEIGHT );
            brickPath.lineTo( brickOrigin.getX() - BRICK_WIDTH / 2, brickOrigin.getY() );
            brickPath.lineTo( brickOrigin.getX(), brickOrigin.getY() );
            // Move the origin to the next brick.
            brickOrigin.setLocation( brickOrigin.getX(), brickOrigin.getY() + BRICK_HEIGHT );
        }
        Shape rotatedShape = AffineTransform.getRotateInstance( rotationAngle ).createTransformedShape( brickPath.getGeneralPath() );
        Shape scaledShape = AffineTransform.getScaleInstance( scale, scale ).createTransformedShape( rotatedShape );
        Shape translatedShape = AffineTransform.getTranslateInstance( centerBottom.getX(), centerBottom.getY() ).createTransformedShape( scaledShape );
        return translatedShape;
    }

    public void setPosition( double x, double y ) {
        position.setLocation( x, y );
        updateShape();
    }

    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }

    @Override public Point2D getPosition() {
        return new Point2D.Double( position.getX(), position.getY() );
    }

    @Override public void translate( double x, double y ) {
        setPosition( position.getX() + x, position.getY() + y );
    }

    @Override public void translate( ImmutableVector2D delta ) {
        translate( delta.getX(), delta.getY() );
    }

    @Override public void setRotationAngle( double angle ) {
        super.setRotationAngle( angle );
        updateShape();
    }

    @Override public void initiateAnimation() {
        // Calculate velocity.  A higher velocity is used if the model element
        // has a long way to travel, otherwise it takes too long.
        double velocity = Math.max( getPosition().distance( animationDestination ) / MAX_REMOVAL_ANIMATION_DURATION, MIN_ANIMATION_VELOCITY );
        expectedAnimationTime = getPosition().distance( animationDestination ) / velocity; // In seconds.
        // Calculate the animation motion vector.
        animationMotionVector.setComponents( velocity, 0 );
        double animationAngle = Math.atan2( animationDestination.getY() - getPosition().getY(), animationDestination.getX() - getPosition().getX() );
        animationMotionVector.rotate( animationAngle );
        // Calculate the scaling factor such that the object is about 10% of
        // its usual size when it reaches the destination.
        // Update the property that tracks the animation state.
        animatingProperty.set( true );
    }

    @Override public void stepInTime( double dt ) {
        if ( animatingProperty.get() ) {
            // Do a step of the linear animation towards the destination.
            translate( animationMotionVector.getScaledInstance( dt ) );
            scale = Math.max( scale - ( dt / expectedAnimationTime ) * 0.9, 0.1 );
            if ( getPosition().distance( animationDestination ) < animationMotionVector.getMagnitude() * dt ) {
                // Close enough - animation is complete.
                setPosition( animationDestination );
                animatingProperty.set( false );
                scale = 1;
            }
        }
    }

    private void updateShape() {
        shapeProperty.set( generateShape( numBricks, position, rotationalAngleProperty.get(), scale ) );
    }
}
