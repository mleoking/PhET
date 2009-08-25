/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import edu.colorado.phet.nuclearphysics.common.NucleusType;

/**
 * This interface is used, generally by a control panel, to set the type of
 * atomic nucleus being simulated.
 * 
 * @author John Blanco
 *
 */
public interface NucleusTypeControl {

	/**
	 * Set the type of nucleus.
	 * 
	 * @param nucleusType - One of the defined types of nuclei.
	 */
	public void setNucleusType( NucleusType nucleusType );

	/**
	 * Get the type of nucleus.
	 * 
	 * @return A value representing the current nucleus type. 
	 */
	public NucleusType getNucleusType();

	/**
	 * Register to be informed of changes to the nucleus type (as well as
	 * other nucleus events).
	 */
	public void addListener(NuclearDecayModelListener listener);

}
