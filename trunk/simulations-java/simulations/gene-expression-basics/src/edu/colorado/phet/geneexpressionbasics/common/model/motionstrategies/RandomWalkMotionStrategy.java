// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This class defines a motion strategy that produces a random walk, meaning
 * that items using this strategy will move in one direction for a while, then
 * switch directions and move in another.
 *
 * @author John Blanco
 */
public class RandomWalkMotionStrategy extends MotionStrategy {

    private static final double MIN_XY_VELOCITY = 200; // In picometers/s
    private static final double MAX_XY_VELOCITY = 400; // In picometers/s
    private static final double MIN_Z_VELOCITY = 0.2; // In normalized units per sec
    private static final double MAX_Z_VELOCITY = 0.5; // In normalized units per sec
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.0; // In seconds.
    private static final Random RAND = new Random();

    private double directionChangeCountdown = 0;
    private ImmutableVector2D currentMotionVector2D = new Vector2D( 0, 0 );
    private double currentZVelocity = 0;

    public RandomWalkMotionStrategy( Property<MotionBounds> motionBoundsProperty ) {
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                RandomWalkMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        Point3D location3D = getNextLocation3D( new Point3D.Double( currentLocation.getX(), currentLocation.getY(), 0 ), shape, dt );
        return new Point2D.Double( location3D.getX(), location3D.getY() );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation, Shape shape, double dt ) {
        directionChangeCountdown -= dt;
        if ( directionChangeCountdown <= 0 ) {
            // Time to change direction.
            double newXYVelocity = MIN_XY_VELOCITY + RAND.nextDouble() * ( MAX_XY_VELOCITY - MIN_XY_VELOCITY );
            double newXYAngle = Math.PI * 2 * RAND.nextDouble();
            currentMotionVector2D = ImmutableVector2D.createPolar( newXYVelocity, newXYAngle );
            currentZVelocity = MIN_Z_VELOCITY + RAND.nextDouble() * ( MAX_Z_VELOCITY - MIN_Z_VELOCITY );
            currentZVelocity = RAND.nextBoolean() ? -currentZVelocity : currentZVelocity;
            // Reset the countdown timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // Make sure that current motion will not cause the model element to
        // move outside of the motion bounds.
        if ( !motionBounds.testIfInMotionBounds( shape, currentMotionVector2D, dt ) ) {
            // The current motion vector would take this element out of bounds,
            // so it needs to "bounce".
            currentMotionVector2D = getMotionVectorForBounce( shape, currentMotionVector2D, dt, MAX_XY_VELOCITY );
            // Reset the timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // To prevent odd-looking situations, the Z direction is limited so
        // that biomolecules don't appear transparent when on top of the DNA
        // molecule.
        double minZ = getMinZ( shape, new Point2D.Double( currentLocation.getX(), currentLocation.getY() ) );

        // Calculate the next location based on current motion.
        return new Point3D.Double( currentLocation.getX() + currentMotionVector2D.getX() * dt,
                                   currentLocation.getY() + currentMotionVector2D.getY() * dt,
                                   MathUtil.clamp( minZ, currentLocation.getZ() + currentZVelocity * dt, 0 ) );
    }

    private double generateDirectionChangeCountdownValue() {
        return MIN_TIME_IN_ONE_DIRECTION + RAND.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
    }
}
