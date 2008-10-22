/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * Implementation of the Verlet algorithm for simulating molecular interaction
 * based on the Lennard-Jones potential - monatomic (i.e. on atom per
 * molecule) version.
 * 
 * @author John Blanco
 */
public class MonatomicVerletAlgorithm extends AbstractVerletAlgorithm {

	public MonatomicVerletAlgorithm( MoleculeForceAndMotionDataSet moleculeDataSet ){
		super( moleculeDataSet );
	}
	
	public double getPressure() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateForcesAndMotion() {
		// TODO Auto-generated method stub

	}
}
