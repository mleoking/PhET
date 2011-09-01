// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * @author John Blanco
 */
public class RandomWalkMotionStrategy extends MotionStrategy {

    private static final double MIN_VELOCITY = 100; // In picometers/s
    private static final double MAX_VELOCITY = 500; // In picometers/s
    private static final double MIN_TIME_IN_ONE_DIRECTION = 0.25; // In seconds.
    private static final double MAX_TIME_IN_ONE_DIRECTION = 1.25; // In seconds.
    private static final Random RAND = new Random();

    private double directionChangeCountdown = 0;
    private ImmutableVector2D currentMotionVector = new Vector2D( 0, 0 );

    public RandomWalkMotionStrategy( Property<MotionBounds> motionBoundsProperty ) {
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                RandomWalkMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
    }

    public Point2D getNextLocation( double dt, Point2D currentLocation ) {
        directionChangeCountdown -= dt;
        if ( directionChangeCountdown <= 0 ) {
            // Time to change direction.
            double newVelocity = MIN_VELOCITY + RAND.nextDouble() * ( MAX_VELOCITY - MIN_VELOCITY );
            double newAngle = Math.PI * 2 * RAND.nextDouble();
            currentMotionVector = ImmutableVector2D.parseAngleAndMagnitude( newVelocity, newAngle );
            // Reset the countdown timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // Calculate the next location based on the motion vector.
        Point2D nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                   currentLocation.getY() + currentMotionVector.getY() * dt );

        // Cause the item to "bounce" if it is outside the motion bounds.
        if ( !motionBounds.inBounds( nextLocation ) ) {
            currentMotionVector = getMotionVectorForBounce( currentLocation, dt );
            directionChangeCountdown = generateDirectionChangeCountdownValue();
            nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt, currentLocation.getY() + currentMotionVector.getY() * dt );
        }

        return nextLocation;
    }

    public Point2D getNextLocation( double dt, Point2D currentLocation, Shape shape ) {
        directionChangeCountdown -= dt;
        if ( directionChangeCountdown <= 0 ) {
            // Time to change direction.
            double newVelocity = MIN_VELOCITY + RAND.nextDouble() * ( MAX_VELOCITY - MIN_VELOCITY );
            double newAngle = Math.PI * 2 * RAND.nextDouble();
            currentMotionVector = ImmutableVector2D.parseAngleAndMagnitude( newVelocity, newAngle );
            // Reset the countdown timer.
            directionChangeCountdown = generateDirectionChangeCountdownValue();
        }

        // Calculate the next location based on the motion vector.
        Point2D nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                   currentLocation.getY() + currentMotionVector.getY() * dt );

        // Cause the item to "bounce" if it is outside the motion bounds.
        AffineTransform motionTransform = getMotionTransform( currentMotionVector, dt );

        if ( !motionBounds.inBounds( motionTransform.createTransformedShape( shape ) ) ) {
            currentMotionVector = getMotionVectorForBounce( shape, dt );
            directionChangeCountdown = generateDirectionChangeCountdownValue();
            nextLocation = new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt, currentLocation.getY() + currentMotionVector.getY() * dt );
        }

        return nextLocation;
    }

    private double generateDirectionChangeCountdownValue() {
        return MIN_TIME_IN_ONE_DIRECTION + RAND.nextDouble() * ( MAX_TIME_IN_ONE_DIRECTION - MIN_TIME_IN_ONE_DIRECTION );
    }

    private AffineTransform getMotionTransform( ImmutableVector2D motionVector, double dt ) {
        return AffineTransform.getTranslateInstance( motionVector.getX() * dt, motionVector.getY() * dt );
    }

    /**
     * Change the motion vector so that the next location will be in bounds.
     * This should only be called when the current location combined with the
     * current motion leads to an out-of-bounds location.  Visually, this will
     * cause the object using this motion strategy to "bounce".
     *
     * @param currentLocation
     * @return
     */
    private ImmutableVector2D getMotionVectorForBounce( Point2D currentLocation, double dt ) {
        // Check that this isn't being called inappropriately.
        assert ( !motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + currentMotionVector.getX() * dt,
                                                             currentLocation.getY() + currentMotionVector.getY() * dt ) ) );

        ImmutableVector2D reversedXMotionVector = new Vector2D( -currentMotionVector.getX(), currentMotionVector.getY() );
        if ( motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + reversedXMotionVector.getX() * dt,
                                                        currentLocation.getY() + reversedXMotionVector.getY() * dt ) ) ) {
            return reversedXMotionVector;
        }
        ImmutableVector2D reversedYMotionVector = new Vector2D( currentMotionVector.getX(), -currentMotionVector.getY() );
        if ( motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + reversedYMotionVector.getX() * dt,
                                                        currentLocation.getY() + reversedYMotionVector.getY() * dt ) ) ) {
            return reversedYMotionVector;
        }
        ImmutableVector2D reversedXYMotionVector = new Vector2D( -currentMotionVector.getX(), -currentMotionVector.getY() );
        if ( motionBounds.inBounds( new Point2D.Double( currentLocation.getX() + reversedXYMotionVector.getX() * dt,
                                                        currentLocation.getY() + reversedXYMotionVector.getY() * dt ) ) ) {
            return reversedXYMotionVector;
        }
        // If we reach this point, there is a problem.  Reversing the vector
        // in all the possible ways doesn't get us back to a valid location.
        // This might be because the user dropped the molecule in an invalid
        // location.  Set the motion to be directly back to the center of the
        // motion bounds for now.
        System.out.println( "Debug Warning: Biomolecule is unable to bounce back into motion bounds." );
        Point2D centerOfMotionBounds = new Point2D.Double( motionBounds.getBounds().getBounds2D().getCenterX(),
                                                           motionBounds.getBounds().getBounds2D().getCenterY() );
        Vector2D vectorToMotionBoundsCenter = new Vector2D( centerOfMotionBounds.getX() - currentLocation.getX(),
                                                            centerOfMotionBounds.getY() - currentLocation.getY() );
        vectorToMotionBoundsCenter.scale( MAX_VELOCITY / vectorToMotionBoundsCenter.getMagnitude() );
        return new ImmutableVector2D( vectorToMotionBoundsCenter );
    }

    private ImmutableVector2D getMotionVectorForBounce( Shape shape, double dt ) {
        // Check that this isn't being called inappropriately.
        AffineTransform currentMotionTransform = getMotionTransform( currentMotionVector, dt );
        assert ( !motionBounds.inBounds( currentMotionTransform.createTransformedShape( shape ) ) );

        // Try reversing X direction.
        ImmutableVector2D reversedXMotionVector = new Vector2D( -currentMotionVector.getX(), currentMotionVector.getY() );
        AffineTransform reverseXMotionTransform = getMotionTransform( reversedXMotionVector, dt );
        if ( motionBounds.inBounds( reverseXMotionTransform.createTransformedShape( shape ) ) ) {
            return reversedXMotionVector;
        }
        // Try reversing Y direction.
        ImmutableVector2D reversedYMotionVector = new Vector2D( currentMotionVector.getX(), -currentMotionVector.getY() );
        AffineTransform reverseYMotionTransform = getMotionTransform( reversedYMotionVector, dt );
        if ( motionBounds.inBounds( reverseYMotionTransform.createTransformedShape( shape ) ) ) {
            return reversedYMotionVector;
        }
        // Try reversing both X and Y directions.
        ImmutableVector2D reversedXYMotionVector = new Vector2D( -currentMotionVector.getX(), -currentMotionVector.getY() );
        AffineTransform reverseXYMotionTransform = getMotionTransform( reversedXYMotionVector, dt );
        if ( motionBounds.inBounds( reverseXYMotionTransform.createTransformedShape( shape ) ) ) {
            return reversedXYMotionVector;
        }
        // If we reach this point, there is a problem.  Reversing the vector
        // in all the possible ways doesn't get us back to a valid location.
        // This might be because the user dropped the molecule in an invalid
        // location.  Set the motion to be directly back to the center of the
        // motion bounds for now.
        System.out.println( "Debug Warning: Biomolecule is unable to bounce back into motion bounds." );
        Point2D centerOfMotionBounds = new Point2D.Double( motionBounds.getBounds().getBounds2D().getCenterX(),
                                                           motionBounds.getBounds().getBounds2D().getCenterY() );
        Vector2D vectorToMotionBoundsCenter = new Vector2D( centerOfMotionBounds.getX() - shape.getBounds2D().getCenterX(),
                                                            centerOfMotionBounds.getY() - shape.getBounds2D().getCenterY() );
        vectorToMotionBoundsCenter.scale( MAX_VELOCITY / vectorToMotionBoundsCenter.getMagnitude() );
        return new ImmutableVector2D( vectorToMotionBoundsCenter );
    }
}
