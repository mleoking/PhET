package edu.colorado.phet.membranediffusion.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

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
    public Vector2DInterface getInstantaneousVelocity() {
        return new Vector2D(0, 0);
    }
}
