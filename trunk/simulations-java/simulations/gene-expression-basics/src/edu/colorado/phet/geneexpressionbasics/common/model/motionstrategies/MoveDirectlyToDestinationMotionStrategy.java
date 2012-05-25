// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * A motion strategy that moves directly to the specified destination.
 *
 * @author John Blanco
 */
public class MoveDirectlyToDestinationMotionStrategy extends MotionStrategy {

    protected static final double DEFAULT_VELOCITY_2D = 500;  // Default velocity on the x-y plain, in picometers per second
    private static final double DEFAULT_Z_VELOCITY = 0.3; // Default velocity in the z direction in normalized units.
    private static final double MAX_Z_VELOCITY = 10; // Max Z velocity in normalized units.

    private final Vector2D velocityVector2D = new Vector2D( 0, 0 );

    // Destination to which this motion strategy moves.  Note that it is
    // potentially a moving target.
    private final Property<Point2D> destinationProperty;

    // Fixed offset from the destination location property used when computing
    // the actual target destination.  This is useful in cases where something
    // needs to move such that some point that is not its center is positioned
    // at the destination.
    private final ImmutableVector2D offsetFromDestinationProperty;

    // Scalar velocity with which the controlled item travels.
    private final double scalarVelocity2D;

    public MoveDirectlyToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty, ImmutableVector2D destinationOffset, double velocity ) {
        this.destinationProperty = destinationProperty;
        this.scalarVelocity2D = velocity;
        this.offsetFromDestinationProperty = destinationOffset;
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                MoveDirectlyToDestinationMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        Point3D nextLocation3D = getNextLocation3D( new Point3D.Double( currentLocation.getX(), currentLocation.getY(), 0 ), shape, dt );
        return new Point2D.Double( nextLocation3D.getX(), nextLocation3D.getY() );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation3D, Shape shape, double dt ) {
        // Destination is assumed to always have a Z value of 0, i.e. at the
        // "surface".
        Point3D currentDestination3D = new Point3D.Double( destinationProperty.get().getX() - offsetFromDestinationProperty.getX(),
                                                           destinationProperty.get().getY() - offsetFromDestinationProperty.getY(),
                                                           0 );
        Point2D currentDestination2D = new Point2D.Double( destinationProperty.get().getX() - offsetFromDestinationProperty.getX(),
                                                           destinationProperty.get().getY() - offsetFromDestinationProperty.getY() );
        Point2D currentLocation2D = new Point2D.Double( currentLocation3D.getX(), currentLocation3D.getY() );
        updateVelocityVector2D( currentLocation2D,
                                new Point2D.Double( currentDestination3D.getX(), currentDestination3D.getY() ),
                                scalarVelocity2D );

        // Make the Z velocity such that the front (i.e. z = 0) will be reached
        // at the same time as the destination in XY space.
        double distanceToDestination2D = currentLocation2D.distance( destinationProperty.get() );
        double zVelocity;
        if ( distanceToDestination2D > 0 ) {
            zVelocity = Math.min( Math.abs( currentLocation3D.getZ() ) / ( currentLocation2D.distance( destinationProperty.get() ) / scalarVelocity2D ), MAX_Z_VELOCITY );
        }
        else {
            zVelocity = MAX_Z_VELOCITY;
        }

        // Make sure that current motion will not cause the model element to
        // move outside of the motion bounds.
        if ( motionBounds.inBounds( shape ) && !motionBounds.testIfInMotionBounds( shape, velocityVector2D, dt ) ) {
            // Not sure what to do in this case, where the destination causes
            // some portion of the shape to go out of bounds.  For now, just
            // issue a warning an allow it to happen.
            System.out.println( getClass().getName() + " - Warning: Destination is causing some portion of shape to move out of bounds." );
        }

        // Make sure that the current motion won't move the model element past
        // the destination.
        double distanceToDestination = currentLocation2D.distance( currentDestination2D );
        if ( velocityVector2D.getMagnitude() * dt > distanceToDestination ) {
            return currentDestination3D;
        }

        // Calculate the next location based on the motion vector.
        return new Point3D.Double( currentLocation3D.getX() + velocityVector2D.getX() * dt,
                                   currentLocation3D.getY() + velocityVector2D.getY() * dt,
                                   MathUtil.clamp( -1, currentLocation3D.getZ() + zVelocity * dt, 0 ) );
    }

    private void updateVelocityVector2D( Point2D currentLocation, Point2D destination, double velocity ) {
        if ( currentLocation.distance( destination ) == 0 ) {
            velocityVector2D.setComponents( 0, 0 );
        }
        else {
            velocityVector2D.setValue( new ImmutableVector2D( currentLocation, destination ).getInstanceOfMagnitude( velocity ) );
        }
    }
}
