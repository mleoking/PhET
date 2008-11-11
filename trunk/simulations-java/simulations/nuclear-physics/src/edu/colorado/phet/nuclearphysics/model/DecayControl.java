/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

/**
 * Interface for controlling various aspects of a nucleus's decay behavior.
 * 
 * @author John Blanco
 */
public interface DecayControl {

	/**
	 * Activate the decay process for a nucleus.  In most cases, this will
	 * main that a timer is set and that once the timer has expired the decay
	 * will occur.
	 */
	public void activateDecay();

}
