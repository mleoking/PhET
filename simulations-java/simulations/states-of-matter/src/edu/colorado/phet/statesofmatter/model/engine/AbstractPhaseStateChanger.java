/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

/**
 * This is the base class for the objects that directly change the state of
 * the molecules within the multi-particle simulation.
 * 
 * @author John Blanco
 *
 */
public abstract class AbstractPhaseStateChanger implements PhaseStateChanger {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    protected static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.12;  // In particle diameters.
	protected static final int MAX_PLACEMENT_ATTEMPTS = 500; // For random placement of particles.
	protected static final double MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE = 2.5;

	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	protected MultipleParticleModel2 m_model;
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public AbstractPhaseStateChanger( MultipleParticleModel2 model ) {
		m_model = model;
	}

	//----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
	
	public void setPhase(int phaseID) {
		// Stubbed in base class. 
	}

	//----------------------------------------------------------------------------
    // Private and Protected Methods
    //----------------------------------------------------------------------------
    /**
     * Does a linear search for a location that is suitably far away enough
     * from all other molecules.  This is generally used when the attempt to
     * place a molecule at a random location fails.  This is expensive in
     * terms of computational power, and should thus be used sparingly.
     * 
     * @return
     */
    protected Point2D findOpenMoleculeLocation(){
        
        double posX, posY;
        double minInitialInterParticleDistance;
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
        
        if (moleculeDataSet.getAtomsPerMolecule() == 1){
        	minInitialInterParticleDistance = 1.2;
        }
        else{
        	minInitialInterParticleDistance = 2.0;
        }
        
        double rangeX = m_model.getNormalizedContainerWidth() - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        double rangeY = m_model.getNormalizedContainerHeight() - (2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE);
        for (int i = 0; i < rangeX / minInitialInterParticleDistance; i++){
            for (int j = 0; j < rangeY / minInitialInterParticleDistance; j++){
                posX = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (i * minInitialInterParticleDistance);
                posY = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + (j * minInitialInterParticleDistance);
                
                // See if this position is available.
                boolean positionAvailable = true;
                for (int k = 0; k < moleculeDataSet.getNumberOfMolecules(); k++){
                    if (moleculeCenterOfMassPositions[k].distance( posX, posY ) < minInitialInterParticleDistance){
                        positionAvailable = false;
                        break;
                    }
                }
                if (positionAvailable){
                    // We found an open position.
                    return new Point2D.Double( posX, posY );
                }
            }
        }
        return null;
    }
}
