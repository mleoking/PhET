// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * A motion strategy that moves directly to the specified destination.
 *
 * @author John Blanco
 */
public class MoveDirectlyToDestinationMotionStrategy extends MotionStrategy {

    protected static final double DEFAULT_VELOCITY = 1500;  // In picometers per second

    private final Vector2D velocityVector = new Vector2D( 0, 0 );

    // Destination to which this motion strategy moves.  Note that it is
    // potentially a moving target.
    private Point2D destination;

    // Scalar velocity with which the controlled item travels.
    private double scalarVelocity;

    /**
     * Constructor.
     *
     * @param destinationProperty
     * @param motionBoundsProperty
     */
    public MoveDirectlyToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty ) {
        this( destinationProperty, motionBoundsProperty, DEFAULT_VELOCITY );
    }

    public MoveDirectlyToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty, double velocity ) {
        this.scalarVelocity = velocity;
        destinationProperty.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D destination ) {
                MoveDirectlyToDestinationMotionStrategy.this.destination = destination;
            }
        } );
        motionBoundsProperty.addObserver( new VoidFunction1<MotionBounds>() {
            public void apply( MotionBounds motionBounds ) {
                MoveDirectlyToDestinationMotionStrategy.this.motionBounds = motionBounds;
            }
        } );
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {

        updateVelocityVector( currentLocation, destination, scalarVelocity );

        // Make sure that current motion will not cause the model element to
        // move outside of the motion bounds.
        if ( !motionBounds.testMotionAgainstBounds( shape, velocityVector, dt ) ) {
            // The current motion vector would take this element out of bounds,
            // so it needs to "bounce".
            velocityVector.setValue( getMotionVectorForBounce( shape, velocityVector, dt, scalarVelocity ) );
        }

        // Make sure that the current motion won't move the model element past
        // the destination.
        double distanceToDestination = currentLocation.distance( destination );
        if ( velocityVector.getMagnitude() * dt > distanceToDestination ) {
            velocityVector.scale( distanceToDestination / velocityVector.getMagnitude() );
        }

        // Calculate the next location based on the motion vector.
        return new Point2D.Double( currentLocation.getX() + velocityVector.getX() * dt,
                                   currentLocation.getY() + velocityVector.getY() * dt );
    }

    private void updateVelocityVector( Point2D currentLocation, Point2D destination, double velocity ) {
        velocityVector.setValue( new ImmutableVector2D( currentLocation, destination ).getInstanceOfMagnitude( velocity ) );
    }
}
