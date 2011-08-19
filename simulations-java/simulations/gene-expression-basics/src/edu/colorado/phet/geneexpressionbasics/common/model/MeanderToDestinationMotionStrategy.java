// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author John Blanco
 */
public class MeanderToDestinationMotionStrategy implements IMotionStrategy {

    private static final double DIRECTED_PROPORTION = 0.90; // Proportion of motion updates that move towards
    protected static final double MIN_DIRECTED_VELOCITY = 100;  // In picometers per second
    protected static final double MAX_DIRECTED_VELOCITY = 500;  // In picometers per second
    protected static final double MIN_WANDERING_VELOCITY = 100;  // In picometers per second
    protected static final double MAX_WANDERING_VELOCITY = 500;  // In picometers per second

    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.25; // In seconds.

    // Radius within which the item always moves toward the destination.
    protected static double DIRECT_MOVEMENT_RANGE = 40; // In picometers.

    private final Random rand = new Random();
    private double directionChangeCountdown = 0;
    private ImmutableVector2D currentMotionVector = new Vector2D( 0, 0 );

    // Destination to which this motion strategy moves.  Note that it is
    // potentially a moving targer.
    private final Property<Point2D> locationProperty;

    public MeanderToDestinationMotionStrategy( Property<Point2D> locationProperty ) {
        this.locationProperty = locationProperty;
    }

    public Point2D getNextLocation( double dt, Point2D currentLocation ) {
        directionChangeCountdown -= dt;
        double distanceToDestination = currentLocation.distance( locationProperty.get() );
        double newVelocity, newAngle;
        if ( directionChangeCountdown <= 0 ) {
            // Change the direction.
            if ( rand.nextDouble() < DIRECTED_PROPORTION || distanceToDestination < DIRECT_MOVEMENT_RANGE ) {
                // Move towards the destination.
                newVelocity = MIN_DIRECTED_VELOCITY + rand.nextDouble() * ( MAX_DIRECTED_VELOCITY - MIN_DIRECTED_VELOCITY );
                if ( newVelocity * dt > distanceToDestination ) {
                    // Scale the velocity to make sure that we don't overshoot
                    // the destination.
                    newVelocity = distanceToDestination / dt;
                }
                newAngle = Math.atan2( locationProperty.get().getY() - currentLocation.getY(),
                                       locationProperty.get().getX() - currentLocation.getX() );
            }
            else {
                // Move randomly.
                newVelocity = MIN_WANDERING_VELOCITY + rand.nextDouble() * ( MAX_WANDERING_VELOCITY - MIN_WANDERING_VELOCITY );
                newAngle = Math.PI * 2 * rand.nextDouble();
            }
            currentMotionVector = ImmutableVector2D.parseAngleAndMagnitude( newVelocity, newAngle );
            // Reset the countdown timer.
            directionChangeCountdown = MIN_TIME_IN_ONE_DIRECTION + rand.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
        }
        Point2D nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                   currentLocation.getY() + currentMotionVector.getY() * dt );
        // TODO: Need to check next location against bounds and "bounce" once we have bounds.
        return nextLocation;
    }
}
