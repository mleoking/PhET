// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

/**
 * Motion strategy that does not do any motion, i.e. just leaves the model
 * element in the same location.
 * 
 * @author John Blanco
 */
public class StillnessMotionStrategy extends MotionStrategy {

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		// Does nothing, since the object is not moving.
	}
}
