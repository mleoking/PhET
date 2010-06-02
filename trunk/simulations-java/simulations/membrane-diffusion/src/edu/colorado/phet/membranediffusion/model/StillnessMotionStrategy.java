package edu.colorado.phet.membranediffusion.model;

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
}
