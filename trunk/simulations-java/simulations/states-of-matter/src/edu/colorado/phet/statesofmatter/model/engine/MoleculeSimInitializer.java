/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

/**
 * This interface is used to initialize a molecular interaction simulation,
 * meaning that the various data that describes the locations, forces, and
 * motion of the simulation are all created and set to initial values.
 * 
 * @author John
 *
 */
public interface MoleculeSimInitializer {

	/**
	 * Initialize the simulation, meaning that the data representing the
	 * positions, motion, and forces are created and set to initial values.
	 */
	public void initializeMoleculeSimulation();
}
