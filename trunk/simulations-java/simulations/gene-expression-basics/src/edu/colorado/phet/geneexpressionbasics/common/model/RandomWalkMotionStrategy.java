// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * @author John Blanco
 */
public class RandomWalkMotionStrategy implements IMotionStrategy {

    private static final double MIN_VELOCITY = 100; // In picometers/s
    private static final double MAX_VELOCITY = 500; // In picometers/s
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.25; // In seconds.
    private static final Random RAND = new Random();

    private final Rectangle2D motionBounds = new Rectangle2D.Double();
    private double directionChangeCountdown = 0;
    private ImmutableVector2D currentMotionVector = new Vector2D( 0, 0 );

    public RandomWalkMotionStrategy( Rectangle2D motionBounds ) {
        this.motionBounds.setRect( motionBounds );
    }

    public RandomWalkMotionStrategy() {
        // TODO: Temp, awaiting the InfiniteRectangle class.
        this.motionBounds.setRect( -100000, -100000, 100000, 100000 );
    }

    public Point2D getNextLocation( double dt, Point2D currentLocation ) {
        directionChangeCountdown -= dt;
        if ( directionChangeCountdown <= 0 ) {
            // Time to change direction.
            double newVelocity = MIN_VELOCITY + RAND.nextDouble() * ( MAX_VELOCITY - MIN_VELOCITY );
            double newAngle = Math.PI * 2 * RAND.nextDouble();
            currentMotionVector = ImmutableVector2D.parseAngleAndMagnitude( newVelocity, newAngle );
            // Reset the countdown timer.
            directionChangeCountdown = MIN_TIME_IN_ONE_DIRECTION + RAND.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
        }
        Point2D nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                   currentLocation.getY() + currentMotionVector.getY() * dt );

        // Cause the item to "bounce" if it is outside the motion bounds.
        if ( !motionBounds.contains( nextLocation ) ) {
            if ( nextLocation.getX() > motionBounds.getMaxX() ) {
                currentMotionVector = new ImmutableVector2D( -Math.abs( currentMotionVector.getX() ), currentMotionVector.getY() );
            }
            else if ( nextLocation.getX() < motionBounds.getMinX() ) {
                currentMotionVector = new ImmutableVector2D( Math.abs( currentMotionVector.getX() ), currentMotionVector.getY() );
            }
            if ( nextLocation.getY() > motionBounds.getMaxY() ) {
                currentMotionVector = new ImmutableVector2D( currentMotionVector.getX(), -Math.abs( currentMotionVector.getY() ) );
            }
            else if ( nextLocation.getY() < motionBounds.getMinY() ) {
                currentMotionVector = new ImmutableVector2D( currentMotionVector.getX(), Math.abs( currentMotionVector.getY() ) );
            }
            nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                               currentLocation.getY() + currentMotionVector.getY() * dt );
        }

        return nextLocation;
    }
}
