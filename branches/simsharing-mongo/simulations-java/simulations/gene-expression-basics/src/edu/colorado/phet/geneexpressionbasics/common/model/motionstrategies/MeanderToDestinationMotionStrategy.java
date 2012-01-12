// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * @author John Blanco
 */
public class MeanderToDestinationMotionStrategy extends MotionStrategy {

    private static final double DIRECTED_PROPORTION = 0.90; // Proportion of motion updates that move towards
    protected static final double MIN_DIRECTED_VELOCITY = 100;  // In picometers per second
    protected static final double MAX_DIRECTED_VELOCITY = 500;  // In picometers per second
    protected static final double MIN_WANDERING_VELOCITY = 100;  // In picometers per second
    protected static final double MAX_WANDERING_VELOCITY = 500;  // In picometers per second

    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.25; // In seconds.

    // Radius within which the item always moves toward the destination.
    protected static double DIRECT_MOVEMENT_RANGE = 40; // In picometers.

    private static final Random RAND = new Random();

    private double directionChangeCountdown = 0;
    private ImmutableVector2D currentMotionVector = new Vector2D( 0, 0 );

    // Destination to which this motion strategy moves.  Note that it is
    // potentially a moving target.
    private Point2D destination;

    /**
     * Constructor.
     *
     * @param destinationProperty
     * @param motionBoundsProperty
     */
    public MeanderToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty ) {
        destinationProperty.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D destination ) {
                MeanderToDestinationMotionStrategy.this.destination = destination;
            }
        } );
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                MeanderToDestinationMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        directionChangeCountdown -= dt;
        double distanceToDestination = currentLocation.distance( destination );
        if ( directionChangeCountdown <= 0 ) {
            // Time to change the direction.
            double newVelocity, newAngle;
            if ( RAND.nextDouble() < DIRECTED_PROPORTION || distanceToDestination < DIRECT_MOVEMENT_RANGE ) {
                // Move towards the destination.
                newVelocity = MIN_DIRECTED_VELOCITY + RAND.nextDouble() * ( MAX_DIRECTED_VELOCITY - MIN_DIRECTED_VELOCITY );
                if ( newVelocity * dt > distanceToDestination ) {
                    // Scale the velocity to make sure that we don't overshoot
                    // the destination.
                    newVelocity = distanceToDestination / dt;
                }
                newAngle = Math.atan2( destination.getY() - currentLocation.getY(),
                                       destination.getX() - currentLocation.getX() );
            }
            else {
                // Move randomly.
                newVelocity = MIN_WANDERING_VELOCITY + RAND.nextDouble() * ( MAX_WANDERING_VELOCITY - MIN_WANDERING_VELOCITY );
                newAngle = Math.PI * 2 * RAND.nextDouble();
            }
            currentMotionVector = ImmutableVector2D.createPolar( newVelocity, newAngle );
            // Reset the countdown timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // Make sure that current motion will not cause the model element to
        // move outside of the motion bounds.
        if ( !motionBounds.testMotionAgainstBounds( shape, currentMotionVector, dt ) ) {
            // The current motion vector would take this element out of bounds,
            // so it needs to "bounce".
            currentMotionVector = getMotionVectorForBounce( shape, currentMotionVector, dt, MAX_DIRECTED_VELOCITY );
            // Reset the timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // Calculate the next location based on the motion vector.
        Point2D nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                   currentLocation.getY() + currentMotionVector.getY() * dt );
        return nextLocation;
    }

    private double generateDirectionChangeCountdownValue() {
        return MIN_TIME_IN_ONE_DIRECTION + RAND.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
    }
}
