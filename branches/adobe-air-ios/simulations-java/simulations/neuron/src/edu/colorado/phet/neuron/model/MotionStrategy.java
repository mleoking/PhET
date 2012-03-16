// Copyright 2002-2011, University of Colorado

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
	 * time and the nature of the motion strategy.  The fadable interface is
	 * also passed in, since it is possible for the motion stratagy to update
	 * the fade strategy.
	 * 
	 * @param moveableModelElement
	 * @param fadableModelElement
	 * @param dt
	 */
	public abstract void move(IMovable moveableModelElement, IFadable fadableModelElement, double dt);
}
