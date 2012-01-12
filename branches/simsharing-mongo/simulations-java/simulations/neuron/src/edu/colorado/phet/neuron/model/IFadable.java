// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.model;


/**
 * Interface implemented by model elements that can fade in or out of
 * existence.  This was created primarily for use with fade strategies, but
 * may have other uses.
 * 
 * @author John Blanco
 */
public interface IFadable {

	/**
	 * Set the opaqueness value.
	 *  
	 * @param newPosition
	 */
	void setOpaqueness(double opaqueness);
	
	/**
	 * Get the opaqueness value.
	 *  
	 * @param newPosition
	 */
	double getOpaqueness();
	
	/**
	 * Set the fade strategy for the element.
	 */
	void setFadeStrategy(FadeStrategy fadeStrategy);
}
