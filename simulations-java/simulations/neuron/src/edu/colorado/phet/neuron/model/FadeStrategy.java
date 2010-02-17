/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

/**
 * Base class for fade strategies that can be used to fade model elements in
 * and out.
 * 
 * @author John Blanco
 */
public abstract class FadeStrategy {

	/**
	 * Fade the associated model element according to the specified amount of
	 * time and the nature of the strategy.
	 * 
	 * @param dt
	 */
	public abstract void updateOpaqueness(IFadable moveableModelElement, double dt);
}
