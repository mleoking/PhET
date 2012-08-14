// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * A motion strategy where the user moves in a general direction with some
 * random direction changes every once in a while.
 *
 * @author John Blanco
 */
public class WanderInGeneralDirectionMotionStrategy extends MotionStrategy {

    private static final double MIN_VELOCITY = 100; // In picometers/s
    private static final double MAX_VELOCITY = 500; // In picometers/s
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.25; // In seconds.
    private static final Random RAND = new Random();

    private final Vector2D generalDirection;
    private double directionChangeCountdown = 0;
    private Vector2D currentMotionVector = new Vector2D( 0, 0 );

    public WanderInGeneralDirectionMotionStrategy( Vector2D generalDirection, Property<MotionBounds> motionBoundsProperty ) {
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                WanderInGeneralDirectionMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
        this.generalDirection = generalDirection;
    }

    @Override public Vector2D getNextLocation( Vector2D currentLocation, Shape shape, double dt ) {
        directionChangeCountdown -= dt;
        if ( directionChangeCountdown <= 0 ) {
            // Time to change the direction.
            double newVelocity = MIN_VELOCITY + RAND.nextDouble() * ( MAX_VELOCITY - MIN_VELOCITY );
            double varianceAngle = ( RAND.nextDouble() - 0.5 ) * Math.PI / 3;
            currentMotionVector = generalDirection.getInstanceOfMagnitude( newVelocity ).getRotatedInstance( varianceAngle );
            // Reset the countdown timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // Make sure that current motion will not cause the model element to
        // move outside of the motion bounds.
        if ( !motionBounds.testIfInMotionBounds( shape, currentMotionVector, dt ) ) {
            // The current motion vector would take this element out of bounds,
            // so it needs to "bounce".
            currentMotionVector = getMotionVectorForBounce( shape, currentMotionVector, dt, MAX_VELOCITY );
            // Reset the timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        return currentLocation.plus( currentMotionVector.times( dt ) );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation, Shape shape, double dt ) {
        // The 3D version of this motion strategy doesn't move in the z
        // direction.  This may change some day.
        Vector2D nextLocation2D = getNextLocation( new Vector2D( currentLocation.getX(), currentLocation.getY() ), shape, dt );
        return new Point3D.Double( nextLocation2D.getX(), nextLocation2D.getY(), currentLocation.getZ() );
    }

    private double generateDirectionChangeCountdownValue() {
        return MIN_TIME_IN_ONE_DIRECTION + RAND.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
    }
}
