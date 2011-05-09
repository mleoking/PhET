// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.membranechannels.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that does not do any motion, i.e. just leaves the model
 * element in the same location.
 * 
 * @author John Blanco
 */
public class StillnessMotionStrategy extends MotionStrategy {

	@Override
	public void move(IMovable movableModelElement, double dt) {
		// Does nothing, since the object is not moving.
	}
	
   @Override
    public Vector2D getInstantaneousVelocity() {
        return new Vector2D(0, 0);
    }
}
