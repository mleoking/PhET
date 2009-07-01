/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

/**
 * This interface is used to obtain notifications about an animated model
 * element.
 * 
 * @author John Blanco
 */
public interface ModelAnimationListener {

	public void positionChanged();
	public void rotationalAngleChanged();
	public void sizeChanged();
	public void imageChanged();
}
