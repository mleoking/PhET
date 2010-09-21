/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This interface is used to update the positions of the atoms that make up a
 * molecule based on the data that represents the molecule's position.  This
 * is often necessary since the simulations will tend to operate on the
 * molecular velocity, location, and rotation, and the individual atoms must
 * be moved as a result.
 * 
 * @author John Blanco
 *
 */
public interface AtomPositionUpdater {

	/**
	 * Update the positions of the atoms.  It is assumed that the implementer
	 * already has references to the needed data.
	 */
	public void updateAtomPositions( MoleculeForceAndMotionDataSet moleculeDataSet );
}
