/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This interface is used to update the positions of the atoms that make up a
 * molecule.  This is often necessary since the simulations will tend to
 * operate on the molecular velocity, location, and rotation, and the
 * individual atoms must be moved as a result.
 * 
 * @author John Blanco
 *
 */
public interface AtomPositionUpdater {

	/**
	 * Update the positions of the atoms.
	 */
	public void updateAtomPositions();
}
