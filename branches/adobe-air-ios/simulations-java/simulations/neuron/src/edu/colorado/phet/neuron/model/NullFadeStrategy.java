// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

/**
 * Fade strategy that does nothing.  Useful for avoiding having to check for
 * null values of fade strategy all the time.
 * 
 * @author John Blanco
 */
public class NullFadeStrategy extends FadeStrategy {

	@Override
	public void updateOpaqueness(IFadable fadableModelElement, double dt) {
		// Does nothing.
	}
}
