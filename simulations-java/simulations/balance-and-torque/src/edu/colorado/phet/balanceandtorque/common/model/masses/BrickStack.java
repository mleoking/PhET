// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.balanceandtorque.common.model.ShapeMass;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
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
    public static final double BRICK_HEIGHT = BRICK_WIDTH / 3;
    public static final double BRICK_MASS = 5; // In kg.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private int numBricks = 1;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BrickStack( int numBricks ) {
        this( numBricks, new Point2D.Double( 0, 0 ) );
    }

    public BrickStack( int numBricks, Point2D initialCenterBottom ) {
        super( getUserComponent( numBricks ), numBricks * BRICK_MASS, generateShape( numBricks, 1 ) );
        setPosition( initialCenterBottom );
        this.numBricks = numBricks;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    // Generate the shape for this object.  This is static so that it can be
    // used in the constructor.
    private static Shape generateShape( int numBricks, double scale ) {
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
        return AffineTransform.getScaleInstance( scale, scale ).createTransformedShape( brickPath.getGeneralPath() );
    }

    /**
     * Get the appropriate sim-sharing user component based on the number of
     * bricks in the stack.
     *
     * @param numBricks
     * @return
     */
    private static IUserComponent getUserComponent( int numBricks ) {
        switch( numBricks ) {
            case 1:
                return BalanceAndTorqueSimSharing.UserComponents.singleBrick;
            case 2:
                return BalanceAndTorqueSimSharing.UserComponents.stackOfTwoBricks;
            case 3:
                return BalanceAndTorqueSimSharing.UserComponents.stackOfThreeBricks;
            case 4:
                return BalanceAndTorqueSimSharing.UserComponents.stackOfFourBricks;
            default:
                assert false; // If this line is reached then additional user components need to be added.
                return BalanceAndTorqueSimSharing.UserComponents.singleBrick;
        }
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
        // Update the property that tracks the animation state.
        animatingProperty.set( true );
    }

    @Override public void stepInTime( double dt ) {
        if ( animatingProperty.get() ) {
            // Do a step of the linear animation towards the destination.
            if ( getPosition().distance( animationDestination ) >= animationMotionVector.getMagnitude() * dt ) {
                // Perform next step of animation.
                translate( animationMotionVector.getScaledInstance( dt ) );
                animationScale = Math.max( animationScale - ( dt / expectedAnimationTime ) * 0.9, 0.1 );
            }
            else {
                // Close enough - animation is complete.
                setPosition( animationDestination );
                animatingProperty.set( false );
                animationScale = 1;
            }
        }
    }

    @Override public Mass createCopy() {
        return new BrickStack( numBricks, getPosition() );
    }
}
