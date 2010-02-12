package edu.colorado.phet.neuron.model;

/**
 * Motion strategy that does not do any motion, i.e. just leaves the model
 * element in the same location.
 * 
 * @author John Blanco
 */
public class StillnessMotionStrategy extends MotionStrategy {

	public StillnessMotionStrategy(IMovable movableModelElement) {
		super(movableModelElement);
	}

	@Override
	public void move(double dt) {
		// Does nothing, since the object is not moving.
	}
}
