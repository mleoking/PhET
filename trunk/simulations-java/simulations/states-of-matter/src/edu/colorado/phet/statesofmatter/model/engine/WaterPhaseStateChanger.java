/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class is used to change the phase state (i.e. solid, liquid, or gas)
 * for a set of water molecules.  It only works for water and would need to be
 * generalized to handle other triatomic molecules.
 * 
 * @author John Blanco
 */
public class WaterPhaseStateChanger extends AbstractPhaseStateChanger {
	
	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double MIN_INITIAL_DIAMETER_DISTANCE = 1.4;
    
	// The following constants can be adjusted to make the the corresponding
	// phase more or less dense.
	private static final double LIQUID_SPACING_FACTOR = 0.8;
	private static final double GAS_SPACING_FACTOR = 1.0;
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private final AtomPositionUpdater m_positionUpdater = new WaterAtomPositionUpdater();
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public WaterPhaseStateChanger(MultipleParticleModel model) {
		super(model);
		
		// Make sure this is not being used on an inappropriate data set.
		assert m_model.getMoleculeDataSetRef().getAtomsPerMolecule() == 3;
	}
	
	//----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
	
	public void setPhase(int phaseID) {
		switch (phaseID){
		case PhaseStateChanger.PHASE_SOLID:
			formMoleculesIntoCube( MultipleParticleModel.SOLID_TEMPERATURE );
			break;
		case PhaseStateChanger.PHASE_LIQUID:
			formMoleculesIntoBlob( MultipleParticleModel.LIQUID_TEMPERATURE );
			break;
		case PhaseStateChanger.PHASE_GAS:
			formMoleculesIntoBlob( MultipleParticleModel.GAS_TEMPERATURE );
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
	 * Form the molecules into a cube shape near the bottom of the container
	 * and set the temperature to the provided value.
	 * 
	 * @param temperature
	 */
	private void formMoleculesIntoCube( double temperature ){

		// Set the model temperature for this phase.
        m_model.setTemperature( temperature );
        
        // Get references to the various elements of the data set.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
		int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		Vector2D [] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		double [] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
		double [] moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();
		
		// Create and initialize other variables needed to do the job.
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int moleculesPerLayer = (int)Math.sqrt( numberOfMolecules );

        // Initialize the velocities and angles of the molecules.
        for (int i = 0; i < numberOfMolecules; i++){

            // Assign each molecule an initial velocity.
            moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
                    temperatureSqrt * rand.nextGaussian() );
            
            // Assign each molecule an initial rotation rate.
            moleculeRotationRates[i] = rand.nextDouble() * temperatureSqrt * Math.PI * 2;
        }
        
        // Establish the starting position, which will be the lower left corner
        // of the "cube".
        double crystalWidth = (moleculesPerLayer - 1) * MIN_INITIAL_DIAMETER_DISTANCE;
        
        double startingPosX = (m_model.getNormalizedContainerWidth() / 2) - (crystalWidth / 2);
        double startingPosY = MIN_INITIAL_DIAMETER_DISTANCE;
        
        // Place the molecules by placing their centers of mass.
        
        int moleculesPlaced = 0;
        double xPos, yPos;
        for (int i = 0; i < numberOfMolecules; i++){ // One iteration per layer.
            for (int j = 0; (j < moleculesPerLayer) && (moleculesPlaced < numberOfMolecules); j++){
                xPos = startingPosX + (j * MIN_INITIAL_DIAMETER_DISTANCE);
                if (i % 2 != 0){
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += MIN_INITIAL_DIAMETER_DISTANCE / 2;
                }
                yPos = startingPosY + ((double)i * MIN_INITIAL_DIAMETER_DISTANCE * 0.866);
                moleculeCenterOfMassPositions[(i * moleculesPerLayer) + j].setLocation( xPos, yPos );
                moleculeRotationAngles[(i * moleculesPerLayer) + j] = rand.nextDouble() * 2 * Math.PI;
                
                moleculesPlaced++;
            }
        }
	}
	
	/**
	 * Form the molecules into a blob that sits toward the bottom of the
	 * container and assign them the specified temperature.
	 * 
	 * @param temperature
	 */
	private void formMoleculesIntoBlob( double temperature ){

		// Set the model temperature for this phase.
		m_model.setTemperature( temperature );

        // Get references to the various elements of the data set.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		Vector2D [] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		double [] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
		double [] moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();
		
		// Create and initialize other variables needed to do the job.
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();

        // Initialize the velocities and angles of the molecules.
        for (int i = 0; i < numberOfMolecules; i++){

            // Assign each molecule an initial velocity.
            moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(), 
                    temperatureSqrt * rand.nextGaussian() );
            
            // Assign each molecule an initial rotation rate.
            moleculeRotationRates[i] = rand.nextDouble() * temperatureSqrt * Math.PI * 2;
        }
        
        // Assign each molecule to a position.
        
        int moleculesPlaced = 0;
        
        Point2D centerPoint = new Point2D.Double(m_model.getNormalizedContainerWidth() / 2,
        		m_model.getNormalizedContainerHeight() / 4);
        int currentLayer = 0;
        int particlesOnCurrentLayer = 0;
        int particlesThatWillFitOnCurrentLayer = 1;
        
        for (int i = 0; i < numberOfMolecules; i++){
            
            for (int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++){
                
                double distanceFromCenter = currentLayer * MIN_INITIAL_DIAMETER_DISTANCE * LIQUID_SPACING_FACTOR;
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
                        (int)( currentLayer * 2 * Math.PI / (MIN_INITIAL_DIAMETER_DISTANCE * LIQUID_SPACING_FACTOR) );
                    particlesOnCurrentLayer = 0;
                }

                // Check if the position is too close to the wall.  Note
                // that we don't check inter-particle distances here - we rely
                // on the placement algorithm to make sure that this is not a
                // problem.
                if ((xPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_model.getNormalizedContainerWidth() - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (yPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE) &&
                    (xPos < m_model.getNormalizedContainerHeight() - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE)){
                    
                    // This is an acceptable position.
                    moleculeCenterOfMassPositions[moleculesPlaced].setLocation( xPos, yPos );
                    moleculeRotationAngles[moleculesPlaced] = angle + Math.PI / 2;
                    moleculesPlaced++;
                    break;
                }
            }
        }
	}
}
