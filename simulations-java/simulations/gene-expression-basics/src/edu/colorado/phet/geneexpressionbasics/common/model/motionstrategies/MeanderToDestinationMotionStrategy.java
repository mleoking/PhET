// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.math.vector.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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
    private final Property<Vector2D> destinationProperty;

    /**
     * Constructor.
     *
     * @param destinationProperty
     * @param motionBoundsProperty
     * @param destinationOffset
     */
    public MeanderToDestinationMotionStrategy( Property<Vector2D> destinationProperty, Property<MotionBounds> motionBoundsProperty, AbstractVector2D destinationOffset ) {
        randomWalkMotionStrategy = new RandomWalkMotionStrategy( motionBoundsProperty );
        directToDestinationMotionStrategy = new MoveDirectlyToDestinationMotionStrategy( destinationProperty, motionBoundsProperty, destinationOffset, 750 );
        this.destinationProperty = destinationProperty;
    }

    @Override public Vector2D getNextLocation( Vector2D currentLocation, Shape shape, double dt ) {
        Point3D nextLocation3D = getNextLocation3D( new Point3D.Double( currentLocation.getX(), currentLocation.getY(), 0 ), shape, dt );
        return new Vector2D( nextLocation3D.getX(), nextLocation3D.getY() );
    }

    @Override public Point3D getNextLocation3D( Point3D currentLocation, Shape shape, double dt ) {
        // If the destination in within the shape, go straight to it.
        if ( shape.getBounds2D().contains( destinationProperty.get().toPoint2D() ) ) {
            // Move directly towards the destination with no randomness.
            return directToDestinationMotionStrategy.getNextLocation3D( currentLocation, shape, dt );
        }
        else {
            // Use a combination of the random and linear motion.
            Point3D intermediateLocation = randomWalkMotionStrategy.getNextLocation3D( currentLocation, shape, dt * 0.6 );
            return directToDestinationMotionStrategy.getNextLocation3D( intermediateLocation, shape, dt * 0.4 );
        }
    }
}