// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
    private final Property<Point2D> destinationProperty;

    public MeanderToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty, ImmutableVector2D destinationOffset ) {
        randomWalkMotionStrategy = new RandomWalkMotionStrategy( motionBoundsProperty );
        directToDestinationMotionStrategy = new MoveDirectlyToDestinationMotionStrategy( destinationProperty, motionBoundsProperty, destinationOffset );
        this.destinationProperty = destinationProperty;
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        if ( shape.getBounds2D().contains( destinationProperty.get() ) ) {
            // Move directly towards the destination with no randomness.
            return directToDestinationMotionStrategy.getNextLocation( currentLocation, shape, dt );
        }
        else {
            // Use a combination of the random and linear motion.
            Point2D intermediateLocation = randomWalkMotionStrategy.getNextLocation( currentLocation, shape, dt * 0.5 );
            return directToDestinationMotionStrategy.getNextLocation( intermediateLocation, shape, dt * 0.5 );
        }
    }
}