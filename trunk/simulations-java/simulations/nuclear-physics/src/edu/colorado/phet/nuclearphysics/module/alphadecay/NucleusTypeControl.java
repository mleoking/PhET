package edu.colorado.phet.nuclearphysics.module.alphadecay;

import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;

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
	 * @param nucleusId - must be one of the values defined in this interface
	 * definition file.
	 */
	public void setNucleusType( int nucleusId );

	/**
	 * Get the type of nucleus.
	 * 
	 * @return A value representing the current nucleus type. 
	 */
	public int getNucleusType();

	/**
	 * Register to be informed of changes to the nucleus type (as well as
	 * other nucleus events).
	 */
	public void addListener(NuclearDecayModelListener listener);
}
