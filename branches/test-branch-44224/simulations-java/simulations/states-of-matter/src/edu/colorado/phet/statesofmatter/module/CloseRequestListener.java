/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module;

/**
 * This interface allows the implementer to listen for events from a diagram,
 * chart, or whatever, that indicate that the user wants to close or hide the
 * the item.
 * 
 * @author John Blanco
 */
public interface CloseRequestListener {

	/**
	 * Indicates that a request has been received to close or hide this item.
	 * An example of when this might occur is when the user clicks a 'close'
	 * button (e.g. the 'x' in the corner of the window) and the window is
	 * not responsible for hiding itself.
	 */
	public void closeRequestReceived();
}
