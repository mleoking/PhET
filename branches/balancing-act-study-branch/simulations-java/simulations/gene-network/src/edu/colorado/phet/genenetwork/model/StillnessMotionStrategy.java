// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

public class StillnessMotionStrategy extends AbstractMotionStrategy {

	public StillnessMotionStrategy() {
		super();
	}

	@Override
	public void updatePositionAndMotion(double dt, SimpleModelElement modelElement) {
		// Does nothing, which means that this model element never moves.
	}
}
