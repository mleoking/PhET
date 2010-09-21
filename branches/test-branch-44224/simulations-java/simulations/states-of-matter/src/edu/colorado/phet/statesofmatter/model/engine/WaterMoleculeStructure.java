/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

/**
 * This class is a singleton that provides information about the structure
 * of a water molecule (i.e. the spatial and angular relationships between
 * the atoms that comprise the molecule).
 * 
 * @author John Blanco
 */
public class WaterMoleculeStructure {

    private static WaterMoleculeStructure instance;
	double [] m_moleculeStructureX = new double[3];
	double [] m_moleculeStructureY = new double[3];

	/**
	 * Constructor, which is private to force it to be created via the
	 * getInstance method.
	 */
    private WaterMoleculeStructure() {
    	
		// Initialize the data that defines the molecular structure of the
		// water molecule.  This defines the distances in the x and y
    	// dimensions from the center of mass when the rotational angle is
    	// zero.
		m_moleculeStructureX[0] = 0;
		m_moleculeStructureY[0] = 0;
        m_moleculeStructureX[1] = StatesOfMatterConstants.DISTANCE_FROM_OXYGEN_TO_HYDROGEN;
        m_moleculeStructureY[1] = 0;
        m_moleculeStructureX[2] = 
        	StatesOfMatterConstants.DISTANCE_FROM_OXYGEN_TO_HYDROGEN*Math.cos( StatesOfMatterConstants.THETA_HOH );
        m_moleculeStructureY[2] = 
        	StatesOfMatterConstants.DISTANCE_FROM_OXYGEN_TO_HYDROGEN*Math.sin( StatesOfMatterConstants.THETA_HOH );
        double xcm0 = (m_moleculeStructureX[0]+0.25*m_moleculeStructureX[1]+0.25*m_moleculeStructureX[2])/1.5;
        double ycm0 = (m_moleculeStructureY[0]+0.25*m_moleculeStructureY[1]+0.25*m_moleculeStructureY[2])/1.5;
        for (int i = 0; i < 3; i++){
        	m_moleculeStructureX[i] -= xcm0;
        	m_moleculeStructureY[i] -= ycm0;
        }
    }

    public static WaterMoleculeStructure getInstance() {
        if ( instance == null ) {
            instance = new WaterMoleculeStructure();
        }
        return instance;
    }
    
    public double [] getStructureArrayX(){
    	return m_moleculeStructureX;
    }

    public double [] getStructureArrayY(){
    	return m_moleculeStructureY;
    }
    
    public double getRotationalInertia(){
        double rotationalInertia = (Math.pow(m_moleculeStructureX[0], 2) + Math.pow(m_moleculeStructureY[0], 2))
            + 0.25 * (Math.pow(m_moleculeStructureX[1], 2) + Math.pow(m_moleculeStructureY[1], 2))
            + 0.25 * (Math.pow(m_moleculeStructureX[2], 2) + Math.pow(m_moleculeStructureY[2], 2));
        
        return rotationalInertia;
    }
}
