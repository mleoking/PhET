/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This class updates the positions of atoms in a diatomic data set (i.e.
 * where each molecule is made up of two molecules).  IMPORTANT: This class
 * assumes that they molecules are the same, e.g. O2, and not different, such
 * as OH.
 * 
 * @author John Blanco
 */
public class DiatomicAtomPositionUpdater implements AtomPositionUpdater {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final double BONDED_PARTICLE_DISTANCE = 0.9;  // In particle diameters.
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public void updateAtomPositions( MoleculeForceAndMotionDataSet moleculeDataSet ) {
		
		// Make sure this is not being used on an inappropriate data set.
		assert moleculeDataSet.getAtomsPerMolecule() == 2;
		
		// Get direct references to the data in the data set.
		Point2D [] atomPositions = moleculeDataSet.getAtomPositions();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		double [] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
		
        double xPos, yPos, cosineTheta, sineTheta;
        
        for (int i = 0; i < moleculeDataSet.getNumberOfMolecules(); i++){
            cosineTheta = Math.cos( moleculeRotationAngles[i] );
            sineTheta = Math.sin( moleculeRotationAngles[i] );
            xPos = moleculeCenterOfMassPositions[i].getX() + cosineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            yPos = moleculeCenterOfMassPositions[i].getY() + sineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            atomPositions[i * 2].setLocation( xPos, yPos );
            xPos = moleculeCenterOfMassPositions[i].getX() - cosineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            yPos = moleculeCenterOfMassPositions[i].getY() - sineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            atomPositions[i * 2 + 1].setLocation( xPos, yPos );
        }
	}
}
