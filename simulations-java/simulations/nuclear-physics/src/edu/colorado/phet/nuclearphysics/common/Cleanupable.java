/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common;

/**
 * This awkwardly named interface defines a method through which an object can
 * be commanded to clean up memory references that could potentially cause
 * memory leaks, such as when an inner class is registered as a listener with
 * some other object.
 * 
 * @author John Blanco
 *
 */
public interface Cleanupable {

	/**
	 * Clean up any lingering memory references and/or registrations that
	 * would be likely to cause memory leaks.
	 */
	public void cleanup();
}
