// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Motion strategy that moves towards a destination, but it wanders or meanders
 * a bit on the way to look less directed and, in some cases, more natural.
 *
 * @author John Blanco
 */
public class MeanderToDestinationMotionStrategy extends MotionStrategy {
    private final MotionStrategy randomWalkMotionStrategy;
    private final MotionStrategy directToDestinationMotionStrategy;

    // Destination property.  It is only in two dimensions because the
    // destination must always have a Z value of zero.
    private final Property<Point2D> destinationProperty;

    /**
     * Constructor.
     *
     * @param destinationProperty
     * @param motionBoundsProperty
     * @param destinationOffset
     */
    public MeanderToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty, ImmutableVector2D destinationOffset ) {
        randomWalkMotionStrategy = new RandomWalkMotionStrategy( motionBoundsProperty );
        directToDestinationMotionStrategy = new MoveDirectlyToDestinationMotionStrategy( destinationProperty, motionBoundsProperty, destinationOffset );
        this.destinationProperty = destinationProperty;
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        Point3D nextLocation3D = getNextLocation3D( new Point3D.Double( currentLocation.getX(), currentLocation.getY(), 0 ), shape, dt );
        return new Point2D.Double( nextLocation3D.getX(), nextLocation3D.getY() );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation, Shape shape, double dt ) {
        // If the destination in within the shape, go straight to it.
        if ( shape.getBounds2D().contains( destinationProperty.get() ) ) {
            // Move directly towards the destination with no randomness.
            return directToDestinationMotionStrategy.getNextLocation3D( currentLocation, shape, dt );
        }
        else {
            // Use a combination of the random and linear motion.
            Point3D intermediateLocation = randomWalkMotionStrategy.getNextLocation3D( currentLocation, shape, dt * 0.5 );
            return directToDestinationMotionStrategy.getNextLocation3D( intermediateLocation, shape, dt * 0.5 );
        }
    }
}