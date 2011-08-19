// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * @author John Blanco
 */
public class WanderInGeneralDirectionMotionStrategy implements IMotionStrategy {

    private static final double MIN_VELOCITY = 100; // In picometers/s
    private static final double MAX_VELOCITY = 500; // In picometers/s
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.25; // In seconds.

    private final ImmutableVector2D generalDirection;
    private final Random rand = new Random();
    private double directionChangeCountdown = 0;
    private ImmutableVector2D currentMotionVector = new Vector2D( 0, 0 );

    public WanderInGeneralDirectionMotionStrategy( ImmutableVector2D generalDirection ) {
        this.generalDirection = generalDirection;
    }

    public Point2D getNextLocation( double dt, Point2D currentLocation ) {
        directionChangeCountdown -= dt;
        if ( directionChangeCountdown <= 0 ) {
            // Time to change direction.
            double newVelocity = MIN_VELOCITY + rand.nextDouble() * ( MAX_VELOCITY - MIN_VELOCITY );
            double varianceAngle = ( rand.nextDouble() - 0.5 ) * Math.PI / 3;
            currentMotionVector = generalDirection.getInstanceOfMagnitude( newVelocity ).getRotatedInstance( varianceAngle );
            // Reset the countdown timer.
            directionChangeCountdown = MIN_TIME_IN_ONE_DIRECTION + rand.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
        }
        Point2D nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                   currentLocation.getY() + currentMotionVector.getY() * dt );
        // TODO: Need to check next location against bounds and "bounce" once we have bounds.
        return nextLocation;
    }
}
