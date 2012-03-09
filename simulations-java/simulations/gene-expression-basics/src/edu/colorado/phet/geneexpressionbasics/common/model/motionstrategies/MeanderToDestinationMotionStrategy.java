// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Motion strategy that moves towards a destination, but it wanders or meanders
 * a bit on the way to look less directed and, in some cases, more natural.
 *
 * @author John Blanco
 */
public class MeanderToDestinationMotionStrategy extends CompositeMotionStrategy {
    public MeanderToDestinationMotionStrategy( Property<Point2D> destinationProperty, Property<MotionBounds> motionBoundsProperty, ImmutableVector2D destinationOffset ) {
        super( new Entry( new RandomWalkMotionStrategy( motionBoundsProperty ), 0.5 ),

               //Do the "direct" motion last so the object will land in the right spot (since that is necessary to trigger state changes)
               new Entry( new MoveDirectlyToDestinationMotionStrategy( destinationProperty, motionBoundsProperty, destinationOffset ), 0.5 ) );
    }
}