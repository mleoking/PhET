/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class is used to change the phase state (i.e. solid, liquid, or gas)
 * for a set of molecules.
 * 
 * @author John Blanco
 */
public class MonatomicPhaseStateChanger extends AbstractPhaseStateChanger {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double MIN_INITIAL_INTER_PARTICLE_DISTANCE = 1.12;
	
    private final MonatomicAtomPositionUpdater m_positionUpdater = 
		new MonatomicAtomPositionUpdater();
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public MonatomicPhaseStateChanger( MultipleParticleModel model ) {
		super(model);
	}

	//----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
	
	public void setPhase(int phaseID) {
		switch (phaseID){
		case PhaseStateChanger.PHASE_SOLID:
			formAtomsIntoCube( MultipleParticleModel.SOLID_TEMPERATURE );
			break;
		case PhaseStateChanger.PHASE_LIQUID:
			formAtomsIntoBlob( MultipleParticleModel.LIQUID_TEMPERATURE );
			break;
		case PhaseStateChanger.PHASE_GAS:
			formAtomsIntoBlob( MultipleParticleModel.GAS_TEMPERATURE );
			break;
		}

		MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
		
        // Assume that we've done our job correctly and that all the atoms are
        // in safe positions.
        m_model.getMoleculeDataSetRef().setNumberOfSafeMolecules( moleculeDataSet.getNumberOfMolecules() );
        
        // Sync up the atom positions with the molecule positions.
        m_positionUpdater.updateAtomPositions( moleculeDataSet );
	}
	
	/**
	 * Form the atoms into a cube shape near the bottom of the container and
	 * set the temperature to the provided value.
	 * 
	 * @param temperature
	 */
	private void formAtomsIntoCube(double temperature){
		
		// Set the temperature in the model.
		m_model.setTemperature( temperature );
    	
		// Create the cube, a.k.a. the crystal.
		
		int numberOfAtoms = m_model.getMoleculeDataSetRef().getNumberOfAtoms();
		Point2D [] moleculeCenterOfMassPositions = m_model.getMoleculeDataSetRef().getMoleculeCenterOfMassPositions();
		Vector2D [] moleculeVelocities = m_model.getMoleculeDataSetRef().getMoleculeVelocities();
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int atomsPerLayer = (int)Math.round( Math.sqrt( numberOfAtoms ) );

        // Establish the starting position, which will be the lower left corner
        // of the "cube".
        double crystalWidth = (atomsPerLayer - 1) * MIN_INITIAL_INTER_PARTICLE_DISTANCE;
        double startingPosX = (m_model.getNormalizedContainerWidth() / 2) - (crystalWidth / 2);
        double startingPosY = MIN_INITIAL_INTER_PARTICLE_DISTANCE;

        // Move the atoms into their new locations and assign a velocity to each.
        
        int particlesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; particlesPlaced < numberOfAtoms; i++){ // One iteration per layer.
            for (int j = 0; (j < atomsPerLayer) && (particlesPlaced < numberOfAtoms); j++){
                xPos = startingPosX + (j * MIN_INITIAL_INTER_PARTICLE_DISTANCE);
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += MIN_INITIAL_INTER_PARTICLE_DISTANCE / 2;
                }
                yPos = startingPosY + (double)i * MIN_INITIAL_INTER_PARTICLE_DISTANCE * 0.866;
                moleculeCenterOfMassPositions[(i * atomsPerLayer) + j].setLocation( xPos, yPos );
                particlesPlaced++;

                // Assign each particle an initial velocity.
                moleculeVelocities[(i * atomsPerLayer) + j].setComponents( temperatureSqrt * rand.nextGaussian(), 
                        temperatureSqrt * rand.nextGaussian() );
            }
        }
	}

	/**
	 * Form the atoms into a blob that sits toward the bottom of the
	 * container and assign them the specified temperature.
	 * 
	 * @param temperature
	 */
	private void formAtomsIntoBlob( double temperature ){
		
		m_model.setTemperature( temperature );
        double temperatureSqrt = Math.sqrt( MultipleParticleModel.LIQUID_TEMPERATURE );
        
        // Set the initial velocity for each of the atoms based on the new
        // temperature.

		int numberOfAtoms = m_model.getMoleculeDataSetRef().getNumberOfAtoms();
		Point2D [] moleculeCenterOfMassPositions = m_model.getMoleculeDataSetRef().getMoleculeCenterOfMassPositions();
		Vector2D [] moleculeVelocities = m_model.getMoleculeDataSetRef().getMoleculeVelocities();
        Random rand = new Random();
		for (int i = 0; i < numberOfAtoms; i++){
            // Assign each particle an initial velocity.
			moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
                    temperatureSqrt * rand.nextGaussian() );
        }
        
        // Assign each atom to a position.
        
        int atomsPlaced = 0;
        
        Point2D centerPoint = new Point2D.Double(m_model.getNormalizedContainerWidth() / 2, 
        		m_model.getNormalizedContainerHeight() / 4);
        int currentLayer = 0;
        int particlesOnCurrentLayer = 0;
        int particlesThatWillFitOnCurrentLayer = 1;
        
        for (int j = 0; j < numberOfAtoms; j++){
            
            for (int k = 0; k < MAX_PLACEMENT_ATTEMPTS; k++){
                
                double distanceFromCenter = currentLayer * MIN_INITIAL_INTER_PARTICLE_DISTANCE;
                double angle = ((double)particlesOnCurrentLayer / (double)particlesThatWillFitOnCurrentLayer * 2 * Math.PI) +
                        ((double)particlesThatWillFitOnCurrentLayer / (4 * Math.PI));
                double xPos = centerPoint.getX() + (distanceFromCenter * Math.cos( angle ));
                double yPos = centerPoint.getY() + (distanceFromCenter * Math.sin( angle ));
                particlesOnCurrentLayer++;  // Consider this spot used even if we don't actually put the
                                            // particle there.
                if (particlesOnCurrentLayer >= particlesThatWillFitOnCurrentLayer){
                    
                    // This layer is full - move to the next one.
                    currentLayer++;
                    particlesThatWillFitOnCurrentLayer = 
                        (int)( currentLayer * 2 * Math.PI / MIN_INITIAL_INTER_PARTICLE_DISTANCE );
                    particlesOnCurrentLayer = 0;
                }

                // Check if the position is too close to the wall.  Note
                // that we don't check inter-particle distances here - we
                // rely on the placement algorithm to make sure that we don't
                // run into problems with this.
                if ((xPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_model.getNormalizedContainerWidth() - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (yPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_model.getNormalizedContainerHeight() - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE)){
                    
                    // This is an acceptable position.
                    moleculeCenterOfMassPositions[atomsPlaced++].setLocation( xPos, yPos );
                    break;
                }
            }
        }
	}
}
