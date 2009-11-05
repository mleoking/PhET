/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

/**
 * This is a motion strategy that defers to a leader, meaning that it never
 * decides to move on its own.
 * 
 * @author John Blanco
 */
public class DeferToLeaderMotionStrategy extends AbstractMotionStrategy {

	public DeferToLeaderMotionStrategy(IModelElement modelElement) {
		super(modelElement);
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		// Does nothing, because it is assumed that the model element is
		// monitoring the motion of the leader and following that.
	}
}
