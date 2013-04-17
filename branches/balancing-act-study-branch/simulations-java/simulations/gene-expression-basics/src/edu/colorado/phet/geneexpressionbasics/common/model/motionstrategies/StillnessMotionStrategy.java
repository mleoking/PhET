// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * Motion strategy that has no motion, i.e. causes the user to be still.
 *
 * @author John Blanco
 */
public class StillnessMotionStrategy extends MotionStrategy {

    @Override public Vector2D getNextLocation( Vector2D currentLocation, Shape shape, double dt ) {
        return new Vector2D( currentLocation.getX(), currentLocation.getY() );
    }
}
