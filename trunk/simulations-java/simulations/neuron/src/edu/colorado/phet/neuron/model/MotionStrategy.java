/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

/**
 * Base class for motion strategies that can be used to set the type of motion
 * for elements within the model.
 * 
 * @author John Blanco
 */
public abstract class MotionStrategy {

	/**
	 * Move the associated model element according to the specified amount of
	 * time and the nature of the motion strategy.
	 * @param dt
	 */
	public abstract void move(IMovable moveableModelElement, double dt);
}
