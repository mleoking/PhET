/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This is an abstract base class for classes that implement the Verlet
 * algorithm for simulating molecular interactions based on the Lennard-
 * Jones potential.
 *  
 * @author John Blanco
 *
 */
public abstract class AbstractVerletAlgorithm implements MoleculeForceAndMotionCalculator {
	
	protected MoleculeForceAndMotionDataSet m_moleculeDataSet;
	
	public AbstractVerletAlgorithm( MoleculeForceAndMotionDataSet moleculeDataSet ) {

		m_moleculeDataSet = moleculeDataSet;
	}
}
