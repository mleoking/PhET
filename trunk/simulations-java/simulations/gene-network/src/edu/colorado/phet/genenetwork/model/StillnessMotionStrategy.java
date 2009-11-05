package edu.colorado.phet.genenetwork.model;

public class StillnessMotionStrategy extends AbstractMotionStrategy {

	public StillnessMotionStrategy(IModelElement modelElement) {
		super(modelElement);
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		// Does nothing, which means that this model element never moves.
	}
}
