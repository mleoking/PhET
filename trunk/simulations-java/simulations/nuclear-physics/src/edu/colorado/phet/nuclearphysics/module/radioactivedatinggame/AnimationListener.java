/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

/**
 * This interface is used to obtain notifications about an animated model
 * element.
 * 
 * @author John Blanco
 */
public interface AnimationListener {

	/**
	 * Handle a notification that the animation state of the corresponding
	 * model element has changed.  This may mean that it moved, or rotated, or
	 * that the the associated image changed, etc. 
	 */
	public void AnimationStateChanged();
}
