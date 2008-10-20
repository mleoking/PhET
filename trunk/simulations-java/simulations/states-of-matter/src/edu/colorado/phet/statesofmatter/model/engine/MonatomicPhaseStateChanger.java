/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

/**
 * This class is used to change the phase state (i.e. solid, liquid, or gas)
 * for a set of molecules.
 * 
 * @author John Blanco
 */
public class MonatomicPhaseStateChanger extends AbstractPhaseStateChanger {

	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public MonatomicPhaseStateChanger( MultipleParticleModel2 model ) {
		super(model);
	}

	//----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
	
	public void setPhase(int phaseID) {
		switch (phaseID){
		case PhaseStateChanger.PHASE_SOLID:
			setPhaseSolid();
			break;
		case PhaseStateChanger.PHASE_LIQUID:
			break;
		case PhaseStateChanger.PHASE_GAS:
			break;
		}
	}
	
	/**
	 * Set the phase to the solid state.
	 */
	private void setPhaseSolid(){

		// Set the temperature in the model.
		m_model.setTemperature(SOLID_TEMPERATURE);
    	
		// Create the solid form, a.k.a a crystal.
		
		int numberOfAtoms = m_model.getMoleculeDataSetRef().getNumberOfAtoms();
		Point2D [] atomPositions = m_model.getMoleculeDataSetRef().getAtomPositions();
		Vector2D [] moleculeVelocities = m_model.getMoleculeDataSetRef().getMoleculeVelocities();
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int particlesPerLayer = (int)Math.round( Math.sqrt( numberOfAtoms ) );

        double startingPosX = (m_model.getNormalizedContainerWidth() / 2) - (double)(particlesPerLayer / 2) - 
                ((particlesPerLayer / 2) * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
        double startingPosY = 2.0 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL;
        
        int particlesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; particlesPlaced < numberOfAtoms; i++){ // One iteration per layer.
            for (int j = 0; (j < particlesPerLayer) && (particlesPlaced < numberOfAtoms); j++){
                xPos = startingPosX + j + (j * DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL);
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL) / 2;
                }
                yPos = startingPosY + (double)i * (1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL)* 0.7071;
                atomPositions[(i * particlesPerLayer) + j].setLocation( xPos, yPos );
                particlesPlaced++;

                // Assign each particle an initial velocity.
                moleculeVelocities[(i * particlesPerLayer) + j].setComponents( temperatureSqrt * rand.nextGaussian(), 
                        temperatureSqrt * rand.nextGaussian() );
            }
        }
	}
}
