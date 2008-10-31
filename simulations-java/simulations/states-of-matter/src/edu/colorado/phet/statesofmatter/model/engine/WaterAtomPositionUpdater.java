/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This class updates the positions of atoms in a water molecule based on the
 * position and rotation information for the molecule.
 * 
 * @author John Blanco
 */
public class WaterAtomPositionUpdater implements AtomPositionUpdater {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final double BONDED_PARTICLE_DISTANCE = 0.9;  // In particle diameters.
	
	// These constants describe the relationship from the centers of mass to the
	// individual atoms.
	private static final double DISTANCE_FROM_OXYGEN_TO_HYDROGEN = 1.0 / 3.12;  // Number supplied by Paul Beale.
	private static final double [] COM_TO_ATOM_RELATIONSHIPS_X = {0, DISTANCE_FROM_OXYGEN_TO_HYDROGEN,
		DISTANCE_FROM_OXYGEN_TO_HYDROGEN * Math.cos( StatesOfMatterConstants.THETA_HOH )};
	private static final double [] COM_TO_ATOM_RELATIONSHIPS_Y = {0, 0,
		DISTANCE_FROM_OXYGEN_TO_HYDROGEN * Math.sin( StatesOfMatterConstants.THETA_HOH )};
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public void updateAtomPositions( MoleculeForceAndMotionDataSet moleculeDataSet ) {
		
		// Make sure this is not being used on an inappropriate data set.
		assert moleculeDataSet.getAtomsPerMolecule() == 3;
		
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
        
        for (int i = 0; i < moleculeDataSet.getNumberOfMolecules(); i++){
            cosineTheta = Math.cos( moleculeRotationAngles[i] );
            sineTheta = Math.sin( moleculeRotationAngles[i] );
            for (int j = 0; j < 3; j++){
                xPos = moleculeCenterOfMassPositions[i].getX() + cosineTheta * COM_TO_ATOM_RELATIONSHIPS_X[j] - 
                        sineTheta * COM_TO_ATOM_RELATIONSHIPS_Y[j];
                yPos = moleculeCenterOfMassPositions[i].getY() + sineTheta * COM_TO_ATOM_RELATIONSHIPS_X[j] + 
                        cosineTheta * COM_TO_ATOM_RELATIONSHIPS_Y[j];
                atomPositions[i * 3 + j].setLocation( xPos, yPos );
            }
        }
	}
}
