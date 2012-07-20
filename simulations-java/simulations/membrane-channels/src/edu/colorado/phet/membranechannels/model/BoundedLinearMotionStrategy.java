// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.membranechannels.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * A simple motion strategy for moving in a straight line and bouncing if
 * bounds are encountered.
 *
 * @author John Blanco
 */
public class BoundedLinearMotionStrategy extends MotionStrategy {

    private MutableVector2D velocity; // In nanometers per second of simulation time.
    private Rectangle2D motionBounds = new Rectangle2D.Double( Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                                                               Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );

    public BoundedLinearMotionStrategy( MutableVector2D velocity ) {
        this.velocity = velocity;
    }

    public BoundedLinearMotionStrategy( MutableVector2D velocity, Rectangle2D motionBounds ) {
        this.velocity = velocity;
        this.motionBounds.setFrame( motionBounds );
    }

    @Override
    public void move( IMovable movableModelElement, double dt ) {
        Point2D currentLocation = movableModelElement.getPositionReference();
        double radius = movableModelElement.getRadius();

        // Bounce back toward the inside if we are outside of the motion bounds.
        if ( ( currentLocation.getX() + radius > motionBounds.getMaxX() && velocity.getX() > 0 ) ||
             ( currentLocation.getX() - radius < motionBounds.getMinX() && velocity.getX() < 0 ) ) {
            // Reverse direction in the X direction.
            velocity.setComponents( -velocity.getX(), velocity.getY() );
        }
        if ( ( currentLocation.getY() + radius > motionBounds.getMaxY() && velocity.getY() > 0 ) ||
             ( currentLocation.getY() - radius < motionBounds.getMinY() && velocity.getY() < 0 ) ) {
            // Reverse direction in the Y direction.
            velocity.setComponents( velocity.getX(), -velocity.getY() );
        }

        movableModelElement.setPosition( currentLocation.getX() + velocity.getX() * dt,
                                         currentLocation.getY() + velocity.getY() * dt );
    }

    @Override
    public MutableVector2D getInstantaneousVelocity() {
        return new MutableVector2D( velocity.getX(), velocity.getY() );
    }
}
