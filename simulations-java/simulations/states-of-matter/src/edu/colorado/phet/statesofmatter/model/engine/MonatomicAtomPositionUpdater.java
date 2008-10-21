/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This class updates the positions of atoms in a monatomic data set (i.e.
 * where each molecule is just a single atom).
 * 
 * @author John
 *
 */
public class MonatomicAtomPositionUpdater implements AtomPositionUpdater {

	public void updateAtomPositions( MoleculeForceAndMotionDataSet moleculeDataSet ) {
		
		// Make sure this is not being used on an inappropriate data set.
		assert moleculeDataSet.getAtomsPerMolecule() == 1;
		
		// Get direct references to the data in the data set.
		Point2D [] atomPositions = moleculeDataSet.getAtomPositions();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		
		// Position the atoms to match the position of the molecules.
		for (int i = 0; i < moleculeDataSet.getNumberOfMolecules(); i++){
			atomPositions[i].setLocation(moleculeCenterOfMassPositions[i]);
		}
	}
}
